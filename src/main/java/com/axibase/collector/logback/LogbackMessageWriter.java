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
import com.axibase.collector.Tag;
import com.axibase.collector.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.*;

/**
 * @author Nikolay Malevanny.
 */
public class LogbackMessageWriter<E extends ILoggingEvent> implements MessageWriter<E> {
    public static final String DEFAULT_METRIC_NAME = "log_event_count";
    public static final int DEFAULT_BB_SIZE = 16 * 1024;
    private Map<String, String> tags = new LinkedHashMap<String, String>();
    private int zeroRepeats;
    private String entity = Utils.resolveHostname();
    private String metric = DEFAULT_METRIC_NAME;
    private Map<Key, Counter> story = new HashMap<Key, Counter>();
    private ByteBuffer seriesPrefix;
    private ByteBuffer messagePrefix;

    @Override
    public void writeStatMessages(WritableByteChannel writer, Collection<E> events) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(DEFAULT_BB_SIZE);
        // decrement all previous zero repeat counters
        for (Counter counter : story.values()) {
            counter.decrementZeroRepeats();
        }
        // increment using new events
        for (E event : events) {
            Key key = new Key(event.getLevel(), event.getLoggerName());
            Counter counter = story.get(key);
            if (counter == null) {
                story.put(key, new Counter(zeroRepeats));
            } else {
                counter.increment();
                counter.setZeroRepeats(zeroRepeats);
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
                try {
                    seriesPrefix.rewind();
                    writer.write(seriesPrefix);
                    StringBuilder sb = new StringBuilder();
                    Key key = entry.getKey();
                    Level level = key.level;
                    String levelString = level.toString();
                    sb.append(counter.value).append(" t:level=").append(levelString);
                    sb.append(" t:logger=").append(Utils.sanitizeTagValue(key.logger));
                    sb.append(" ms:").append(time).append("\n");
                    writer.write(ByteBuffer.wrap(sb.toString().getBytes()));
                } catch (Throwable e) {
                    // ignore
                    e.printStackTrace();
                } finally {
                    counter.value = 0;
                }
            }
        }
    }

    @Override
    public void writeSingleMessage(WritableByteChannel writer, E event) throws IOException {
        messagePrefix.rewind();
        writer.write(messagePrefix);
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.sanitizeMessage(event.getFormattedMessage())).append(" t:level=").append(event.getLevel());
        sb.append(" t:logger=").append(Utils.sanitizeTagValue(event.getLoggerName()));
        sb.append(" ms:").append(System.currentTimeMillis()).append("\n");
        writer.write(ByteBuffer.wrap(sb.toString().getBytes()));
    }

    @Override
    public void start() {
        {
            StringBuilder sb = new StringBuilder();
            sb.append("series e:").append(Utils.sanitizeEntity(entity));
            appendTags(sb);
            sb.append(" m:").append(Utils.sanitizeMetric(metric)).append("=");
            seriesPrefix = ByteBuffer.wrap(sb.toString().getBytes(Utils.UTF_8));
        }
        {
            StringBuilder sb = new StringBuilder();
            sb.append("message e:").append(Utils.sanitizeEntity(entity));
            appendTags(sb);
            sb.append(" m:");
            messagePrefix = ByteBuffer.wrap(sb.toString().getBytes(Utils.UTF_8));
        }


    }

    private void appendTags(StringBuilder sb) {
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            sb.append(" t:").append(Utils.sanitizeTagKey(entry.getKey())).append("=").append(
                    Utils.sanitizeTagValue(entry.getValue()));
        }
    }

    @Override
    public void stop() {

    }

    public void addTag(Tag tag) {
        tags.put(tag.getName(), tag.getValue());
    }

    public void setZeroRepeats(int zeroRepeats) {
        this.zeroRepeats = zeroRepeats;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setMetric(String metric) {
        this.metric = metric;
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
        private int zeroRepeats;
        private int value = 1;

        public Counter(int zeroRepeats) {
            this.zeroRepeats = zeroRepeats;
        }

        void increment() {
            value++;
        }

        void decrementZeroRepeats() {
            zeroRepeats--;
        }

        public void setZeroRepeats(int zeroRepeats) {
            this.zeroRepeats = zeroRepeats;
        }
    }
}
