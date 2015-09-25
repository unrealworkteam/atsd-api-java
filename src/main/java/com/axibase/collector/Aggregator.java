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


import com.axibase.collector.config.SeriesSenderConfig;
import com.axibase.collector.logback.LogbackEventTrigger;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Nikolay Malevanny.
 */
public class Aggregator<E> {
    private Collection<E> cache = new ConcurrentLinkedQueue<E>();
    private AtomicInteger stepCounter = new AtomicInteger(0);
    private AtomicLong last = new AtomicLong(System.currentTimeMillis());
    private WritableByteChannel writer;
    private final MessageWriter<E> messageWriter;
    private ScheduledExecutorService scheduledExecutorService;
    private SendMessageTrigger<E>[] triggers = null;
    private SeriesSenderConfig seriesSenderConfig = SeriesSenderConfig.DEFAULT;

    public Aggregator(MessageWriter<E> messageWriter) {
        this.messageWriter = messageWriter;
    }

    public boolean register(E event) throws IOException {
        try {
            cache.add(event);
            checkThresholds(true);
            if (triggers != null) {
                int lines = 0;
                boolean fire = false;
                for (SendMessageTrigger<E> trigger : triggers) {
                    if (trigger.onEvent(event)) {
                        fire = true;
                        int stackTraceLines = trigger.getStackTraceLines();
                        if (stackTraceLines < 0) {
                            lines = Integer.MAX_VALUE;
                        } else if (stackTraceLines > lines) {
                            lines = stackTraceLines;
                        }
                    }
                }
                if (fire) {
                    sendSingle(event, lines);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException(t);
        }
        return true;
    }

    public void start() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        long periodMs = seriesSenderConfig.getPeriodMs();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    checkThresholds(false);
                } catch (IOException e) {
                    // ignore
                    e.printStackTrace();
                }
            }
        }, periodMs, periodMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }

    private void checkThresholds(boolean increment) throws IOException {
        int cnt = increment ? stepCounter.incrementAndGet() : stepCounter.get();
        long currentTime = System.currentTimeMillis();
        long lastTime = last.get();
        long dt = currentTime - lastTime;
        if (dt > seriesSenderConfig.getPeriodMs()) {
            if (last.compareAndSet(lastTime, currentTime)) {
                flush(lastTime, currentTime);
                cnt = 0;
            }
        }
        int sendThreshold = seriesSenderConfig.getSendThreshold();
        if (sendThreshold > 0 && dt > seriesSenderConfig.getMinPeriodMs() && cnt > sendThreshold) {
            if (stepCounter.compareAndSet(cnt, 0)) {
                flush(lastTime, currentTime);
            }
        }
    }

    protected void flush(long lastTime, long currentTime) throws IOException {
        last.set(currentTime);
        stepCounter.set(0);

        Collection<E> lastCache = cache;
        cache = new ConcurrentLinkedQueue<E>();

        synchronized (this) {
            messageWriter.writeStatMessages(writer, lastCache, (1 + currentTime - lastTime));
        }
        lastCache.clear();
    }

    private void sendSingle(E event, int lines) throws IOException {
        synchronized (this) {
            messageWriter.writeSingleMessage(writer, event, lines);
        }
    }

    public void setWriter(WritableByteChannel writer) {
        this.writer = writer;
    }

    public void addSendMessageTrigger(LogbackEventTrigger messageTrigger) {
        if (triggers == null) {
            triggers = new SendMessageTrigger[] {messageTrigger};
        } else {
            int l = triggers.length;
            triggers = Arrays.copyOf(triggers, l + 1);
            triggers[l] = messageTrigger;
        }
    }

    public void setSeriesSenderConfig(SeriesSenderConfig seriesSenderConfig) {
        this.seriesSenderConfig = seriesSenderConfig;
    }
}
