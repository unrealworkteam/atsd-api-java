package com.axibase.tsd.client;

public class TcpClientConfiguration {
    private final String serverName;
    private final int port;
    private final boolean autoflush;

    public TcpClientConfiguration(String serverName, int port, boolean autoflush) {
        this.serverName = serverName;
        this.port = port;
        this.autoflush = autoflush;
    }

    public String getServerName() {
        return serverName;
    }

    public int getPort() {
        return port;
    }

    public boolean isAutoflush() {
        return autoflush;
    }
}
