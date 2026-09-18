package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindTagCommand;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;

public class FindTagCommandParserTest {
    private final FindTagCommandParser parser = new FindTagCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, " \t ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, "friends bad!", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_validArgs_returnsCommand() {
        FindTagCommand expected =
                new FindTagCommand(new TagContainsKeywordsPredicate(List.of("friends", "Colleagues")));
        assertParseSuccess(parser, " \n friends \t Colleagues ", expected);
    }
}
