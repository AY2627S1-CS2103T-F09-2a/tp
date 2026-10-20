package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FeesCommand;

public class FeesCommandParserTest {

    private final FeesCommandParser parser = new FeesCommandParser();

    @Test
    public void parse_emptyArguments_returnsFeesCommand() {
        assertParseSuccess(parser, "", new FeesCommand());
        assertParseSuccess(parser, "   ", new FeesCommand());
    }

    @Test
    public void parse_nonEmptyArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "1", expectedMessage);
        assertParseFailure(parser, " t/late", expectedMessage);
    }

    @Test
    public void parse_validMinimum_returnsFeesCommand() {
        for (String amount : new String[]{"0", "0.00", "0.01", "100", "100.0", "100.00", "99999.99", "000100"}) {
            assertParseSuccess(parser, " min/" + amount, new FeesCommand(new BigDecimal(amount)));
        }
        assertParseSuccess(parser, "min/100", new FeesCommand(new BigDecimal("100")));
        assertParseSuccess(parser, "  min/ 100.00  ", new FeesCommand(new BigDecimal("100")));
    }

    @Test
    public void parse_invalidMinimum_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE);
        for (String amount : new String[]{"", " ", "-1", "-0", "+1", "100000", "99999.999", "0.001",
            "1e2", "1E2", "NaN", "Infinity", ".50", "1.", "1,000", "1 00", "abc"}) {
            assertParseFailure(parser, " min/" + amount, expectedMessage);
        }
    }

    @Test
    public void parse_extraArgumentsOrPrefixes_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE);
        for (String arguments : new String[]{" min/100 min/100", " min/100 min/200", " min/ min/100",
            " min/100min/200", " max/100", " MIN/100", " extra min/100", " min/100 extra",
            " min/100 max/200", " max/200 min/100", " min/100 200"}) {
            assertParseFailure(parser, arguments, expectedMessage);
        }
    }
}
