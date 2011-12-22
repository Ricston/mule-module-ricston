/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.routing.RoutingException;
import org.mule.routing.outbound.OutboundPassThroughRouter;

public class IgnoreReplyPassThroughRouter extends OutboundPassThroughRouter
{
    
    @Override
    public MuleEvent route(MuleEvent event) throws RoutingException
    {
        MuleMessage clonedMessage = cloneMessage(event.getMessage());
        MuleEvent resultEvent = super.route(event);
        
        if (resultEvent != null && resultEvent.getMessage() != null && resultEvent.getMessage().getExceptionPayload() != null)
        {
        	throw new RoutingException(event, this, resultEvent.getMessage().getExceptionPayload().getException());
        }
        
        return new DefaultMuleEvent(clonedMessage, event);
    }

}


