/*
 * Copyright 2015 Axibase Corporation or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * https://www.axibase.com/atsd/axibase-apache-2.0.pdf
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.axibase.collector.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.axibase.collector.Tag;
import com.axibase.collector.TestUtils;
import com.axibase.collector.Utils;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;

/**
 * @author Nikolay Malevanny.
 */
public class LogbackMessageWriterTest extends TestCase {

    public void testBuildSingleStatMessage() throws Exception {
        LogbackMessageWriter<ILoggingEvent> messageBuilder = createMessageBuilder();
        ArrayList<ILoggingEvent> events = new ArrayList<ILoggingEvent>();
        for (int i = 0; i < 100; i++) {
            events.add(TestUtils.createLoggingEvent(Level.ERROR, "test-logger", "test-message", "test-thread"));
        }
        StringsCatcher catcher = new StringsCatcher();
        messageBuilder.writeStatMessages(catcher, events);
        String result = catcher.sb.toString();
        assertEquals("series e:test-entity t:ttt1=vvv1 t:ttt2=vvv2 m:test-metric=100 t:level=ERROR t:logger=test-logger ",
                result.substring(0, result.indexOf("ms:1")));
    }

    public void testBuildMultipleStatMessage() throws Exception {
        LogbackMessageWriter<ILoggingEvent> messageBuilder = createMessageBuilder();
        messageBuilder.setZeroRepeats(2);
        ArrayList<ILoggingEvent> events = new ArrayList<ILoggingEvent>();
        for (int i = 0; i < 100; i++) {
            events.add(TestUtils.createLoggingEvent(Level.ERROR, "test-logger", "test-message", "test-thread"));
            events.add(TestUtils.createLoggingEvent(Level.WARN, "test-logger", "test-message", "test-thread"));
            events.add(TestUtils.createLoggingEvent(Level.DEBUG, "test-logger", "test-message", "test-thread"));
        }
        StringsCatcher catcher;
        {
            catcher = new StringsCatcher();
            messageBuilder.writeStatMessages(catcher, events);
            String result = catcher.sb.toString();
            assertTrue(result.contains("series e:test-entity t:ttt1=vvv1 t:ttt2=vvv2 m:test-metric=100 t:level="));
            assertTrue(result.contains("ERROR"));
            assertTrue(result.contains("WARN"));
            assertTrue(result.contains("DEBUG"));
            assertTrue(result.contains("m:test-metric=100 "));
        }

        {
            catcher.clear();
            events.clear();
            messageBuilder.writeStatMessages(catcher, events);
            String result = catcher.sb.toString();
            assertTrue(result.contains("ERROR"));
            assertTrue(result.contains("WARN"));
            assertTrue(result.contains("DEBUG"));
            assertTrue(result.contains("m:test-metric=0 "));
        }
        {
            catcher.clear();
            events.clear();
            messageBuilder.writeStatMessages(catcher, events);
            String result = catcher.sb.toString();
            assertTrue(result.contains("ERROR"));
            assertTrue(result.contains("WARN"));
            assertTrue(result.contains("DEBUG"));
            assertTrue(result.contains("m:test-metric=0 "));
        }
        {
            catcher.clear();
            events.clear();
            messageBuilder.writeStatMessages(catcher, events);
            String result = catcher.sb.toString();
            assertEquals("", result);
        }
    }

    public void testBuildSingleMessage() throws Exception {
        LogbackMessageWriter<ILoggingEvent> messageBuilder = createMessageBuilder();
            LoggingEvent event = TestUtils.createLoggingEvent(Level.ERROR, "test-logger", "test-message", "test-thread");
        StringsCatcher catcher = new StringsCatcher();
        messageBuilder.writeSingleMessage(catcher, event);
        String result= catcher.sb.toString();
        assertEquals("message e:test-entity t:ttt1=vvv1 t:ttt2=vvv2 m:test-message t:level=ERROR t:logger=test-logger ",
                result.substring(0, result.indexOf("ms:1")));
    }

    private LogbackMessageWriter<ILoggingEvent> createMessageBuilder() {
        LogbackMessageWriter<ILoggingEvent> messageBuilder = new LogbackMessageWriter<ILoggingEvent>();
        messageBuilder.setEntity("test-entity");
        messageBuilder.setMetric("test-metric");
        messageBuilder.addTag(new Tag("ttt1","vvv1"));
        messageBuilder.addTag(new Tag("ttt2","vvv2"));
        messageBuilder.start();
        return messageBuilder;
    }

    private static class StringsCatcher implements WritableByteChannel {
        private StringBuilder sb = new StringBuilder();

        @Override
        public int write(ByteBuffer src) throws IOException {
            if (src != null) {
                CharBuffer cb = Utils.UTF_8.decode(src);
                String string = cb.toString();
                sb.append(string);
                return string.length();
            } else {
                throw new IOException("src is null");
            }
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() throws IOException {

        }

        public void clear() {
            sb = new StringBuilder();
        }
    }
}