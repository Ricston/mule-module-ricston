/*
 * Copyright (c) Ricston Ltd.  All rights reserved.  http://www.ricston.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.ricston;

import org.mule.api.MuleMessage;
import org.mule.api.expression.ExpressionEnricher;
import org.mule.transport.NullPayload;

import java.lang.reflect.Method;

public class BeanPropertyExpressionEnricher implements ExpressionEnricher {

    public static final String SET = "set";
    public static final String GET = "get";

    @Override
    public void setName(String name) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getName() {
        return "bean-property";
    }

    @Override
    public void enrich(String expression, MuleMessage message, Object object) {

        if (!(object instanceof NullPayload)) {
            String[] properties = expression.split("\\.");
            Object finalRefObject = getFinalRefObject(properties, message.getPayload());
            Method[] finalRefObjectMethods = finalRefObject.getClass().getMethods();
            String setter = SET + capitaliseFirstLetter(properties[properties.length - 1]);

            //TODO: not efficient; use getMethod(...) instead of looping
            for (Method method : finalRefObjectMethods) {
                if (method.getName().equals(setter)) {
                    try {
                        method.invoke(finalRefObject, object);
                        break;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private Object getFinalRefObject(String[] properties, Object objectToCall) {
        if (properties.length <= 1) {
            return objectToCall;
        } else {
            Method[] methods = objectToCall.getClass().getMethods();
            String getter = GET + capitaliseFirstLetter(properties[0]);

            //TODO: not efficient; use getMethod(...) instead of looping
            for (Method method : methods) {
                if (method.getName().equals(getter)) {
                    try {
                        Object nextObjectToCall = method.invoke(objectToCall, null);
                        String[] propertiesToGet = new String[properties.length - 1];
                        int index = 1;

                        while (index < properties.length) {
                            propertiesToGet[index - 1] = properties[index];
                            index++;
                        }

                        return getFinalRefObject(propertiesToGet, nextObjectToCall);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return null;
        }
    }

    private String capitaliseFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1, value.length());
    }

}
