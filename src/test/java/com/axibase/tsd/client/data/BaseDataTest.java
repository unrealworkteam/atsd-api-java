package com.axibase.tsd.client.data;

import com.axibase.tsd.client.DataService;
import com.axibase.tsd.client.HttpClientManager;

import com.axibase.tsd.client.TcpClientManager;
import org.junit.After;
import org.junit.Before;

import static com.axibase.tsd.TestUtil.buildHttpClientManager;
import static com.axibase.tsd.TestUtil.buildTcpClientManager;
import static com.axibase.tsd.TestUtil.waitWorkingServer;

abstract class BaseDataTest {

    protected DataService dataService;
    protected HttpClientManager httpClientManager;
    protected TcpClientManager tcpClientManager;

    @Before
    public void setUp() throws Exception {
        httpClientManager = buildHttpClientManager();
        httpClientManager.setCheckPeriodMillis(1000);
//        httpClientManager.setCheckPeriodMillis(30); // to extreme tests
        tcpClientManager = buildTcpClientManager();
        dataService = new DataService();
        dataService.setHttpClientManager(httpClientManager);

        waitWorkingServer(httpClientManager);
    }

    @After
    public void tearDown() throws Exception {
        httpClientManager.close();
    }

}
