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
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.FilterReply;
import com.axibase.collector.Aggregator;
import com.axibase.collector.Tag;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * @author Nikolay Malevanny.
 */
public class Collector<E extends ILoggingEvent> extends Filter<E> implements ContextAware {
    private final LogbackMessageWriter<E> logbackMessageBuilder = new LogbackMessageWriter<E>();
    private final Aggregator<E> aggregator = new Aggregator<E>(logbackMessageBuilder);
    private Level level = Level.TRACE;

    @Override
    public FilterReply decide(E event) {
        try {
            if (event.getLevel().isGreaterOrEqual(level)) {
                aggregator.register(event);
            }
        } catch (IOException e) {
            addError("Could not write message", e);
        }
        return FilterReply.NEUTRAL;
    }

    @Override
    public void start() {
        super.start();
        aggregator.start();
        logbackMessageBuilder.start();
    }

    @Override
    public void stop() {
        super.stop();
        aggregator.stop();
        logbackMessageBuilder.stop();
    }

    public void setZeroRepeats(int zeroRepeats) {
        aggregator.setZeroRepeats(zeroRepeats);
        logbackMessageBuilder.setZeroRepeats(zeroRepeats);
    }

    public void setTag(Tag tag) {
        logbackMessageBuilder.addTag(tag);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setWriter(WritableByteChannel writer) {
        aggregator.setWriter(writer);
    }
    
    public void setSendEvery(int sendEvery) {
        aggregator.setSendEvery(sendEvery);
    }
    
    public void setSendThreshold(int sendThreshold) {
        aggregator.setSendThreshold(sendThreshold);
    }
    
    public void setPeriodSec(int periodSec) {
        aggregator.setPeriodSec(periodSec);
    }

    public void setEntity(String entity) {
        logbackMessageBuilder.setEntity(entity);
    }

    public void setMetric(String metric) {
        logbackMessageBuilder.setMetric(metric);
    }
}
