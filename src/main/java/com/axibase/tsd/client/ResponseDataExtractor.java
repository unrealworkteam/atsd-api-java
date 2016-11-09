package com.axibase.tsd.client;

import javax.ws.rs.core.Response;

public interface ResponseDataExtractor<T> {

    T extract(Response response);

}