package org.mule.module.ricston.config;

import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorBuilder;
import org.mule.config.spring.factories.MessageProcessorChainFactoryBean;

public class ExceptionMessageProcessorChainFactoryBean extends MessageProcessorChainFactoryBean
{

    public Object getObject() throws Exception
    {
        ExceptionMessageProcessorChainBuilder builder = new ExceptionMessageProcessorChainBuilder();
        builder.setName(name);
        for (Object processor : processors)
        {
            if (processor instanceof MessageProcessor)
            {
                builder.chain((MessageProcessor) processor);
            }
            else if (processor instanceof MessageProcessorBuilder)
            {
                builder.chain((MessageProcessorBuilder) processor);
            }
            else
            {
                throw new IllegalArgumentException(
                    "MessageProcessorBuilder should only have MessageProcessor's or MessageProcessorBuilder's configured");
            }
        }
        return builder.build();
    }

}
