package com.axibase.tsd.model.system;

import lombok.Data;

@Data
public class TcpClientConfiguration {
    private final String serverName;
    private final int port;
    private final boolean autoflush;

    public TcpClientConfiguration(String serverName, int port, boolean autoflush) {
        this.serverName = serverName;
        this.port = port;
        this.autoflush = autoflush;
    }
}
