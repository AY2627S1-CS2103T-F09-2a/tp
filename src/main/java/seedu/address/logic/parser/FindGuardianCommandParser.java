package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.text.Normalizer;
import java.util.List;

import seedu.address.logic.commands.FindGuardianCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.GuardianContainsKeywordsPredicate;
import seedu.address.model.person.GuardianName;

/**
 * Parses input arguments and creates a new FindGuardianCommand object
 */
public class FindGuardianCommandParser implements Parser<FindGuardianCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindGuardianCommand
     * and returns a FindGuardianCommand object for execution.
     * A guardian phone keyword is accepted because guardian names may contain digits.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindGuardianCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindGuardianCommand.MESSAGE_USAGE));
        }

        String[] keywords = Normalizer.normalize(trimmedArgs, Normalizer.Form.NFC).split("\\s+");
        for (String keyword : keywords) {
            if (!keyword.matches(GuardianName.VALIDATION_REGEX)) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindGuardianCommand.MESSAGE_USAGE));
            }
        }

        return new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of(keywords)));
    }

}
