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

package com.axibase.tsd.client;

import com.axibase.tsd.plain.PlainCommand;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.*;

/**
 * @author Nikolay Malevanny.
 */
class PlainSender extends AbstractHttpEntity implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PlainSender.class);
    public static final String PING_COMMAND = "ping\n";

    private String url;
    private CountDownLatch latch = new CountDownLatch(1);
    private CloseableHttpClient httpClient;
    private BlockingQueue<String> messages;
    private volatile boolean active;
    private final long pingTimeoutMillis;
    private long lastMessageTime;

    public PlainSender(String url, long pingTimeoutMillis) {
        this.url = url;
        this.pingTimeoutMillis = pingTimeoutMillis;
    }

    public void send(PlainCommand plainCommand) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Initialization error:", e);
        }

        String message = plainCommand.compose();
        try {
            messages.put(message);
        } catch (InterruptedException e) {
            log.error("Could not put message: {}", message, e);
        }
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return true;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        while (active) {
            try {
                String message = messages.poll(pingTimeoutMillis, TimeUnit.MILLISECONDS);
//                log.debug("message = {}", message);
                if (message != null) {
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                    lastMessageTime = System.currentTimeMillis();
                }
                if (lastMessageTime - System.currentTimeMillis() > pingTimeoutMillis) {
                    outputStream.write(PING_COMMAND.getBytes());
                    outputStream.flush();
                    lastMessageTime = System.currentTimeMillis();
                }
            } catch (InterruptedException e) {
                log.error("Could not poll message from queue", e);
            }
        }
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    public void close() {
        active = false;
        HttpClientUtils.closeQuietly(httpClient);
    }

    @Override
    public void run() {
        messages = new LinkedBlockingQueue<String>();
        latch.countDown();
        httpClient = HttpClients.custom().build();
        HttpPost httpPost = new HttpPost(url +
                "/command");
        httpPost.setEntity(this);
        try {
            active = true;
            httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("Could not execute HTTP POST: {}", httpPost, e);
        }
    }
}
