package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetOutstandingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.OutstandingFee;

public class SetOutstandingCommandParserTest {

    private final SetOutstandingCommandParser parser = new SetOutstandingCommandParser();

    @Test
    public void parse_validBoundaryAmounts_returnsCommand() throws Exception {
        assertEquals(new SetOutstandingCommand(Index.fromOneBased(1), null), parser.parse("1 0"));
        assertEquals(new SetOutstandingCommand(Index.fromOneBased(1), null), parser.parse("1 0.00"));
        assertEquals(new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("0.01")),
                parser.parse(" 1 0.01 "));
        assertEquals(new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("99999.99")),
                parser.parse("1 99999.99"));
        assertEquals(new SetOutstandingCommand(Index.fromOneBased(1), null),
                new AddressBookParser().parseCommand("setoutstanding 1 0"));
    }

    @Test
    public void parse_plusPrefixedAmount_returnsIncrementCommand() throws Exception {
        assertEquals(SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(1),
                        new OutstandingFee("50")),
                parser.parse("1 +50"));
        assertEquals(SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(2),
                        new OutstandingFee("0.01")),
                parser.parse(" 2 +0.01 "));
        assertEquals(SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(1),
                        new OutstandingFee("99999.99")),
                parser.parse("1 +99999.99"));
        assertEquals(SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(1),
                        new OutstandingFee("120")),
                new AddressBookParser().parseCommand("setoutstanding 1 +120"));
    }

    @Test
    public void parse_invalidIncrement_reportsIncrementConstraints() {
        for (String amount : new String[]{"+0", "+0.00", "+", "++50", "+50.001", "+100000", "+abc"}) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse("1 " + amount));
            assertEquals(SetOutstandingCommand.MESSAGE_INVALID_INCREMENT, exception.getMessage(), amount);
        }
    }

    @Test
    public void parse_invalidAmount_reportsAmountConstraints() {
        for (String amount : new String[]{"-1", "100000", "1.234"}) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse("1 " + amount));
            assertEquals("Outstanding fee must be between 0 and 99999.99, with at most two decimal places.",
                    exception.getMessage());
        }
    }

    @Test
    public void parse_invalidIndexOrArgumentCount_reportsCommandFormat() {
        for (String input : new String[]{"0 1", "abc 1", "1", "1 2 3"}) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse(input));
            assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetOutstandingCommand.MESSAGE_USAGE),
                    exception.getMessage());
        }
    }

    @Test
    public void parse_indexTooLargeForInt_reportsInvalidStudentIndex() {
        for (String input : new String[]{"2147483648 1", "999999999999999999999999999999 1"}) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse(input));
            assertEquals(MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, exception.getMessage());
        }
    }

    @Test
    public void parse_invalidArguments_rejects() {
        for (String input : new String[]{"", "1", "1 2 3", "0 1", "01 1", "+1 1", "-1 1", "abc 1", "2147483648 1",
            "1 -1", "1 NaN", "1 1e2", "1 .5", "1 1.000", "1 0.001", "1 100000", "1 f/1"}) {
            assertThrows(ParseException.class, () -> parser.parse(input), input);
        }
    }
}
