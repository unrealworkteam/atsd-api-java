package com.axibase.collector.logback;

import ch.qos.logback.core.AppenderBase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nikolay Malevanny.
 */
public class CountAppender<E> extends AppenderBase<E> {
    private static final AtomicInteger counter = new AtomicInteger();

    @Override
    protected void append(E eventObject) {
        counter.incrementAndGet();
    }

    public static int getCount() {
        return counter.get();
    }

    public static void clear() {
        counter.set(0);
    }
}
