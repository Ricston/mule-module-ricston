/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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
