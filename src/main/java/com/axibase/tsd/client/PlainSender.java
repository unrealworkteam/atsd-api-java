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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Nikolay Malevanny.
 */
class PlainSender extends AbstractHttpEntity implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PlainSender.class);
    private static final String PING_COMMAND = "ping\n";
    private static final int SMALL = 64;

    private String url;
    private CountDownLatch latch = new CountDownLatch(1);
    private CloseableHttpClient httpClient;
    private BlockingQueue<String> messages;
    private volatile boolean active;
    private volatile boolean correct = true;
    private final long pingTimeoutMillis;
    private long lastMessageTime;
    private CloseableHttpResponse response;

    public PlainSender(String url, long pingTimeoutMillis, PlainSender old) {
        this.url = url;
        this.pingTimeoutMillis = pingTimeoutMillis;
        if (old != null) {
            messages = old.messages;
            log.info("Reborn plain commands sender using previous messages, size: {}", messages.size());
        }
    }

    public void send(PlainCommand plainCommand) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Initialization error:", e);
        }

        messages.add(plainCommand.compose());
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
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
            String message = null;
            try {
                message = messages.poll(pingTimeoutMillis, TimeUnit.MILLISECONDS);
//                log.debug("message = {}", message);
            } catch (InterruptedException e) {
                log.error("Could not poll message from queue", e);
            }

            try {
                if (message != null) {
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                    lastMessageTime = System.currentTimeMillis();
                }
            } catch (Throwable e) {
                active = false;
                correct = false;
                log.error("Sender is died. Could not send message: {}", message, e);
                messages.add(message);
                close();
                return;
            }
            if (lastMessageTime - System.currentTimeMillis() > pingTimeoutMillis) {
                outputStream.write(PING_COMMAND.getBytes());
                outputStream.flush();
                lastMessageTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    public void close() {
        active = false;
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                log.error("Could not close response: {}", response, e);
            }
        }
        if (httpClient == null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("Could not close client: {}", httpClient, e);
            }
        }
    }

    @Override
    public void run() {
        if (messages == null) {
            messages = new LinkedBlockingQueue<String>();
        }
        latch.countDown();
        BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager();
        connManager.setConnectionConfig(ConnectionConfig.custom().setBufferSize(SMALL).build());
        httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();
        HttpPost httpPost = new HttpPost(url +
                "/command");
        httpPost.setEntity(this);
        try {
            active = true;
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("Could not execute HTTP POST: {}", httpPost, e);
        }
    }

    public boolean isCorrect() {
        return correct;
    }
}
