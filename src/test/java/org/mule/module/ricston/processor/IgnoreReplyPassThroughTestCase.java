/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.processor;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.NullPayload;

public class IgnoreReplyPassThroughTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "ignore-reply-pass-through-config.xml";
    }

    public void testIgnoreReply() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("vm://main.in", "some data", null);
        assertNotNull(result);
        assertNull(result.getExceptionPayload());
        assertFalse(result.getPayload() instanceof NullPayload);

        assertEquals("some data", result.getPayloadAsString());
    }
    
    public void testIgnoreReplyWithException() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("vm://main.exception.in", "some data", null);
        assertNotNull(result);
        assertNotNull(result.getExceptionPayload());
    }
}
