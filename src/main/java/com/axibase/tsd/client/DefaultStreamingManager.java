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

import com.axibase.tsd.model.system.MarkerState;
import com.axibase.tsd.plain.MarkerCommand;
import com.axibase.tsd.plain.PlainCommand;
import com.axibase.tsd.query.Query;
import com.axibase.tsd.query.QueryPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Nikolay Malevanny.
 */
public class DefaultStreamingManager implements StreamingManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultStreamingManager.class);
    public static final String PING = "ping";
    private static final int DEFAULT_PING_PERIOD_MS = 5000;
    private long pingPeriodMillis = DEFAULT_PING_PERIOD_MS;
    private final AtomicReference<PlainSender> plainSender = new AtomicReference<PlainSender>();
    private final AtomicLong lastPingTime = new AtomicLong(0);
    private final AtomicReference<String> marker = new AtomicReference<String>();
    private boolean lastPingResult = false;
    private final List<String> saved = new ArrayList<String>();
    private final HttpClientManager httpClientManager;
    private Future<?> senderFuture;

    public DefaultStreamingManager(HttpClientManager httpClientManager) {
        if (httpClientManager == null) {
            throw new IllegalArgumentException("httpClientManager is null");
        }
        this.httpClientManager = httpClientManager;
    }

    @Override
    public void close() {
        PlainSender sender = plainSender.get();
        if (sender != null) {
            sender.close();
        }

    }

    @Override
    public void send(PlainCommand plainCommand) {
        if (!lastPingResult) {
            throw new IllegalStateException("Last ping was bad, call canSend() method before command sending");
        }
        PlainSender sender = plainSender.get();
        if (sender ==null) {
            throw new IllegalStateException("Sender is null");
        } else if (!sender.isCorrect()) {
            throw new IllegalStateException("Sender is in the wrong state");
        }
        sender.send(plainCommand);
    }

    @Override
    public boolean canSend() {
        long last = lastPingTime.get();
        long current = System.currentTimeMillis();
        PlainSender sender = plainSender.get();
        if (current - last > pingPeriodMillis) {
            if (lastPingTime.compareAndSet(last, current)) {
                sender = prepareSender();
                boolean beforeLastResult = lastPingResult;
                if (beforeLastResult && lastPingResult) {
                    saved.clear();
                }
            }
        }
        return lastPingResult && sender != null && sender.isCorrect();
    }

    private PlainSender prepareSender() {
        PlainSender sender = plainSender.get();
        if (sender == null || !sender.isCorrect()) {
            PlainSender newSender = new PlainSender(httpClientManager.getClientConfiguration(), sender);
            if (plainSender.compareAndSet(sender, newSender)) {
                if (sender !=null) {
                    sender.close();
                }
                if (senderFuture!=null) {
                    senderFuture.cancel(true);
                }
                senderFuture = Executors.newSingleThreadExecutor().submit(newSender);
                newSender.setCorrect(true);
            }
            sender = plainSender.get();
        }
        lastPingResult = ping(sender);
        if (lastPingResult) {
            compareAndSendNewMarker(marker.get());
        }
        return sender;
    }

    private boolean ping(PlainSender sender) {
        try {
            if (sender != null) {
                Map<String, List<String>> markerToMessages = sender.getMarkerToMessages();
                int size = markerToMessages.size();
                if (size <= 2) {
                    // just ping
                    MarkerState markerState = askMarkerState(PING);
                    boolean result = PING.equals(markerState.getMarker());
                    if (result) {
                        sender.setCorrect(result);
                    }
                    return result;
                }

                int i = 0;

                for (Iterator<Map.Entry<String, List<String>>> iterator = markerToMessages.entrySet().iterator();
                     iterator.hasNext() && i < size - 2; i++) {
                    Map.Entry<String, List<String>> markerAndCommands = iterator.next();
                    String checkedMarker = markerAndCommands.getKey();
                    MarkerState markerState = askMarkerState(checkedMarker);
                    List<String> commands = markerAndCommands.getValue();
                    if (markerState.getCount() != null) {
                        if (markerState.getCount() > commands.size()) {
                            log.warn("Server received more ({}) commands then client sent ({}), marker: {}",
                                    markerState.getCount(), commands.size(), checkedMarker);
                        } else if (markerState.getCount() < commands.size()) {
                            log.error("Server received less ({}) commands then client sent ({}), marker: {}",
                                    markerState.getCount(), commands.size(), checkedMarker);
                            saved.addAll(commands);
                        } else {
                            log.debug("Server received same command count ({}) that client sent, marker: {}",
                                    commands.size(), checkedMarker);
                        }
                        iterator.remove();
                    } else {
                        log.error("Could not get command count for marker {}", marker);
                        saved.addAll(commands);
                        iterator.remove();
                    }
                }

                if (saved.isEmpty()) {
                    sender.setCorrect(true);
                    return true;
                } else {
                    for (Iterator<Map.Entry<String, List<String>>> iterator = markerToMessages.entrySet().iterator();
                         iterator.hasNext(); ) {
                        Map.Entry<String, List<String>> markerAndCommands = iterator.next();
                        List<String> commands = markerAndCommands.getValue();
                        saved.addAll(commands);
                        iterator.remove();
                    }
                    log.error("Save {} commands, broken sender will be killed", saved.size());
                    sender.close();
                    return false;
                }
            } else {
                log.warn("Sender is null");
                return false;
            }
        } catch (Throwable e) {
            log.warn("Ping error: ", e);
            return false;
        }
    }

    private MarkerState askMarkerState(String marker) {
        QueryPart<MarkerState> markersPath = new Query<MarkerState>("command").path("marker");
        QueryPart<MarkerState> query = markersPath.path(marker);
        return httpClientManager.requestData(MarkerState.class, query, null);
    }

    private void compareAndSendNewMarker(String current) {
        MarkerCommand markerCommand = new MarkerCommand();
        final String newMarker = markerCommand.getMarker();

        if (marker.compareAndSet(current, newMarker)) {
            PlainSender sender = plainSender.get();
            if (sender == null) {
                throw new IllegalStateException("Sender is null");
            } else if (!sender.isCorrect()) {
                throw new IllegalStateException("Sender is incorrect");
            } else {
                sender.send(markerCommand);
            }
        } else {
            log.warn("Current marker:{} is already replaced by another marker:", current, marker.get());
        }
    }

    @Override
    public List<String> removeSavedPlainCommands() {
        if (saved.isEmpty()) {
            return Collections.emptyList();
        }
        synchronized (saved) {
            List<String> result = new ArrayList<String>(saved);
            saved.removeAll(result);
            if (result.size() > 0) {
                log.info("{} commands are removed from saved list", result.size());
            }
            return result;
        }
    }

}
