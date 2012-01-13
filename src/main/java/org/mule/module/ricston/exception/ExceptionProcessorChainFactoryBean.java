/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.exception;

import org.mule.api.processor.MessageProcessor;
import org.mule.api.processor.MessageProcessorBuilder;
import org.mule.api.processor.MessageProcessors;
import org.mule.config.spring.factories.MessageProcessorChainFactoryBean;
import org.mule.processor.chain.DefaultMessageProcessorChainBuilder;

public class ExceptionProcessorChainFactoryBean extends MessageProcessorChainFactoryBean {

    @Override
    public Object getObject() throws Exception {
        DefaultMessageProcessorChainBuilder builder = new DefaultExceptionProcessorChainBuilder();
        builder.setName("processor chain '" + name + "'");
        for (Object processor : processors) {
            if (processor instanceof MessageProcessor) {
                builder.chain((MessageProcessor) processor);
            } else if (processor instanceof MessageProcessorBuilder) {
                builder.chain((MessageProcessorBuilder) processor);
            } else {
                throw new IllegalArgumentException(
                        "MessageProcessorBuilder should only have MessageProcessor's or MessageProcessorBuilder's configured");
            }
        }
        return MessageProcessors.lifecyleAwareMessageProcessorWrapper(builder.build());
    }

}
