/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.objectstore;

import org.junit.Test;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.transaction.Transaction;
import org.mule.tck.functional.EventCallback;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transaction.TransactionCoordination;
import org.mule.transport.NullPayload;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import static org.junit.Assert.*;

public class TransactionAwareObjectStoreTestCase extends FunctionalTestCase {

    private MuleClient client;
    private DefaultMuleMessage messageFoo;
    private DefaultMuleMessage messageFooDuplicate;
    private DefaultMuleMessage messageBar;

    protected String getConfigResources() {
        return "transaction-aware-object-store-config.xml";
    }

    @Override
    public void doSetUp() throws Exception {
        client = muleContext.getClient();
        messageFoo = new DefaultMuleMessage("some data", muleContext);
        messageFooDuplicate = messageFoo;
        messageBar = new DefaultMuleMessage("some data", muleContext);

        super.doSetUp();
    }

    @Test
    public void testAbort() throws Exception {
        getFunctionalTestComponent("main").setEventCallback(new EventCallback() {
            @Override
            public void eventReceived(MuleEventContext context, Object component) throws Exception {
                throw new RuntimeException();

            }
        });

        assertNull(sendMessage("vm://in", "vm://out", messageFoo));

        getFunctionalTestComponent("main").setEventCallback(null);

        MuleMessage result = sendMessage("vm://in", "vm://out", messageFooDuplicate);
        assertValidMessage(result);
    }

    @Test
    public void testCommit() throws Exception {
        MuleMessage result = sendMessage("vm://in", "vm://out", messageFoo);
        assertValidMessage(result);

        MuleMessage secondResult = sendMessage("vm://in", "vm://out", messageBar);
        assertValidMessage(secondResult);

        assertNull(sendMessage("vm://in", "vm://out", messageFooDuplicate));
    }

    @Test
    public void testAbortInPreparePhase() throws Exception {

        getFunctionalTestComponent("main").setEventCallback(new EventCallback() {
            @Override
            public void eventReceived(MuleEventContext context, Object component) throws Exception {
                Transaction tx = TransactionCoordination.getInstance().getTransaction();
                tx.bindResource("", new XAResource() {
                    @Override
                    public void commit(Xid xid, boolean b) throws XAException {

                    }

                    @Override
                    public void end(Xid xid, int i) throws XAException {

                    }

                    @Override
                    public void forget(Xid xid) throws XAException {

                    }

                    @Override
                    public int getTransactionTimeout() throws XAException {
                        return 0;
                    }

                    @Override
                    public boolean isSameRM(XAResource xaResource) throws XAException {
                        return false;
                    }

                    @Override
                    public int prepare(Xid xid) throws XAException {
                        throw new XAException(XAException.XAER_RMERR);
                    }

                    @Override
                    public Xid[] recover(int i) throws XAException {
                        return null;
                    }

                    @Override
                    public void rollback(Xid xid) throws XAException {

                    }

                    @Override
                    public boolean setTransactionTimeout(int i) throws XAException {
                        return true;
                    }

                    @Override
                    public void start(Xid xid, int i) throws XAException {

                    }
                });

            }
        });

        assertNull(sendMessage("vm://in", "vm://out", messageFoo));

        getFunctionalTestComponent("main").setEventCallback(null);

        MuleMessage result = sendMessage("vm://in", "vm://out", messageFooDuplicate);
        assertValidMessage(result);
    }

    private void assertValidMessage(MuleMessage muleMessage) throws Exception {
        assertNotNull(muleMessage);
        assertNull(muleMessage.getExceptionPayload());
        assertFalse(muleMessage.getPayload() instanceof NullPayload);
        assertEquals("some data Received", muleMessage.getPayloadAsString());
    }

    private MuleMessage sendMessage(String inboundEndpoint, String outboundEndpoint, MuleMessage message) throws Exception {
        client.dispatch(inboundEndpoint, message);
        return client.request(outboundEndpoint, RECEIVE_TIMEOUT);
    }


}
