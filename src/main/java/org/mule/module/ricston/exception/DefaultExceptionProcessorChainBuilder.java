/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.exception;

import org.mule.api.MuleException;
import org.mule.api.processor.InterceptingMessageProcessor;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorChain;
import org.mule.processor.chain.DefaultMessageProcessorChain;
import org.mule.processor.chain.DefaultMessageProcessorChainBuilder;
import org.mule.processor.chain.InterceptingChainLifecycleWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class DefaultExceptionProcessorChainBuilder extends DefaultMessageProcessorChainBuilder {

    @Override
    public MessageProcessorChain build() throws MuleException {
        LinkedList<MessageProcessor> tempList = new LinkedList<MessageProcessor>();

        // Start from last but one message processor and work backwards
        for (int i = processors.size() - 1; i >= 0; i--) {
            MessageProcessor processor = initializeMessageProcessor(processors.get(i));
            if (processor instanceof InterceptingMessageProcessor) {
                InterceptingMessageProcessor interceptingProcessor = (InterceptingMessageProcessor) processor;
                // Processor is intercepting so we can't simply iterate
                if (i + 1 < processors.size()) {
                    // The current processor is not the last in the list
                    if (tempList.isEmpty()) {
                        interceptingProcessor.setListener(initializeMessageProcessor(processors.get(i + 1)));
                    } else if (tempList.size() == 1) {
                        interceptingProcessor.setListener(tempList.get(0));
                    } else {
                        final DefaultMessageProcessorChain chain = new ExceptionMessageProcessorChain(
                                "(inner iterating chain) of " + name, new ArrayList<MessageProcessor>(tempList));
                        interceptingProcessor.setListener(chain);
                    }
                }
                tempList = new LinkedList<MessageProcessor>(Collections.singletonList(processor));
            } else {
                // Processor is not intercepting so we can invoke it using iteration
                // (add to temp list)
                tempList.addFirst(initializeMessageProcessor(processor));
            }
        }
        // Create the final chain using the current tempList after reserve iteration is complete. This temp
        // list contains the first n processors in the chain that are not intercepting.. with processor n+1
        // having been injected as the listener of processor n
        final DefaultMessageProcessorChain chain = new ExceptionMessageProcessorChain(name,
                new ArrayList<MessageProcessor>(tempList));

        // Wrap with something that can apply lifecycle to all processors which are otherwise not visable from
        // DefaultMessageProcessorChain
        return new InterceptingChainLifecycleWrapper(chain, processors, "wrapper for " + name);
    }

}
