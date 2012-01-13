/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.exception;

import org.junit.Ignore;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

@Ignore
public class ExceptionMessageProcessorChainTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "exception-message-processor-chain-config.xml";
    }

    public void testExceptionMessageProcessorChain() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("vm://MyFlowRequest", "some data", null);
        assertEquals("Sad Path", result.getPayloadAsString());
    }
    
}
