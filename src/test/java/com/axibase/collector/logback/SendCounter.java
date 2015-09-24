package com.axibase.collector.logback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * @author Nikolay Malevanny.
 */
public class SendCounter implements WritableByteChannel {
    private volatile static long count;

    public static long getCount() {
        return count;
    }

    public static void clear() {
        System.out.println("SendCounter.clear");
        count = 0;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        count++;
        return 1;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
