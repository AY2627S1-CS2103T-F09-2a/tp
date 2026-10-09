package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void parse_invalidArguments_rejects() {
        for (String input : new String[]{"", "1", "1 2 3", "0 1", "-1 1", "abc 1", "2147483648 1",
            "1 -1", "1 +1", "1 NaN", "1 1e2", "1 .5", "1 1.000", "1 0.001", "1 100000", "1 f/1"}) {
            assertThrows(ParseException.class, () -> parser.parse(input), input);
        }
    }
}
