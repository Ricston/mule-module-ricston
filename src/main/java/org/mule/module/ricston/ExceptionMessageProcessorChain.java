/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import java.util.ArrayList;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstructAware;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.api.processor.MessageProcessor;
import org.mule.message.ExceptionMessage;
import org.mule.processor.chain.DefaultMessageProcessorChain;

public class ExceptionMessageProcessorChain extends
		DefaultMessageProcessorChain implements Lifecycle, FlowConstructAware {

	public ExceptionMessageProcessorChain(String string,
			ArrayList<MessageProcessor> arrayList) {
		super(string, arrayList);
	}

	public MuleEvent process(MuleEvent event) throws MuleException {
		MuleEvent result = null;
		try {
			result = processNext(event);
		} catch (MuleException e) {
			logger.info("Caught Exception");
			event.getMessage().setPayload(
					new ExceptionMessage(event, e, event.getFlowConstruct()
							.getName(), event.getMessageSourceURI()));
			logger.info("Invoking processor chain");
			result = doProcess(event);
			logger.info("Got result from processor chain="
					+ result.getMessageAsString());
		}
		return result;
	}
}