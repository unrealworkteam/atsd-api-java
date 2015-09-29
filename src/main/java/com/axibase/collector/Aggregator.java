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
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Nikolay Malevanny.
 */
public class Aggregator<E> {
    public static final int DEFAULT_CHECK_PERIOD_MS = 333;
    private final Worker worker = new Worker();
    private volatile CountedQueue<E> cache = new CountedQueue<E>();
    private CountedQueue<EventWrapper<E>> singles = new CountedQueue<EventWrapper<E>>();
    private AtomicInteger stepCounter = new AtomicInteger(0);
    private AtomicLong last = new AtomicLong(System.currentTimeMillis());
    private WritableByteChannel writer;
    private final MessageWriter<E> messageWriter;
    private ExecutorService senderExecutor;
    private volatile CountDownLatch latch = new CountDownLatch(1);
    private SendMessageTrigger<E>[] triggers = null;
    private SeriesSenderConfig seriesSenderConfig = SeriesSenderConfig.DEFAULT;

    private int skippedCount = 0;

    public Aggregator(MessageWriter<E> messageWriter) {
        this.messageWriter = messageWriter;
    }

    public boolean register(E event) throws IOException {
        try {
            cache.add(event);
            stepCounter.incrementAndGet();

            int count = cache.getCount();
            if (count > 0 && count % seriesSenderConfig.getCacheFlushThreshold() == 0) {
                latch.countDown();
            }
            if (count > seriesSenderConfig.getCacheSkipThreshold()) {
                if (++skippedCount % seriesSenderConfig.getCacheSkipThreshold() == 0) {
                    System.err.println("skipped: " + skippedCount + " events");
                }
                cache.poll(); // kill oldest
            }

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
        senderExecutor = Executors.newSingleThreadExecutor();
        senderExecutor.execute(worker);
    }

    public void stop() {
        if (worker != null) {
            worker.stop();
        }
        if (senderExecutor != null && !senderExecutor.isShutdown()) {
            senderExecutor.shutdown();
        }
        if (writer != null && writer.isOpen()) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkThresholds() throws IOException {
        int cnt = stepCounter.get();
        long currentTime = System.currentTimeMillis();
        long lastTime = last.get();
        long dt = currentTime - lastTime;
        long periodMs = seriesSenderConfig.getPeriodMs();
        if (dt > periodMs) {
            if (last.compareAndSet(lastTime, currentTime)) {
                flush(lastTime, currentTime);
                cnt = 0;
            }
        }

        int sendThreshold = seriesSenderConfig.getSendThreshold();
        if ((sendThreshold > 0 && dt > seriesSenderConfig.getMinPeriodMs() && cnt > sendThreshold)
                || cnt > seriesSenderConfig.getCacheFlushThreshold()) {
            for (; ; ) {
                int last = stepCounter.get();
                if (last == 0) {
                    break;
                }
                if (stepCounter.compareAndSet(last, 0)) {
                    flush(lastTime, currentTime);
                    break;
                }
            }
        }
        if (!singles.isEmpty()) {
            messageWriter.writeSingles(writer, singles);
        }
    }

    protected void flush(long lastTime, long currentTime) throws IOException {
        last.set(currentTime);
        stepCounter.set(0);

        CountedQueue<E> lastCache = cache;
        cache = new CountedQueue<E>();

        messageWriter.writeStatMessages(writer, lastCache, (1 + currentTime - lastTime));
    }

    private void sendSingle(final E event, final int lines) throws IOException {
        singles.add(new EventWrapper<E>(event, lines));
        if (singles.getCount() > seriesSenderConfig.getMessageSkipThreshold()) {
            singles.poll();
        }
    }

    public void setWriter(WritableByteChannel writer) {
        this.writer = writer;
    }

    public void addSendMessageTrigger(LogbackEventTrigger messageTrigger) {
        if (triggers == null) {
            triggers = new SendMessageTrigger[]{messageTrigger};
        } else {
            int l = triggers.length;
            triggers = Arrays.copyOf(triggers, l + 1);
            triggers[l] = messageTrigger;
        }
    }

    public void setSeriesSenderConfig(SeriesSenderConfig seriesSenderConfig) {
        this.seriesSenderConfig = seriesSenderConfig;
    }

    private class Worker implements Runnable {
        private volatile boolean stopped = false;

        @Override
        public void run() {
            while (!stopped) {
                if (latch.getCount() == 0) {
                    latch = new CountDownLatch(1);
                }
                try {
                    latch.await(DEFAULT_CHECK_PERIOD_MS, TimeUnit.MILLISECONDS);

                    checkThresholds();
                } catch (IOException e) {
                    // ignore
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // ignore
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void stop() {
            stopped = true;
        }
    }
}
