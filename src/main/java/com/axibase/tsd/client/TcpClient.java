package com.axibase.tsd.client;

import com.axibase.tsd.network.PlainCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

class TcpClient {
    private static final Logger log = LoggerFactory.getLogger(TcpClient.class);
    private TcpClientConfiguration clientConfiguration;
    private Socket socket;
    private PrintWriter writer;

    TcpClient(TcpClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    public void send(PlainCommand command) {
        if (writer == null) {
            try {
                socket = recreateSocket();
                writer = recreateWriter(socket);
            } catch (IOException e) {
                throw new AtsdClientException("Error while connecting to ATSD", e);
            }
        }

        try {
            if (!writer.checkError()) {
                writer.println(command.compose());
                if (clientConfiguration.isAutoflush()) {
                    writer.flush();
                }
                return;
            } else {
                log.warn("Error while sending commands to ATSD. Trying to reconnect");
            }
        } catch (Exception e) {
            log.warn("Error while sending commands to ATSD. Trying to reconnect", e);
        }

        try {
            socket = recreateSocket();
            writer = recreateWriter(socket);
            writer.println(command.compose());
            if (clientConfiguration.isAutoflush()) {
                writer.flush();
            }
        } catch (Exception e) {
            throw new AtsdClientException("Error while sending command to ATSD", e);
        }

        if (writer.checkError()) {
            throw new AtsdClientException("Error while sending command to ATSD");
        }
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }

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

    private PrintWriter recreateWriter(Socket socket) throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }

        writer = new PrintWriter(
                socket.getOutputStream(),
                clientConfiguration.isAutoflush());

        return writer;
    }
}
