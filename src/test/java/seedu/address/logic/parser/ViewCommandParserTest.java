package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewCommand;

public class ViewCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);

    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        // smallest valid index
        assertParseSuccess(parser, "1", new ViewCommand(INDEX_FIRST_PERSON));

        // leading and trailing whitespace
        assertParseSuccess(parser, "   1   ", new ViewCommand(INDEX_FIRST_PERSON));

        // tabs as whitespace
        assertParseSuccess(parser, "\t1\t", new ViewCommand(INDEX_FIRST_PERSON));

        // largest index that fits in an int
        assertParseSuccess(parser, String.valueOf(Integer.MAX_VALUE),
                new ViewCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_nonPositiveIndex_throwsParseException() {
        assertParseFailure(parser, "0", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "-1", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_malformedIndex_throwsParseException() {
        // signed number
        assertParseFailure(parser, "+1", MESSAGE_INVALID_FORMAT);

        // leading zero
        assertParseFailure(parser, "01", MESSAGE_INVALID_FORMAT);

        // decimal number
        assertParseFailure(parser, "1.5", MESSAGE_INVALID_FORMAT);

        // alphabetic input
        assertParseFailure(parser, "one", MESSAGE_INVALID_FORMAT);

        // non-ASCII digit (Arabic-Indic one)
        assertParseFailure(parser, "١", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_additionalArgs_throwsParseException() {
        assertParseFailure(parser, "1 2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 abc", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_indexTooLargeForInt_throwsParseException() {
        assertParseFailure(parser, Long.toString(Integer.MAX_VALUE + 1L), MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void parse_malformedIndexTooLargeForInt_throwsParseException() {
        // syntax errors take precedence over the index being too large

        // leading zero
        assertParseFailure(parser, "0" + (Integer.MAX_VALUE + 1L), MESSAGE_INVALID_FORMAT);

        // additional argument
        assertParseFailure(parser, (Integer.MAX_VALUE + 1L) + " 1", MESSAGE_INVALID_FORMAT);
    }
}
