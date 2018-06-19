package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ServerError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;

public class AtsdServerExceptionFactoryTest {

    @Parameterized.Parameter
    public TestData testData;

    @Test
    public void testBadResponseException() {
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.getStatus()).thenReturn(HttpStatus.SC_UNAUTHORIZED);
        final ServerError error = new ServerError();
        error.setMessage("code 01");
        Mockito.when(response.readEntity(ServerError.class))
                .thenReturn(error);
        final AtsdServerException exception =
                AtsdServerExceptionFactory.fromResponse(response);
        Assert.assertEquals(HttpStatus.SC_UNAUTHORIZED, exception.getStatus());
        Assert.assertEquals("Incorrect exception message",
                "General Server Error", exception.getMessage());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgument() {
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.getStatus())
                .thenReturn(HttpStatus.SC_OK);
        AtsdServerExceptionFactory.fromResponse(response);
    }

    @Getter
    @AllArgsConstructor(staticName = "of")
    private static class TestData {
        private int status;
        private ServerError body;
    }

}