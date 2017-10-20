package com.axibase.tsd.client;

import com.axibase.tsd.model.system.TcpClientConfiguration;
import com.axibase.tsd.network.PlainCommand;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
class TcpClient {
    private TcpClientConfiguration clientConfiguration;
    private Socket socket;
    private OutputStream socketStream;

    TcpClient(TcpClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    synchronized public void send(PlainCommand command) {
        if (socket == null) {
            try {
                socket = recreateSocket();
                socketStream = socket.getOutputStream();
            } catch (IOException e) {
                throw new AtsdClientException("Error while connecting to ATSD", e);
            }
        }

        try {
            socketStream.write(command.compose().getBytes("UTF8"));
            if (clientConfiguration.isAutoflush()) {
                socketStream.flush();
            }
            return;
        } catch (Exception e) {
            log.warn("Error while sending commands to ATSD. Trying to reconnect", e);
        }

        try {
            socket = recreateSocket();
            socketStream = socket.getOutputStream();
            socketStream.write(command.compose().getBytes("UTF8"));
            if (clientConfiguration.isAutoflush()) {
                socketStream.flush();
            }
        } catch (Exception e) {
            throw new AtsdClientException("Error while sending command to ATSD", e);
        }
    }

    synchronized public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.warn("Error while closing tcp stream", e);
            }
        }
    }

    private Socket recreateSocket() throws IOException {
        if (socket != null) {
            socket.close();
            socket = null;
        }

        return new Socket(
                clientConfiguration.getServerName(),
                clientConfiguration.getPort());
    }
}
