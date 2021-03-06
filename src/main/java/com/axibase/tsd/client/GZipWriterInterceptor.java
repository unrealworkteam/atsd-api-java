package com.axibase.tsd.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class GZipWriterInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        String acceptEncodings = getAcceptEncodingHeader(context.getHeaders());

        if (acceptEncodings.contains("gzip")) {
            final OutputStream outputStream = context.getOutputStream();
            context.setOutputStream(new GZIPOutputStream(outputStream));
        }

        context.proceed();
    }

    private String getAcceptEncodingHeader(MultivaluedMap<String, Object> headers) {
        Object value = headers.getFirst(HttpHeaders.ACCEPT_ENCODING);
        return value == null ? "" : (String) value;
    }

}
