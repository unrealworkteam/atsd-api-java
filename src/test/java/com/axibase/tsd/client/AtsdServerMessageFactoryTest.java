package com.axibase.tsd.client;

import com.axibase.tsd.model.system.ServerError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AtsdServerMessageFactoryTest {

    @Parameter
    public TestData testData;

    @Test
    public void testCreation() {
        final ServerError serverError = new ServerError();
        serverError.setMessage(testData.getMessage());
        final String message = AtsdServerMessageFactory.from(serverError);
        Assert.assertEquals(testData.handledMessage, message);
    }


    @Parameters(name = "Extact message from Server Error {0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                        {TestData.of("code 01", "General Server Error")},
                        {TestData.of("code 00", "code 00")},
                        {TestData.of("code 14", "Wrong IP Address")},
                        {TestData.of("message", "message")},
                }
        );
    }

    @AllArgsConstructor(staticName = "of")
    @Getter
    @ToString(doNotUseGetters = true)
    private static class TestData {
        private String message;
        private String handledMessage;
    }
}