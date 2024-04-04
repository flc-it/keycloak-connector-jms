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

package org.flcit.keycloak.connector.jms.streaming;

import java.io.OutputStream;

import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.StreamMessage;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class OutputStreamMessage extends OutputStream {

    private static final String ERROR_MESSAGE = "Error to write object in Stream Message";
    private final StreamMessage streamMessage;

    /**
     * @param streamMessage
     */
    public OutputStreamMessage(StreamMessage streamMessage) {
        this.streamMessage = streamMessage;
    }

    /**
     * @param b
     */
    public void writeInt(int b) {
        try {
            streamMessage.writeInt(b);
        } catch (JMSException e) {
            throw new JMSRuntimeException(ERROR_MESSAGE, "writeInt", e);
        }
    }

    @Override
    public void write(int b) {
        write(new byte[] { (byte) b });
    }

    /**
     * @param b
     */
    public void write(byte b) {
        try {
            streamMessage.writeByte(b);
        } catch (JMSException e) {
            throw new JMSRuntimeException(ERROR_MESSAGE, "writeByte", e);
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            streamMessage.writeBytes(b);
        } catch (JMSException e) {
            throw new JMSRuntimeException(ERROR_MESSAGE, "writeBytes", e);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        try {
            streamMessage.writeBytes(b, off, len);
        } catch (JMSException e) {
            throw new JMSRuntimeException(ERROR_MESSAGE, "writeBytes", e);
        }
    }

    @Override
    public void close() {
        // DO NOTHING
    }

}
