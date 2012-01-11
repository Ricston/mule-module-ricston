/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import org.mule.util.xa.AbstractXAResourceManager;
import org.mule.util.xa.DefaultXASession;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.util.Map;

public class ObjectStoreXASession extends DefaultXASession {

    protected Map.Entry<Serializable, Serializable> object;

    @Override
    public Xid[] recover(int flag) throws XAException {

        try {
            ((ObjectStoreXAResourceManager) resourceManager).recover();
        } catch (Exception e) {
            throw new XAException(XAException.XAER_RMERR);
        }

        return ((ObjectStoreXAResourceManager) resourceManager).getPreparedTransactions();
    }

    public ObjectStoreXASession(AbstractXAResourceManager resourceManager, Map.Entry<Serializable, Serializable> object) {
        super(resourceManager);
        this.object = object;
    }

    public ObjectStoreXASession(AbstractXAResourceManager resourceManager) {
        super(resourceManager);
    }

    public Map.Entry<Serializable, Serializable> getObject() {
        return object;
    }

    public void setObject(Map.Entry<Serializable, Serializable> object) {
        this.object = object;
    }

    public Xid getXid() {
        return localXid;
    }
}
