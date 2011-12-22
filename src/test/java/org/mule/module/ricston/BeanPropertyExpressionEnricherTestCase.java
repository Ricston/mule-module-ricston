/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import com.mockobjects.dynamic.Mock;
import org.junit.Test;
import org.mule.api.MuleMessage;

import static org.junit.Assert.assertEquals;


public class BeanPropertyExpressionEnricherTestCase {

    @Test
    public void test_should_set_property_third() {
        BeanPropertyExpressionEnricher propertyExpressionEnricher = new BeanPropertyExpressionEnricher();

        Mock muleMessageMock = new Mock(MuleMessage.class);

        TestObject testObject = new TestObject();

        muleMessageMock.expectAndReturn("getPayload", testObject);

        propertyExpressionEnricher.enrich("first.second.third", (MuleMessage) muleMessageMock.proxy(), "This is a test");
        assertEquals(testObject.getFirst().getSecond().getThird(), "This is a test");

    }

    @Test
    public void test_should_set_property_hello() {
        BeanPropertyExpressionEnricher propertyExpressionEnricher = new BeanPropertyExpressionEnricher();
        Mock muleMessageMock = new Mock(MuleMessage.class);

        TestObject testObject = new TestObject();

        muleMessageMock.expectAndReturn("getPayload", testObject);

        propertyExpressionEnricher.enrich("hello", (MuleMessage) muleMessageMock.proxy(), "Test me");
        assertEquals(testObject.getHello(), "Test me");

    }


    public class TestObject {

        private First first = new First();
        private String hello;

        public First getFirst() {
            return first;
        }

        public String getHello() {
            return hello;
        }


        public void setHello(String hello) {
            this.hello = hello;
        }

        public class First {

            private Second second = new Second();

            public Second getSecond() {
                return second;
            }

            public class Second {

                private String third = null;

                public void setThird(String third) {
                    this.third = third;
                }

                public String getThird() {
                    return third;
                }
            }

        }

    }

}
