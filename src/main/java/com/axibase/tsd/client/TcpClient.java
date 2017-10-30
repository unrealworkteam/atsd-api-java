package com.axibase.tsd.client;

import com.axibase.tsd.model.system.TcpClientConfiguration;
import com.axibase.tsd.network.PlainCommand;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;

@Slf4j
class TcpClient {
    private final String serverName;
    private final int port;
    private final boolean autoflush;
    private final int connectionTimeoutMs;
    private final int readTimeoutMs;
    private Socket socket;
    private OutputStreamWriter writer;
    private final int BUFFER_SIZE = 16*1024;

    TcpClient(TcpClientConfiguration clientConfiguration) {
        this.serverName = clientConfiguration.getServerName();
        this.port = clientConfiguration.getPort();
        this.autoflush = clientConfiguration.isAutoflush();
        this.connectionTimeoutMs = clientConfiguration.getConnectionTimeoutMs();
        this.readTimeoutMs = clientConfiguration.getReadTimeoutMs();
    }

    synchronized public void send(PlainCommand command) {
        send(command.compose());
    }

    synchronized public void send(Collection<PlainCommand> commands) {
        if (autoflush) {
            for (PlainCommand command : commands) {
                send(command);
            }
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (PlainCommand command : commands) {
            String rawCommand = command.compose();
            builder.append(rawCommand);
            if (!rawCommand.endsWith("\n")) {
                builder.append("\n");
            }
        }

        send(builder.toString());
    }

    private void send(String data) {
        if (socket == null) {
            try {
                log.info("Connecting to ATSD at {}:{}", serverName, port);
                socket = recreateSocket();
                writer = recreateWriter(socket);
            } catch (IOException e) {
                throw new AtsdClientException(
                        String.format("Error while connecting to ATSD at %s:%s", serverName, port), e);
            }
        }

        try {
            writeData(data);
            return;
        } catch (Exception e) {
            log.warn("Error while sending commands to ATSD at {}:{}. Trying to reconnect", serverName, port, e);
        }

        try {
            socket = recreateSocket();
            writer = recreateWriter(socket);
            writeData(data);
        } catch (Exception e) {
            throw new AtsdClientException(
                    String.format("Error while sending command to ATSD at %s:%s", serverName, port), e);
        }
    }

    private void writeData(String data) throws IOException {
        writer.write(data);
        if (!data.endsWith("\n")) {
            writer.write('\n');
        }
        writer.flush();
    }

    synchronized public void close() {
        closeWriter();
        closeSocket();
    }

    private void closeWriter() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                log.warn("Error while closing tcp stream {}:{}", serverName, port, e);
            }
            writer = null;
        }
    }

    private void closeSocket() {
        if (socket != null) {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.warn("Error while closing tcp stream {}:{}", serverName, port, e);
                }
            }
            socket = null;
        }
    }

    private Socket recreateSocket() throws IOException {
        close();

        Socket socket = new Socket();
        socket.setSoTimeout(readTimeoutMs);
        socket.connect(new InetSocketAddress(serverName, port), connectionTimeoutMs);

        return socket;
    }

    private OutputStreamWriter recreateWriter(Socket socket) throws IOException {
        closeWriter();

        BufferedOutputStream stream = new BufferedOutputStream(socket.getOutputStream(), BUFFER_SIZE);
        return new OutputStreamWriter(stream, "UTF8");
    }
}
