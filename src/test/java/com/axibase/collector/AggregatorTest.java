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

package com.axibase.collector;

import ch.qos.logback.classic.Level;
import com.axibase.collector.logback.CountAppender;
import com.axibase.collector.logback.LogbackMessageWriter;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Nikolay Malevanny.
 */
public class AggregatorTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(AggregatorTest.class);
    private WritableByteChannel mockWriter;

    @Override
    public void setUp() throws Exception {
        CountAppender.clear();
        mockWriter = mock(WritableByteChannel.class);
    }

    public void testThresholds() throws Exception {
        int cnt = 15;

        LogbackMessageWriter messageWriter = new LogbackMessageWriter();
        messageWriter.start();
        Aggregator aggregator = new Aggregator(messageWriter);
        aggregator.setWriter(mockWriter);
        aggregator.setPeriodSec(1);
        aggregator.setSendEvery(7);
        aggregator.setSendThreshold(10);
        for (int i = 0; i < cnt; i++) {
            assertTrue(aggregator.register(TestUtils.createLoggingEvent(Level.INFO, "logger", "test-msg", "test-thread")));
        }
        Thread.sleep(1001);
        assertTrue(aggregator.register(TestUtils.createLoggingEvent(Level.INFO, "logger", "test-msg", "test-thread")));

        // (2 (every) + 1 (cnt) + 1 (time)) * 2 (prefix + content) = 8
        verify(mockWriter, times(8)).write(any(ByteBuffer.class));
    }

    @Test
    public void testMultiThreaded() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 15; i++) {
//                        log.warn("test");
                    }
                }
            });
        }
    }
}