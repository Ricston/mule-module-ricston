/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.config;

import org.mule.config.spring.parsers.delegate.ParentContextDefinitionParser;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.config.spring.parsers.generic.MuleOrphanDefinitionParser;

/**
 * This allows a message processor to be defined globally, or embedded within an
 * endpoint.
 */
public class ExceptionMessageProcessorChainDefinitionParser extends ParentContextDefinitionParser {
    public ExceptionMessageProcessorChainDefinitionParser() {
        super(MuleOrphanDefinitionParser.ROOT_ELEMENT, new MuleOrphanDefinitionParser(
                ExceptionMessageProcessorChainFactoryBean.class, false));
        otherwise(new ChildDefinitionParser("messageProcessor", ExceptionMessageProcessorChainFactoryBean.class));
    }

}
