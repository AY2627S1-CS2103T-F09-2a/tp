package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests optional help targets and rejects command arguments rather than executing them.
 */
public class HelpCommandParserTest {
    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_blank_opensHelpWindow() throws Exception {
        assertEquals(new HelpCommand(), parser.parse(""));
        assertEquals(new HelpCommand(), parser.parse(" \t "));
    }

    @Test
    public void parse_commandWord_returnsTargetedHelp() throws Exception {
        assertEquals(new HelpCommand("paid"), parser.parse(" \tpaid \t"));
        assertEquals(new HelpCommand("help"), parser.parse("help"));
    }

    @Test
    public void parse_unknownOrExtraArguments_reportsAvailableCommands() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE)
                + "\nAvailable commands: " + HelpCommand.getAvailableCommands();
        for (String input : new String[]{"unknown", "3", "PAID", "paid 1", "paid\thelp", "fees min/10",
            "findguardian", "f/10"}) {
            assertThrows(ParseException.class, message, () -> parser.parse(input));
        }
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
