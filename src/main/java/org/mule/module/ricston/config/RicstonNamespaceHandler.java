/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.config;

import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.config.spring.parsers.specific.RouterDefinitionParser;
import org.mule.module.ricston.processor.IgnoreReplyMulticastingRouter;
import org.mule.module.ricston.processor.IgnoreReplyPassThroughRouter;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registers a Bean Definition Parser for handling elements defned in META-INF/mule-ricston.xsd
 */
public class RicstonNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("ignore-reply-multicasting-router", new RouterDefinitionParser(IgnoreReplyMulticastingRouter.class));
        registerBeanDefinitionParser("ignore-reply-all", new ChildDefinitionParser("messageProcessor", IgnoreReplyMulticastingRouter.class));
        registerBeanDefinitionParser("ignore-reply-pass-through-router", new RouterDefinitionParser(IgnoreReplyPassThroughRouter.class));
        registerBeanDefinitionParser("ignore-reply-pass-through", new ChildDefinitionParser("messageProcessor", IgnoreReplyPassThroughRouter.class));
        registerBeanDefinitionParser("exception-message-processor-chain", new ExceptionMessageProcessorChainDefinitionParser());
    }


}
