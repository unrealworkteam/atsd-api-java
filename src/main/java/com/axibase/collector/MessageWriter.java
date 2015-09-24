package com.axibase.collector;


import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Collection;

/**
 * @author Nikolay Malevanny.
 */
public interface MessageWriter<E> {
    void writeStatMessages(WritableByteChannel writer, Collection<E> events) throws IOException;

    void writeSingleMessage(WritableByteChannel writer, E event) throws IOException;

    void start();

    void stop();
}
