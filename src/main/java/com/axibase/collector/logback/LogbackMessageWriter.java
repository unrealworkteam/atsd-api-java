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
import com.axibase.collector.MessageWriter;
import com.axibase.collector.config.SeriesSenderConfig;
import com.axibase.collector.config.Tag;
import com.axibase.collector.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.*;

/**
 * @author Nikolay Malevanny.
 */
public class LogbackMessageWriter<E extends ILoggingEvent> implements MessageWriter<E> {
    private Map<String, String> tags = new LinkedHashMap<String, String>();
    private String entity = Utils.resolveHostname();
    private final Map<Key, Counter> story = new HashMap<Key, Counter>();
    private ByteBuffer seriesRatePrefix;
    private ByteBuffer seriesTotalRatePrefix;
    private ByteBuffer seriesTotalSumPrefix;
    private ByteBuffer messagePrefix;
    private SeriesSenderConfig seriesSenderConfig = SeriesSenderConfig.DEFAULT;
    private final Map<Level, CounterWithSum> totals = new HashMap<Level, CounterWithSum>();

    @Override
    public void writeStatMessages(WritableByteChannel writer, Collection<E> events, long deltaTime) throws IOException {
        if (deltaTime < 1) {
            throw new IllegalArgumentException("Illegal delta tie value: " + deltaTime);
        }
        int zeroRepeatCount = seriesSenderConfig.getZeroRepeatCount();

        // clean total counters
        for (Iterator<Map.Entry<Level, CounterWithSum>> iterator = totals.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Level, CounterWithSum> entry = iterator.next();
            if (entry.getValue().zeroRepeats < 0) {
                iterator.remove();
            }
        }
        // decrement all previous zero repeat counters
        for (Counter counter : story.values()) {
            counter.decrementZeroRepeats();
        }
        // increment using new events
        for (E event : events) {
            Key key = new Key(event.getLevel(), event.getLoggerName());
            Counter counter = story.get(key);
            if (counter == null) {
                story.put(key, new Counter(1, zeroRepeatCount));
            } else {
                counter.increment();
                counter.setZeroRepeats(zeroRepeatCount);
            }
        }
        long time = System.currentTimeMillis();
        // compose & clean
        for (Iterator<Map.Entry<Key, Counter>> iterator = story.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Key, Counter> entry = iterator.next();
            Counter counter = entry.getValue();
            if (counter.zeroRepeats < 0) {
                iterator.remove();
            } else {
                Key key = entry.getKey();
                int value = counter.value;
                try {
                    seriesRatePrefix.rewind();
                    writer.write(seriesRatePrefix);
                    StringBuilder sb = new StringBuilder();
                    Level level = key.level;
                    String levelString = level.toString();
                    double rate = value * (double) seriesSenderConfig.getRatePeriodMs() / deltaTime;
                    sb.append(rate);
                    sb.append(" t:level=").append(levelString);
                    sb.append(" t:logger=").append(Utils.sanitizeTagValue(key.logger));
                    sb.append(" ms:").append(time).append("\n");
                    writer.write(ByteBuffer.wrap(sb.toString().getBytes()));
                } catch (Throwable e) {
                    // ignore
                    e.printStackTrace();
                } finally {
                    if (value > 0) {
                        CounterWithSum total = totals.get(key.level);
                        if (total == null) {
                            total = new CounterWithSum(value, zeroRepeatCount);
                            totals.put(key.level, total);
                        } else {
                            total.add(value);
                            total.setZeroRepeats(zeroRepeatCount);
                        }
                    }
                    counter.clean();
                }
            }
        }

        // send totals
        for (Map.Entry<Level, CounterWithSum> entry : totals.entrySet()) {
            Level level = entry.getKey();
            CounterWithSum counterWithSum = entry.getValue();
            try {
                // write total rate
                seriesTotalRatePrefix.rewind();
                writer.write(seriesTotalRatePrefix);
                StringBuilder sb = new StringBuilder();
                String levelString = level.toString();
                double rate = counterWithSum.value * (double) seriesSenderConfig.getRatePeriodMs() / deltaTime;
                sb.append(rate);
                sb.append(" t:level=").append(levelString);
                sb.append(" ms:").append(time).append("\n");
                writer.write(ByteBuffer.wrap(sb.toString().getBytes()));
                counterWithSum.clean();
                // write total sum
                seriesTotalSumPrefix.rewind();
                writer.write(seriesTotalSumPrefix);
                sb = new StringBuilder();
                sb.append(counterWithSum.sum);
                sb.append(" t:level=").append(levelString);
                sb.append(" ms:").append(time).append("\n");
                writer.write(ByteBuffer.wrap(sb.toString().getBytes()));
            } catch (Throwable e) {
                // ignore
                e.printStackTrace();
            } finally {
                entry.getValue().decrementZeroRepeats();
            }
        }
    }

