package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.text.Normalizer;
import java.util.List;

import seedu.address.logic.commands.FindGuardianCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.GuardianContainsKeywordsPredicate;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;

/**
 * Parses guardian-name keywords and exact guardian phone numbers.
 */
public class FindGuardianCommandParser implements Parser<FindGuardianCommand> {
    @Override
    public FindGuardianCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmed = Normalizer.normalize(args.trim(), Normalizer.Form.NFC);
        if (trimmed.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindGuardianCommand.MESSAGE_USAGE));
        }
        List<String> keywords = List.of(trimmed.split("\\s+"));
        for (String keyword : keywords) {
            if (!GuardianName.isValidName(keyword) && !GuardianPhone.isValidPhone(keyword)) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindGuardianCommand.MESSAGE_USAGE));
            }
        }
        return new FindGuardianCommand(new GuardianContainsKeywordsPredicate(keywords));
    }
}
