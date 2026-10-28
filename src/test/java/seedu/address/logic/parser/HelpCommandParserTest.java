package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
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
        // Unicode spaces such as U+2003 (em space) around the target are removed too
        assertEquals(new HelpCommand("paid"), parser.parse(" paid "));
        assertEquals(new HelpCommand("help"), parser.parse("help"));
    }

    @Test
    public void parse_commandWordInAnyCase_returnsTargetedHelp() throws Exception {
        assertEquals(new HelpCommand("paid"), parser.parse("PAID"));
        assertEquals(new HelpCommand("paid"), parser.parse(" Paid "));
        assertEquals(new HelpCommand("findguardian"), parser.parse("findGuardian"));
    }

    @Test
    public void parse_unknownCommandWord_namesWordAndReportsAvailableCommands() {
        for (String input : new String[]{"unknown", "3", "LSIT", "f/10"}) {
            String message = String.format(MESSAGE_UNKNOWN_COMMAND, input)
                    + "\nAvailable commands: " + HelpCommand.getAvailableCommands();
            assertThrows(ParseException.class, message, () -> parser.parse(" " + input + " "));
        }
        assertThrows(ParseException.class, "Unknown command: \"lsit\".\nAvailable commands: "
                + HelpCommand.getAvailableCommands(), () -> parser.parse("lsit"));
    }

    @Test
    public void parse_extraWords_asksForOneCommandWord() {
        for (String input : new String[]{"PAID 1", "paid 1", "paid\thelp", "paid 1", "fees min/10",
            "findguardian Mei", "lsit 1"}) {
            assertThrows(ParseException.class, HelpCommand.MESSAGE_EXTRA_WORDS, () -> parser.parse(input));
        }
        assertThrows(ParseException.class, "Type only one command word after help, such as help paid.", ()
            -> parser.parse("paid 1"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
