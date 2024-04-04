/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flcit.keycloak.connector.jms;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.flcit.commons.core.util.FunctionUtils;
import org.flcit.commons.core.util.PropertyUtils;

import com.tibco.tibjms.TibjmsConnectionFactory;
import com.tibco.tibjms.TibjmsQueue;
import com.tibco.tibjms.TibjmsTopic;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class JmsClientBuilder {

    private JmsClientBuilder() { }

    /**
     * @param properties
     * @param prefix
     * @return
     * @throws JMSException
     */
    public static ConnectionFactory buildConnectionFactory(Properties properties, String prefix) throws JMSException {
        final TibjmsConnectionFactory connectionFactory = new TibjmsConnectionFactory();
        connectionFactory.setServerUrl(properties.getProperty(prefix + "server-url"));
        connectionFactory.setClientID(properties.getProperty(prefix + "client-id"));
        connectionFactory.setUserName(properties.getProperty(prefix + "user-name"));
        connectionFactory.setUserPassword(properties.getProperty(prefix + "user-password"));
        FunctionUtils.consumeIfNotNull(() -> PropertyUtils.getNumber(properties, prefix + "connection-attempt-count", Integer.class), connectionFactory::setConnAttemptCount);
        FunctionUtils.consumeIfNotNull(() -> PropertyUtils.getNumber(properties, prefix + "connection-attempt-delay", Integer.class), connectionFactory::setConnAttemptDelay);
        FunctionUtils.consumeIfNotNull(() -> PropertyUtils.getNumber(properties, prefix + "connection-attempt-timeout", Integer.class), connectionFactory::setConnAttemptTimeout);
        FunctionUtils.consumeIfNotNull(() -> PropertyUtils.getNumber(properties, prefix + "reconnection-attempt-timeout", Integer.class), connectionFactory::setReconnAttemptCount);
        FunctionUtils.consumeIfNotNull(() -> PropertyUtils.getNumber(properties, prefix + "reconnection-attempt-delay", Integer.class), connectionFactory::setReconnAttemptDelay);
        FunctionUtils.consumeIfNotNull(() -> PropertyUtils.getNumber(properties, prefix + "reconnection-attempt-timeout", Integer.class), connectionFactory::setReconnAttemptTimeout);
        connectionFactory.setMulticastDaemon(properties.getProperty(prefix + "multicast-daemon"));
        FunctionUtils.consumeIfNotNull(() -> PropertyUtils.getBoolean(properties, prefix + "multicast-enabled"), connectionFactory::setMulticastEnabled);
        return connectionFactory;
    }

    /**
     * @param properties
     * @param prefix
     * @return
     */
    public static Destination buildDestination(Properties properties, String prefix) {
        String address = properties.getProperty(prefix + "queue");
        if (address != null) {
            return new TibjmsQueue(address);
        }
        address = properties.getProperty(prefix + "topic");
        if (address != null) {
            return new TibjmsTopic(address);
        }
        return null;
    }

}
