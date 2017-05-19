package com.axibase.tsd.client.command;

import java.util.Collections;

import com.axibase.tsd.network.MessageInsertCommand;
import com.axibase.tsd.network.PlainCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CommandMessageQuotesTest {

    private static final long time = 1488800000000L;

    @Test
    public void testComposing() {
        PlainCommand command = new MessageInsertCommand(
                "test-entity",
                time,
                Collections.singletonMap("tag", "OFF- RAMP \" U\", I"),
                "quo\"te"
        );
        
        assertEquals("Commands is composing incorrectly",
                "message e:test-entity ms:1488800000000 t:tag=\"OFF- RAMP \"\" U\"\", I\" m:\"quo\"\"te\"\n",
                command.compose()
        );
    }

    @Test
    public void testComposeMessageCommandWithQuotesInNames() {
        PlainCommand command = new MessageInsertCommand(
                "test\"entity",
                time,
                Collections.singletonMap("tag\"", "OFF- RAMP \" U\", I"),
                "quo\"te"
        );

        assertEquals("Commands is composing incorrectly",
                "message e:\"test\"\"entity\" ms:1488800000000 t:\"tag\"\"\"=\"OFF- RAMP \"\" U\"\", I\" m:\"quo\"\"te\"\n",
                command.compose()
        );
    }

    @Test
    public void testComposeMessageCommandWithEqualsSignInNames() {
        PlainCommand command = new MessageInsertCommand(
                "test=entity",
                time,
                Collections.singletonMap("tag=1", "OFF- RAMP \" U\", I"),
                "quo\"te"
        );

        assertEquals("Commands is composing incorrectly",
               "message e:\"test=entity\" ms:1488800000000 t:\"tag=1\"=\"OFF- RAMP \"\" U\"\", I\" m:\"quo\"\"te\"\n",
                command.compose()
        );
    }

}
