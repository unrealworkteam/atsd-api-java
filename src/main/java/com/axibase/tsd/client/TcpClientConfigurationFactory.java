package com.axibase.tsd.client;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class TcpClientConfigurationFactory {
    private static final Logger log = LoggerFactory.getLogger(TcpClientConfigurationFactory.class);

    private static final boolean DEFAULT_AUTOFLUSH = true;

    private static final String DEFAULT_CLIENT_PROPERTIES_FILE_NAME = "classpath:/client.properties";
    private static final String AXIBASE_TSD_API_DOMAIN = "axibase.tsd.api";
    private static final String CLASSPATH_PREFIX = "classpath:";

    private String serverName;
    private int port;
    private boolean autoflush;

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
        log.debug("Load client properties from file: {}", clientPropertiesFileName);
        Properties clientProperties = new Properties();
        InputStream stream = null;
        try {
            if (clientPropertiesFileName.startsWith(CLASSPATH_PREFIX)) {
                String resourcePath = clientPropertiesFileName.split(CLASSPATH_PREFIX)[1];
                log.info("Load properties from classpath: {}", resourcePath);
                stream = ClientConfigurationFactory.class.getResourceAsStream(resourcePath);
            } else {
                File file = new File(clientPropertiesFileName);
                log.info("Load properties from file: {}", file.getAbsolutePath());
                stream = new FileInputStream(file);
            }
            clientProperties.load(stream);
        } catch (Throwable e) {
            log.warn("Could not load client properties", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        TcpClientConfigurationFactory configurationFactory = new TcpClientConfigurationFactory();
        configurationFactory.serverName = load(AXIBASE_TSD_API_DOMAIN + ".server.name", clientProperties, null);
        configurationFactory.port = loadInt(AXIBASE_TSD_API_DOMAIN + ".server.tcp.port", clientProperties, 8081);
        configurationFactory.autoflush = loadBool(AXIBASE_TSD_API_DOMAIN + ".server.tcp.autoflush", clientProperties, DEFAULT_AUTOFLUSH);

        return configurationFactory;
    }

    public TcpClientConfigurationFactory(String serverName, int port, boolean autoflush) {
        this.serverName = serverName;
        this.port = port;
        this.autoflush = autoflush;
    }

    public TcpClientConfiguration createClientConfiguration() {
        TcpClientConfiguration clientConfiguration = new TcpClientConfiguration(
                serverName,
                port,
                autoflush
        );

        return clientConfiguration;
    }

    private static String load(String name, Properties clientProperties, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null) {
            value = clientProperties.getProperty(name);
            if (value == null) {
                if (defaultValue == null) {
                    log.error("Could not find required property: {}", name);
                    throw new IllegalStateException(name + " property is null");
                } else {
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    private static int loadInt(String name, Properties clientProperties, int defaultValue) {
        return NumberUtils.toInt(load(name, clientProperties, ""), defaultValue);
    }

    private static boolean loadBool(String name, Properties clientProperties, boolean defaultValue) {
        String value = load(name, clientProperties, "");
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        return BooleanUtils.toBoolean(value);
    }
}
