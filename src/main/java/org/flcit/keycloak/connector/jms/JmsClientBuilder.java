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
import org.flcit.commons.core.util.StringUtils;
import org.keycloak.Config.Scope;

import com.tibco.tibjms.TibjmsConnectionFactory;
import com.tibco.tibjms.TibjmsQueue;
import com.tibco.tibjms.TibjmsTopic;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class JmsClientBuilder {

    private static final String SERVER_URL = "server-url";

    private JmsClientBuilder() { }

    /**
     * @param properties
     * @param prefix
     * @return
     */
    public static boolean hasServerUrl(Properties properties, String prefix) {
        return hasServerUrl(properties.getProperty(prefix + SERVER_URL));
    }

    public static boolean hasServerUrl(Scope config, String prefix) {
        return hasServerUrl(config.get(prefix + SERVER_URL));
    }

    private static boolean hasServerUrl(String serverUrl) {
        return StringUtils.hasLength(serverUrl);
    }

    /**
     * @param properties
     * @param prefix
     * @return
     */
    public static ConnectionFactory buildSafelyConnectionFactory(Properties properties, String prefix) {
        try {
            return JmsClientBuilder.buildConnectionFactory(properties, prefix);
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param config
     * @param prefix
     * @return
     */
    public static ConnectionFactory buildSafelyConnectionFactory(Scope config, String prefix) {
        try {
            return JmsClientBuilder.buildConnectionFactory(config, prefix);
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ConnectionFactory buildConnectionFactory(Properties properties, String prefix) throws JMSException {
        final TibjmsConnectionFactory connectionFactory = buildConnectionFactory(
                properties.getProperty(prefix + SERVER_URL),
                properties.getProperty(prefix + "client-id"),
                properties.getProperty(prefix + "user-name"),
                properties.getProperty(prefix + "user-password"),
                properties.getProperty(prefix + "multicast-daemon")
                );
        setConnectionFactory(connectionFactory,
                PropertyUtils.getNumber(properties, prefix + "connection-attempt-count", Integer.class),
                PropertyUtils.getNumber(properties, prefix + "connection-attempt-delay", Integer.class),
                PropertyUtils.getNumber(properties, prefix + "connection-attempt-timeout", Integer.class),
                PropertyUtils.getNumber(properties, prefix + "reconnection-attempt-count", Integer.class),
                PropertyUtils.getNumber(properties, prefix + "reconnection-attempt-delay", Integer.class),
                PropertyUtils.getNumber(properties, prefix + "reconnection-attempt-timeout", Integer.class),
                PropertyUtils.getBoolean(properties, prefix + "multicast-enabled")
                );
        return connectionFactory;
    }

    private static ConnectionFactory buildConnectionFactory(Scope config, String prefix) throws JMSException {
        final TibjmsConnectionFactory connectionFactory = buildConnectionFactory(
                config.get(prefix + SERVER_URL),
                config.get(prefix + "client-id"),
                config.get(prefix + "user-name"),
                config.get(prefix + "user-password"),
                config.get(prefix + "multicast-daemon")
                );
        setConnectionFactory(connectionFactory,
                config.getInt(prefix + "connection-attempt-count"),
                config.getInt(prefix + "connection-attempt-delay"),
                config.getInt(prefix + "connection-attempt-timeout"),
                config.getInt(prefix + "reconnection-attempt-count"),
                config.getInt(prefix + "reconnection-attempt-delay"),
                config.getInt(prefix + "reconnection-attempt-timeout"),
                config.getBoolean(prefix + "multicast-enabled")
                );
        return connectionFactory;
    }

    private static TibjmsConnectionFactory buildConnectionFactory(String serverUrl, String clientID, String userName, String userPassword, String multicastDaemon) throws JMSException {
        final TibjmsConnectionFactory connectionFactory = new TibjmsConnectionFactory();
        connectionFactory.setServerUrl(serverUrl);
        connectionFactory.setClientID(clientID);
        connectionFactory.setUserName(userName);
        connectionFactory.setUserPassword(userPassword);
        connectionFactory.setMulticastDaemon(multicastDaemon);
        return connectionFactory;
    }

    @SuppressWarnings("java:S107")
    private static ConnectionFactory setConnectionFactory(TibjmsConnectionFactory connectionFactory, Integer connectionAttemptCount, Integer connectionAttemptDelay, Integer connectionAttemptTimeout, Integer reconnectionAttemptCount, Integer reconnectionAttemptDelay, Integer reconnectionAttemptTimeout, Boolean multicastEnabled) {
        FunctionUtils.consumeIfNotNull(connectionAttemptCount, connectionFactory::setConnAttemptCount);
        FunctionUtils.consumeIfNotNull(connectionAttemptDelay, connectionFactory::setConnAttemptDelay);
        FunctionUtils.consumeIfNotNull(connectionAttemptTimeout, connectionFactory::setConnAttemptTimeout);
        FunctionUtils.consumeIfNotNull(reconnectionAttemptCount, connectionFactory::setReconnAttemptCount);
        FunctionUtils.consumeIfNotNull(reconnectionAttemptDelay, connectionFactory::setReconnAttemptDelay);
        FunctionUtils.consumeIfNotNull(reconnectionAttemptTimeout, connectionFactory::setReconnAttemptTimeout);
        FunctionUtils.consumeIfNotNull(multicastEnabled, connectionFactory::setMulticastEnabled);
        return connectionFactory;
    }

    /**
     * @param properties
     * @param prefix
     * @return
     */
    public static Destination buildDestination(Properties properties, String prefix) {
        return buildDestination(properties.getProperty(prefix + "queue"), properties.getProperty(prefix + "topic"));
    }

    /**
     * @param config
     * @param prefix
     * @return
     */
    public static Destination buildDestination(Scope config, String prefix) {
        return buildDestination(config.get(prefix + "queue"), config.get(prefix + "topic"));
    }

    private static Destination buildDestination(String addressQueue, String addressTopic) {
        if (StringUtils.hasLength(addressQueue)) {
            return new TibjmsQueue(addressQueue);
        }
        if (StringUtils.hasLength(addressTopic)) {
            return new TibjmsTopic(addressTopic);
        }
        return null;
    }

}
