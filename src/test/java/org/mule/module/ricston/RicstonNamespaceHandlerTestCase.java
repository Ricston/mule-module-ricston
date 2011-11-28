/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import org.junit.Ignore;
import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;

import static org.junit.Assert.fail;

/**
 * TODO
 */
@Ignore
public class RicstonNamespaceHandlerTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        //TODO You'll need to edit this file to configure the properties specific to your module elements
        return "ricston-namespace-config.xml";
    }

    @Test
    public void testRicstonConfig() throws Exception
    {
        //TODO Assert specific properties are configured correctly
        fail("Not implemented yet");

    }
}
