package com.axibase.tsd.client;

import com.axibase.tsd.model.system.TcpClientConfiguration;
import com.axibase.tsd.util.AtsdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class TcpClientConfigurationFactory {
    private static final int DEFAULT_TCP_PORT = 8081;
    private static final boolean DEFAULT_AUTOFLUSH = false;
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 3000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 10000;

    private static final String DEFAULT_CLIENT_PROPERTIES_FILE_NAME = "classpath:/client.properties";
    private static final String AXIBASE_TSD_API_DOMAIN = "axibase.tsd.api";

    private String serverName;
    private int port;
    private boolean autoflush;
    private int connectionTimeoutMs;
    private int readTimeoutMs;

    private TcpClientConfigurationFactory() {
    }

    public static TcpClientConfigurationFactory createInstance() {
        String clientPropertiesFileName = DEFAULT_CLIENT_PROPERTIES_FILE_NAME;
        String sysPropertiesFileName = System.getProperty(AXIBASE_TSD_API_DOMAIN + ".client.properties");
        if (StringUtils.isNotBlank(sysPropertiesFileName)) {
            clientPropertiesFileName = sysPropertiesFileName;
        }
        return createInstance(clientPropertiesFileName);
    }

    public static TcpClientConfigurationFactory createInstance(String clientPropertiesFileName) {
        Properties clientProperties = AtsdUtil.loadProperties(clientPropertiesFileName);
        TcpClientConfigurationFactory configurationFactory = new TcpClientConfigurationFactory();
        configurationFactory.serverName = AtsdUtil.getPropertyStringValue(
                AXIBASE_TSD_API_DOMAIN + ".server.name", clientProperties, null);
        configurationFactory.port = AtsdUtil.getPropertyIntValue(
                AXIBASE_TSD_API_DOMAIN + ".server.tcp.port", clientProperties, DEFAULT_TCP_PORT);
        configurationFactory.autoflush = AtsdUtil.getPropertyBoolValue(
                AXIBASE_TSD_API_DOMAIN + ".server.tcp.autoflush", clientProperties, DEFAULT_AUTOFLUSH);
        configurationFactory.connectionTimeoutMs = AtsdUtil.getPropertyIntValue(
                AXIBASE_TSD_API_DOMAIN + ".server.tcp.connectionTimeoutMs", clientProperties, DEFAULT_CONNECTION_TIMEOUT_MS);
        configurationFactory.readTimeoutMs = AtsdUtil.getPropertyIntValue(
                AXIBASE_TSD_API_DOMAIN + ".server.tcp.readTimeoutMs", clientProperties, DEFAULT_READ_TIMEOUT_MS);

        return configurationFactory;
    }

    public TcpClientConfigurationFactory(
            String serverName,
            int port,
            boolean autoflush,
            int connectionTimeoutMs,
            int readTimeoutMs) {
        this.serverName = serverName;
        this.port = port;
        this.autoflush = autoflush;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    public TcpClientConfiguration createClientConfiguration() {
        return new TcpClientConfiguration(
                serverName,
                port,
                autoflush,
                connectionTimeoutMs,
                readTimeoutMs
        );
    }
}
