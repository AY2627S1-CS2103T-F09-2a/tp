package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.FeesCommandParser.MESSAGE_INVALID_MINIMUM;
import static seedu.address.logic.parser.FeesCommandParser.MESSAGE_MISSING_MINIMUM;
import static seedu.address.logic.parser.FeesCommandParser.MESSAGE_REPEATED_MINIMUM;
import static seedu.address.logic.parser.FeesCommandParser.MESSAGE_UNEXPECTED_ARGUMENT;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FeesCommand;

public class FeesCommandParserTest {

    private final FeesCommandParser parser = new FeesCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_emptyArguments_returnsFeesCommand() {
        assertParseSuccess(parser, "", new FeesCommand());
        assertParseSuccess(parser, "   ", new FeesCommand());
    }

    @Test
    public void parse_textOtherThanMinimum_throwsParseException() {
        assertParseFailure(parser, "1", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "1"));
        assertParseFailure(parser, " t/late", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "t/late"));
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
        for (String amount : new String[]{"-1", "-0", "+1", "100000", "99999.999", "0.001",
            "1e2", "1E2", "NaN", "Infinity", ".50", "1.", "1,000", "abc", "100min/200"}) {
            assertParseFailure(parser, " min/" + amount, String.format(MESSAGE_INVALID_MINIMUM, amount));
        }
    }

    @Test
    public void parse_missingMinimum_throwsParseException() {
        assertParseFailure(parser, " min/", MESSAGE_MISSING_MINIMUM);
        assertParseFailure(parser, " min/  ", MESSAGE_MISSING_MINIMUM);
    }

    @Test
    public void parse_repeatedMinimum_throwsParseException() {
        for (String arguments : new String[]{" min/100 min/100", " min/100 min/200", " min/ min/100"}) {
            assertParseFailure(parser, arguments, MESSAGE_REPEATED_MINIMUM);
        }
    }

    @Test
    public void parse_extraArgumentsOrPrefixes_throwsParseException() {
        assertParseFailure(parser, " max/100", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "max/100"));
        assertParseFailure(parser, " MIN/100", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "MIN/100"));
        assertParseFailure(parser, " extra min/100", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "extra"));
        assertParseFailure(parser, " max/200 min/100", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "max/200"));
        assertParseFailure(parser, " min/100 extra", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "extra"));
        assertParseFailure(parser, " min/100 max/200", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "max/200"));
        assertParseFailure(parser, " min/100 200", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "200"));
        assertParseFailure(parser, " min/1 00", String.format(MESSAGE_UNEXPECTED_ARGUMENT, "00"));
    }
}
