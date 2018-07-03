package com.axibase.tsd.client;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class SelfSignedTrustManager implements X509TrustManager, Serializable, Cloneable {

    public SelfSignedTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
        //No implementation here
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
        //No implementation here
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected void finalize() throws Throwable {
    }

    @Override
    public int hashCode() {
        try {

            return super.hashCode();
        } catch (Throwable e) {
            log.info("Bla bla");
            throw new RuntimeException(e);
        }
    }
}
