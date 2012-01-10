/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleProperties;
import org.mule.api.store.ListableObjectStore;
import org.mule.api.store.ObjectStore;
import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.ObjectStoreManager;
import org.mule.util.Base64;
import org.mule.util.xa.AbstractTransactionContext;
import org.mule.util.xa.AbstractXAResourceManager;
import org.mule.util.xa.ResourceManagerException;
import org.mule.util.xa.ResourceManagerSystemException;

import javax.transaction.Status;
import javax.transaction.xa.Xid;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectStoreXAResourceManager extends AbstractXAResourceManager {

    private static final int KEY_RECORD_INDEX = 0;
    private static final int VALUE_RECORD_INDEX = 1;
    private static final int XID_RECORD_INDEX = 2;

    private TransactionAwareObjectStore objectStore;
    private MuleContext muleContext;
    private List<Xid> preparedTransactions;
    private ListableObjectStore<Serializable> transactionLog;

    public ObjectStore getTransactionLog() {
        return transactionLog;
    }

    public MuleContext getMuleContext() {
        return muleContext;
    }

    public void setMuleContext(MuleContext muleContext) {
        this.muleContext = muleContext;
    }

    public TransactionAwareObjectStore getObjectStore() {
        return objectStore;
    }

    public ObjectStoreXAResourceManager(TransactionAwareObjectStore objectStore, MuleContext muleContext) throws ObjectStoreException {
        super();
        preparedTransactions = new ArrayList<Xid>();
        this.objectStore = objectStore;
        this.muleContext = muleContext;
        transactionLog = ((ObjectStoreManager) muleContext.getRegistry()
                .get(MuleProperties.OBJECT_STORE_MANAGER))
                .getObjectStore("transaction-log", true);

        transactionLog.open();
    }

    @Override
    protected AbstractTransactionContext createTransactionContext(Object session) {
        return new ObjectStoreTransactionContext(((ObjectStoreXASession) session).getObject());
    }

    @Override
    protected void doBegin(AbstractTransactionContext context) {

    }

    @Override
    protected int doPrepare(AbstractTransactionContext context) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            Map.Entry<Serializable, Serializable> obj = ((ObjectStoreTransactionContext) context).getObject();
            Xid xid = getXid(context);

            objectOutputStream.writeObject(xid);
            objectOutputStream.close();

            objectStore.lockStore();
            transactionLog.store(encode(xid.getGlobalTransactionId()), obj.getKey() + ":" + obj.getValue() + ":" + encode(byteArrayOutputStream.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    @Override
    public void recover() throws ResourceManagerSystemException {
        try {
            String record;

            for (Serializable key : ((ListableObjectStore<Serializable>) transactionLog).allKeys()) {
                record = (String) transactionLog.retrieve(key);
                restoreTransactionState(record.split(":"));
            }
        } catch (Exception e) {
            throw new ResourceManagerSystemException(e);
        }
    }

    public Xid[] getPreparedTransactions() {
        return preparedTransactions.toArray(new Xid[preparedTransactions.size()]);
    }

    @Override
    protected void doCommit(AbstractTransactionContext transactionContext) throws ResourceManagerException {
        ObjectStoreTransactionContext objectStoreTransactionContext = (ObjectStoreTransactionContext) transactionContext;
        try {
            objectStore.store(objectStoreTransactionContext.getObject().getKey(), objectStoreTransactionContext.getObject().getValue());
            transactionLog.remove(encode(getXid(transactionContext).getGlobalTransactionId()));
        } catch (Exception e) {
            throw new ResourceManagerSystemException(e);
        } finally {
            objectStore.unlockStore();
        }
    }

    @Override
    protected void doRollback(AbstractTransactionContext transactionContext) throws ResourceManagerException {
        Xid xid = getXid(transactionContext);

        try {
            if (xid != null) {
                String encodedXid = encode(xid.getGlobalTransactionId());

                if (transactionLog.contains(encodedXid)) {
                    transactionLog.remove(encodedXid);
                }
            }
        } catch (Exception e) {
            throw new ResourceManagerSystemException(e);
        } finally {
            objectStore.unlockStore();
        }
    }

    private Xid deserializeXid(String serializedXid) throws Exception {
        ByteArrayInputStream xidInputStream = new ByteArrayInputStream(decode(serializedXid));
        return (Xid) new ObjectInputStream(xidInputStream).readObject();
    }

    private void restoreTransactionState(final String[] record) throws Exception {

        Xid xid = deserializeXid(record[XID_RECORD_INDEX]);
        preparedTransactions.add(xid);

        Map.Entry object = new Map.Entry() {
            @Override
            public Object getKey() {
                return record[KEY_RECORD_INDEX];
            }

            @Override
            public Object getValue() {
                return record[VALUE_RECORD_INDEX];
            }

            @Override
            public Object setValue(Object o) {
                return null;
            }
        };

        ObjectStoreTransactionContext transactionContext = new ObjectStoreTransactionContext(object);
        transactionContext.setStatus(Status.STATUS_PREPARED);

        activeContexts.put(xid, transactionContext);
    }

    private Xid getXid(AbstractTransactionContext transactionContext) {

        if (activeContexts.containsValue(transactionContext)) {
            for (Object entry : activeContexts.entrySet()) {
                if (((Map.Entry) entry).getValue().equals(transactionContext)) {
                    return (Xid) ((Map.Entry) entry).getKey();
                }
            }
        }
        return null;
    }

    private String encode(byte[] source) throws Exception {
        return Base64.encodeBytes(source);
    }

    private byte[] decode(String string) {
        return Base64.decode(string);
    }

}