    @Override
    public void writeSingleMessage(WritableByteChannel writer, E event, int lines) throws IOException {
        messagePrefix.rewind();
        writer.write(messagePrefix);
        StringBuilder sb = new StringBuilder();
        String message = event.getFormattedMessage();
        if (lines > 0 && event.getCallerData() != null) {
            StringBuilder msb = new StringBuilder(message);
            for (int i = 0; i < event.getCallerData().length && i < lines; i++) {
                StackTraceElement traceElement = event.getCallerData()[i];
                msb.append("\n\t").append(traceElement.toString());
            }
            message = msb.toString();
        }
        sb.append(Utils.sanitizeMessage(message));
        sb.append(" t:severity=").append(event.getLevel());
        sb.append(" t:level=").append(event.getLevel());
        sb.append(" t:source=").append(Utils.sanitizeTagValue(event.getLoggerName()));
        sb.append(" ms:").append(System.currentTimeMillis()).append("\n");
        writer.write(ByteBuffer.wrap(sb.toString().getBytes()));
    }

    @Override
    public void start() {
        String sanitizedEntity = Utils.sanitizeEntity(entity);
        {
            StringBuilder sb = new StringBuilder();
            sb.append("series e:").append(sanitizedEntity);
            appendTags(sb);
            sb.append(" m:").append(
                    Utils.sanitizeMetric(seriesSenderConfig.getMetric() + seriesSenderConfig.getRateSuffix())).append(
                    "=");
            seriesRatePrefix = ByteBuffer.wrap(sb.toString().getBytes(Utils.UTF_8));
        }
        {
            StringBuilder sb = new StringBuilder();
            sb.append("series e:").append(sanitizedEntity);
            appendTags(sb);
            sb.append(" m:").append(Utils.sanitizeMetric(
                    seriesSenderConfig.getMetric() + seriesSenderConfig.getTotalSuffix() + seriesSenderConfig.getRateSuffix())).append(
                    "=");
            seriesTotalRatePrefix = ByteBuffer.wrap(sb.toString().getBytes(Utils.UTF_8));
        }
        {
            StringBuilder sb = new StringBuilder();
            sb.append("series e:").append(sanitizedEntity);
            appendTags(sb);
            sb.append(" m:").append(Utils.sanitizeMetric(
                    seriesSenderConfig.getMetric() + seriesSenderConfig.getTotalSuffix() + seriesSenderConfig.getCounterSuffix())).append(
                    "=");
            seriesTotalSumPrefix = ByteBuffer.wrap(sb.toString().getBytes(Utils.UTF_8));
        }
        {
            StringBuilder sb = new StringBuilder();
            sb.append("message e:").append(sanitizedEntity);
            appendTags(sb);
            unsafeAppendTag(sb, "type", "logger");
            sb.append(" m:");
            messagePrefix = ByteBuffer.wrap(sb.toString().getBytes(Utils.UTF_8));
        }


    }

    private void appendTags(StringBuilder sb) {
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String tagName = Utils.sanitizeTagKey(entry.getKey());
            String tagValue = Utils.sanitizeTagValue(entry.getValue());
            unsafeAppendTag(sb, tagName, tagValue);
        }
    }

    private void unsafeAppendTag(StringBuilder sb, String tagName, String tagValue) {
        sb.append(" t:").append(tagName).append("=").append(tagValue);
    }

    @Override
    public void stop() {

    }

    public void addTag(Tag tag) {
        tags.put(tag.getName(), tag.getValue());
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setSeriesSenderConfig(SeriesSenderConfig seriesSenderConfig) {
        this.seriesSenderConfig = seriesSenderConfig;
    }

    private static class Key {
        private final Level level;
        private final String logger;

        public Key(Level level, String logger) {
            if (level == null) {
                throw new IllegalArgumentException("Level is null");
            }
            this.level = level;
            this.logger = logger;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (level != null ? !level.equals(key.level) : key.level != null) return false;
            return !(logger != null ? !logger.equals(key.logger) : key.logger != null);

        }

        @Override
        public int hashCode() {
            int result = level != null ? level.hashCode() : 0;
            result = 31 * result + (logger != null ? logger.hashCode() : 0);
            return result;
        }
    }

    private static class Counter {
        protected int value;
        protected int zeroRepeats;

        public Counter(int value, int zeroRepeats) {
            this.value = value;
            setZeroRepeats(zeroRepeats);
        }

        void increment() {
            value++;
        }

        public void add(int value) {
            this.value += value;
        }

        void decrementZeroRepeats() {
            zeroRepeats--;
        }

        public void setZeroRepeats(int zeroRepeats) {
            this.zeroRepeats = zeroRepeats;
        }

        public void clean() {
            value = 0;
        }
    }

    private static class CounterWithSum extends Counter {
        private long sum;

        public CounterWithSum(int value, int zeroRepeats) {
            super(value, zeroRepeats);
        }

        @Override
        public void clean() {
            sum += value;
            super.clean();
        }
    }
}
