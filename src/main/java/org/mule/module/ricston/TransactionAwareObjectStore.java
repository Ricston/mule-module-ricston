package org.mule.module.ricston;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.store.ListableObjectStore;
import org.mule.api.store.ObjectStoreException;
import org.mule.api.store.ObjectStoreManager;
import org.mule.api.transaction.Transaction;
import org.mule.transaction.TransactionCoordination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionAwareObjectStore<T extends Serializable> implements ListableObjectStore<T>, Startable, Initialisable, MuleContextAware {

    protected ObjectStoreXAResourceManager resourceManager;
    protected MuleContext muleContext;
    protected String storeName;
    protected boolean isPersistent;
    protected ListableObjectStore<T> store;
    protected int maxEntries = 0;
    protected int entryTTL;
    protected int expirationInterval;
    private final ReentrantLock lock = new ReentrantLock(true);

    public ObjectStoreXAResourceManager getResourceManager() {
        return resourceManager;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public void setPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    public int getEntryTTL() {
        return entryTTL;
    }

    public void setEntryTTL(int entryTTL) {
        this.entryTTL = entryTTL;
    }

    public int getExpirationInterval() {
        return expirationInterval;
    }

    public void setExpirationInterval(int expirationInterval) {
        this.expirationInterval = expirationInterval;
    }


    public TransactionAwareObjectStore() {
        this.setStoreName(MuleProperties.OBJECT_STORE_DEFAULT_PERSISTENT_NAME);
        this.setPersistent(true);
    }

    @Override
    public void initialise() throws InitialisationException {
        try {
            resourceManager = new ObjectStoreXAResourceManager(this, muleContext);
            resourceManager.start();
        } catch (Exception e) {
            throw new InitialisationException(e, this);
        }
    }

    @Override
    public void store(Serializable key, T value) throws ObjectStoreException {

        Transaction tx = TransactionCoordination.getInstance().getTransaction();

        try {
            if (tx == null || tx.getStatus() == Transaction.STATUS_COMMITTING)
                getStore().store(key, value);
            else {
                final Serializable finalId = key;
                final Serializable finalValue = value;

                ObjectStoreXASession session = new ObjectStoreXASession(resourceManager, new Map.Entry<Serializable, Serializable>() {

                    @Override
                    public Serializable getKey() {
                        return finalId;
                    }

                    @Override
                    public Serializable getValue() {
                        return finalValue;
                    }

                    @Override
                    public Serializable setValue(Serializable s) {
                        return null;
                    }
                });

                tx.bindResource(this.getClass().getName(), session);
            }

        } catch (Exception e) {
            throw new ObjectStoreException(e);
        }
    }

    public void lockStore() {
        lock.lock();
    }

    public void unlockStore() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
    public void start() throws MuleException {
        muleContext.getRegistry().registerObject("_transactionAwareStore", this);
    }

    @Override
    public void setMuleContext(MuleContext muleContext) {
        this.muleContext = muleContext;
    }


    @Override
    public void open() throws ObjectStoreException {
        ListableObjectStore<T> store = getStore();
        if (store != null) {
            store.open();
        }
    }

    @Override
    public void close() throws ObjectStoreException {
        ListableObjectStore<T> store = getStore();
        if (store != null) {
            getStore().close();
        }
    }

    @Override
    public List<Serializable> allKeys() throws ObjectStoreException {
        ListableObjectStore<T> store = getStore();
        if (store != null) {
            return store.allKeys();
        }
        return new ArrayList<Serializable>();
    }

    @Override
    public boolean contains(Serializable key) throws ObjectStoreException {
        return getStore().contains(key);
    }

    @Override
    public T retrieve(Serializable key) throws ObjectStoreException {
        return getStore().retrieve(key);
    }

    @Override
    public T remove(Serializable key) throws ObjectStoreException {
        return getStore().remove(key);
    }

    private ListableObjectStore<T> getStore() {
        if (store == null) {
            ObjectStoreManager objectStoreManager = (ObjectStoreManager) muleContext.getRegistry().lookupObject(
                    MuleProperties.OBJECT_STORE_MANAGER);
            if (objectStoreManager == null) {
                return null;
            }
            if (maxEntries != 0) {
                store = (ListableObjectStore<T>) objectStoreManager.getObjectStore(storeName, isPersistent,
                        maxEntries, entryTTL, expirationInterval);
            } else {
                store = (ListableObjectStore<T>) objectStoreManager.getObjectStore(storeName, isPersistent);
            }
        }
        return store;
    }
}


