package com.axibase.tsd.client;

import lombok.RequiredArgsConstructor;

/**
 * Creates services with defined {@link HttpClientManager} instance.
 */
@RequiredArgsConstructor(staticName = "with")
public final class ServiceFactory {
    private final HttpClientManager clientManager;

    /**
     * Create {@link MetaDataService} instance.
     *
     * @return new {@link MetaDataService} instance.
     */
    public MetaDataService meta() {
        return new MetaDataService(clientManager);
    }

    /**
     * Create {@link DataService} instance.
     *
     * @return new {@link DataService} instance.
     */
    public DataService data() {
        return new DataService(clientManager);
    }

    /**
     * Create {@link ExtendedService} instance.
     *
     * @return new {@link ExtendedService} instance.
     */
    public ExtendedService extended() {
        return ExtendedService.with(clientManager);
    }
}
