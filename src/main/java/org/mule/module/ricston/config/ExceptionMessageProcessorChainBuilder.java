package org.mule.module.ricston.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.mule.api.MuleException;
import org.mule.api.processor.InterceptingMessageProcessor;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorChain;
import org.mule.module.ricston.exception.ExceptionMessageProcessorChain;
import org.mule.processor.chain.DefaultMessageProcessorChainBuilder;

public class ExceptionMessageProcessorChainBuilder extends DefaultMessageProcessorChainBuilder{
    
	public MessageProcessorChain build() throws MuleException
    {
        LinkedList<MessageProcessor> tempList = new LinkedList<MessageProcessor>();

        // Start from last but one message processor and work backwards
        for (int i = processors.size() - 1; i >= 0; i--)
        {
            MessageProcessor processor = initializeMessageProcessor(processors.get(i));
            if (processor instanceof InterceptingMessageProcessor)
            {
                InterceptingMessageProcessor interceptingProcessor = (InterceptingMessageProcessor) processor;
                // Processor is intercepting so we can't simply iterate
                if (i + 1 < processors.size())
                {
                    // The current processor is not the last in the list
                    if (tempList.isEmpty())
                    {
                        interceptingProcessor.setListener(initializeMessageProcessor(processors.get(i + 1)));
                    }
                    else if (tempList.size() == 1)
                    {
                        interceptingProcessor.setListener(tempList.get(0));
                    }
                    else
                    {
                        final ExceptionMessageProcessorChain chain = new ExceptionMessageProcessorChain(
                            "(inner iterating chain) of " + name, new ArrayList<MessageProcessor>(tempList));
                        interceptingProcessor.setListener(chain);
                    }
                }
                tempList = new LinkedList<MessageProcessor>(Collections.singletonList(processor));
            }
            else
            {
                tempList.addFirst(initializeMessageProcessor(processor));
            }
        }
        return new ExceptionMessageProcessorChain(name,new ArrayList<MessageProcessor>(tempList));
    }

}
