/*
 * Generated by the Mule project wizard. http://mule.mulesoft.org
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston.config;

import org.mule.config.spring.parsers.generic.OrphanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registers a Bean Definition Parser for handling elements defned in META-INF/mule-ricston.xsd
 *
 */
public class RicstonNamespaceHandler extends NamespaceHandlerSupport
{
    public void init()
    {
        //TODO you need to register a definition parser for each element defined in META-INF/mule-ricston.xsd
        //Mule provides many parsrs out of the box which are suitable for most configuration parsing. see
        //http://muledocs.org/Xml+Configuration for more information.
        //for example:
        //    registerBeanDefinitionParser("foo", new OrphanDefinitionParser(RicstonFoo.class, true));
    }
}
