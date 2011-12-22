/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleMessageCollection;
import org.mule.api.routing.RoutingException;
import org.mule.routing.outbound.MulticastingRouter;

public class IgnoreReplyMulticastingRouter extends MulticastingRouter
{
    
    @Override
    public MuleEvent route(MuleEvent event) throws RoutingException
    {
        //user multicasting router to do the routing
        MuleEvent resultEvent = super.route(event);
        
        //test for exceptions
        if (resultEvent.getMessage() instanceof MuleMessageCollection)
        {
	    	MuleMessageCollection messageCollection = (MuleMessageCollection) resultEvent.getMessage();
	    	
	    	for(MuleMessage m : messageCollection.getMessagesAsArray())
	    	{
	    		throwExceptionIfExceptionMessageIsPresent(m, event);
	    	}
        }
        else
        {
        	MuleMessage resultMessage = resultEvent.getMessage();
        	throwExceptionIfExceptionMessageIsPresent(resultMessage, event);
        }
        
        //return the original message/event instead of the result from the multicasting router
        return event;
    }
    
    //only continue if the payload does not contain an exception
    @Override
    protected boolean continueRoutingMessageAfter(MuleEvent response)
    		throws MuleException 
    {
    	return (response.getMessage().getExceptionPayload() == null);
    }
    
    protected void throwExceptionIfExceptionMessageIsPresent(MuleMessage message, MuleEvent event) throws RoutingException
    {
    	if (message.getExceptionPayload() != null)
		{
			throw new RoutingException(event, this, message.getExceptionPayload().getException());
		}
    }

}


