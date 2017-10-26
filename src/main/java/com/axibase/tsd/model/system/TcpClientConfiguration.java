package com.axibase.tsd.model.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TcpClientConfiguration {
    private String serverName;
    private int port;
    private boolean autoflush;
    private int connectionTimeoutMs;
    private int readTimeoutMs;
}
