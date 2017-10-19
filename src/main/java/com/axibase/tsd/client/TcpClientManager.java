package com.axibase.tsd.client;

import com.axibase.tsd.network.PlainCommand;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.atomic.AtomicReference;

public class TcpClientManager {
    private static final int DEFAULT_BORROW_MAX_TIME_MS = 3000;
    private static final int DEFAULT_MAX_TOTAL = 100;
    private static final int DEFAULT_MAX_IDLE = 100;

    private GenericObjectPoolConfig objectPoolConfig;

    private AtomicReference<GenericObjectPool<TcpClient>> objectPoolAtomicReference = new AtomicReference<>();
    private int borrowMaxWaitMillis = DEFAULT_BORROW_MAX_TIME_MS;

    private TcpClientConfiguration clientConfiguration;

    public TcpClientManager() {
        objectPoolConfig = new GenericObjectPoolConfig();
        objectPoolConfig.setMaxTotal(DEFAULT_MAX_TOTAL);
        objectPoolConfig.setMaxIdle(DEFAULT_MAX_IDLE);
    }

    public TcpClientManager(TcpClientConfiguration clientConfiguration) {
        super();
        this.clientConfiguration = clientConfiguration;
    }

    public void setClientConfiguration(TcpClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    public void setObjectPoolConfig(GenericObjectPoolConfig objectPoolConfig) {
        this.objectPoolConfig = objectPoolConfig;
    }

    public void setBorrowMaxWaitMillis(int borrowMaxWaitMillis) {
        this.borrowMaxWaitMillis = borrowMaxWaitMillis;
    }

    public void send(PlainCommand plainCommand) {
        TcpClient tcpClient = borrowClient();
        try {
            tcpClient.send(plainCommand);
        } finally {
            returnClient(tcpClient);
        }
    }

    private TcpClient borrowClient() {
        GenericObjectPool<TcpClient> objectPool = createObjectPool();
        TcpClient tcpClient;
        try {
            tcpClient = objectPool.borrowObject(borrowMaxWaitMillis);
        } catch (Exception e) {
            throw new AtsdClientException("Could not borrow tcp client from pool", e);
        }
        return tcpClient;
    }

    private void returnClient(TcpClient tcpClient) {
        objectPoolAtomicReference.get().returnObject(tcpClient);
    }

    private GenericObjectPool<TcpClient> createObjectPool() {
        GenericObjectPool<TcpClient> tcpClientGenericObjectPool = objectPoolAtomicReference.get();
        if (tcpClientGenericObjectPool == null) {
            tcpClientGenericObjectPool = new GenericObjectPool<>(new TcpClientBasePooledObjectFactory(), objectPoolConfig);
            objectPoolAtomicReference.compareAndSet(null, tcpClientGenericObjectPool);
        }
        return objectPoolAtomicReference.get();
    }

    public void close() {
        GenericObjectPool<TcpClient> pool = objectPoolAtomicReference.get();
        if (pool != null) {
            pool.close();
        }
    }

    private class TcpClientBasePooledObjectFactory extends BasePooledObjectFactory<TcpClient> {
        @Override
        public TcpClient create() throws Exception {
            return new TcpClient(clientConfiguration);
        }

        @Override
        public PooledObject<TcpClient> wrap(TcpClient tcpClient) {
            return new DefaultPooledObject<>(tcpClient);
        }

        @Override
        public void destroyObject(PooledObject<TcpClient> p) throws Exception {
            p.getObject().close();
        }
    }
}
