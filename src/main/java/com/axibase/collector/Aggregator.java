package com.axibase.collector;


import java.io.IOException;
import java.nio.channels.WritableByteChannel;
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
    public static final long MILLIS = 1000L;
    public static final long DEFAULT_PERIOD_SEC = 60 * MILLIS;
    private Collection<E> cache = new ConcurrentLinkedQueue<E>();
    private AtomicInteger stepCounter = new AtomicInteger(0);
    private volatile long totalCounter;
    private AtomicLong last = new AtomicLong(System.currentTimeMillis());
    private int zeroRepeats;
    private WritableByteChannel writer;
    private final MessageWriter<E> messageWriter;
    private int sendEvery;
    private long periodMs = DEFAULT_PERIOD_SEC;
    private int sendThreshold;
    private ScheduledExecutorService scheduledExecutorService;

    public Aggregator(MessageWriter<E> messageWriter) {
        this.messageWriter = messageWriter;
    }

    public boolean register(E event) throws IOException {
        try {
            totalCounter++;
            cache.add(event);
            checkThresholds(true);
            if (totalCounter > 0 && sendEvery > 0 && (totalCounter % sendEvery) == 0) {
                sendSingle(event);
            }
        } catch (Throwable t){
            t.printStackTrace();
            throw new IOException(t);
        }
        return true;
    }

    public void start() {
        if (zeroRepeats > 0) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
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
    }

    public void stop() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }

    private void checkThresholds(boolean increment) throws IOException {
        int cnt = increment ? stepCounter.incrementAndGet() : stepCounter.get();
        long current = System.currentTimeMillis();
        long lastTime = last.get();
        if (current - lastTime > periodMs) {
            if (last.compareAndSet(lastTime, current)) {
                flush(current);
                cnt = 0;
            }
        }
        if (sendThreshold > 0 && cnt > sendThreshold) {
            if (stepCounter.compareAndSet(cnt, 0)) {
                flush(current);
            }
        }
    }

    protected void flush(long current) throws IOException {
        last.set(current);
        stepCounter.set(0);

        Collection<E> lastCache = cache;
        cache = new ConcurrentLinkedQueue<E>();

        synchronized (this) {
            messageWriter.writeStatMessages(writer, lastCache);
        }
        lastCache.clear();
    }

    private void sendSingle(E event) throws IOException {
        synchronized (this) {
            messageWriter.writeSingleMessage(writer, event);
        }
    }

    public void setZeroRepeats(int zeroRepeats) {
        this.zeroRepeats = zeroRepeats;
    }

    public void setWriter(WritableByteChannel writer) {
        this.writer = writer;
    }

    public void setSendThreshold(int sendThreshold) {
        this.sendThreshold = sendThreshold;
    }

    public void setPeriodSec(int periodSec) {
        if (periodSec < 1) {
            throw new IllegalArgumentException("Period value must by more than 0, currently " + periodSec);
        }
        this.periodMs = periodSec * MILLIS;
    }

    public void setSendEvery(int sendEvery) {
        this.sendEvery = sendEvery;
    }

    @Override
    public String toString() {
        return "Aggregator{" +
                "counter=" + stepCounter +
                ", zeroRepeats=" + zeroRepeats +
                ", writer=" + writer +
                ", sendEvery=" + sendEvery +
                ", sendThreshold=" + sendThreshold +
                ", periodMs=" + periodMs +
                '}';
    }
}
