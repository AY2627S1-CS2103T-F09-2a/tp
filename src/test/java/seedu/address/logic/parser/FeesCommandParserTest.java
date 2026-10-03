package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

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
}
