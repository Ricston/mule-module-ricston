package org.mule.module.ricston;

import org.mule.util.xa.AbstractTransactionContext;
import org.mule.util.xa.ResourceManagerException;

import java.io.Serializable;
import java.util.Map;


public class ObjectStoreTransactionContext extends AbstractTransactionContext {

    private Map.Entry<Serializable, Serializable> object;

    public Map.Entry<Serializable, Serializable> getObject() {
        return object;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ObjectStoreTransactionContext(Map.Entry<Serializable, Serializable> object) {
        super();
        this.object = object;
    }

    @Override
    public void doCommit() throws ResourceManagerException {

    }

    @Override
    public void doRollback() throws ResourceManagerException {

    }

}
