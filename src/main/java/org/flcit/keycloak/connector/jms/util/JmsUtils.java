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

package org.flcit.keycloak.connector.jms.util;

import java.io.IOException;

import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.flcit.keycloak.connector.jms.streaming.OutputStreamMessage;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class JmsUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_EMPTY);

    private JmsUtils() { }

    /**
     * @param context
     * @param value
     * @return
     */
    public static Message getJsonMessage(JMSContext context, Object value) {
        final StreamMessage message = context.createStreamMessage();
        try (OutputStreamMessage out = new OutputStreamMessage(message)) {
            try {
                OBJECT_MAPPER.writeValue(out, value);
            } catch (IOException e) {
                throw new JMSRuntimeException("Error to write object in JSON", "JSON", e);
            }
        }
        return message;
    }

}
