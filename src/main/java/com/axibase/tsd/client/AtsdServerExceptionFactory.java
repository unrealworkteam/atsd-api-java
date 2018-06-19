package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ServerError;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
        int status = response.getStatus();
        Status.Family statusFamily = Status.Family.familyOf(status);
        if (Status.Family.SUCCESSFUL == statusFamily
                || Status.Family.REDIRECTION == statusFamily) {
            throw new IllegalArgumentException("Response status is successful!");
        } else {
            final ServerError serverError = response.readEntity(ServerError.class);
            final String message = AtsdServerMessageFactory.from(serverError);
            return new AtsdServerException(message, status);
        }
    }
}
