package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ServerError;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

/**
 * Creates {@link AtsdServerException}.
 */
final class AtsdServerExceptionFactory {
    private AtsdServerExceptionFactory() {

    }

    /**
     * Generate {@link AtsdServerException} from Http Response.
     *
     * @param response {@link Response} class from jersey.
     * @return AtsdServerException instance with extracted message from response.
     */
    static AtsdServerException fromResponse(final Response response) {
        final int status = response.getStatus();
        try {
            final ServerError serverError = response.readEntity(ServerError.class);
            final String message = AtsdServerMessageFactory.from(serverError);
            return new AtsdServerException(message, status);
        } catch (ProcessingException e) {
            throw new IllegalArgumentException("Failed to extract server error", e);
        }
    }
}
