package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindGuardianCommand;
import seedu.address.model.person.GuardianContainsKeywordsPredicate;

public class FindGuardianCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindGuardianCommand.MESSAGE_USAGE);

    private FindGuardianCommandParser parser = new FindGuardianCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidKeyword_throwsParseException() {
        // only keyword is invalid
        assertParseFailure(parser, "@@@", MESSAGE_INVALID_FORMAT);

        // one valid and one invalid keyword
        assertParseFailure(parser, "Mei T@n", MESSAGE_INVALID_FORMAT);

        // prefix
        assertParseFailure(parser, "g/Mei", MESSAGE_INVALID_FORMAT);

        // phone with a country code other than +65
        assertParseFailure(parser, "+6092345678", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_phoneKeyword_returnsFindGuardianCommand() {
        assertParseSuccess(parser, "92345678",
                new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of("92345678"))));
        assertParseSuccess(parser, "Mei 92345678",
                new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of("Mei", "92345678"))));

        // phone typed with a hyphen is kept as one keyword
        assertParseSuccess(parser, "9234-5678",
                new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of("9234-5678"))));

        // phone typed with the Singapore country code
        assertParseSuccess(parser, "+6592345678",
                new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of("+6592345678"))));
    }

    @Test
    public void parse_decomposedKeyword_normalizesKeyword() {
        assertParseSuccess(parser, "José",
                new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of("José"))));
    }

    @Test
    public void parse_validArgs_returnsFindGuardianCommand() {
        // no leading and trailing whitespaces
        FindGuardianCommand expectedCommand =
                new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of("Mei", "Tan")));
        assertParseSuccess(parser, "Mei Tan", expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Mei \n \t Tan  \t", expectedCommand);
    }

}
