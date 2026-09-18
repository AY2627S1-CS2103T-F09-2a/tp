package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.logic.commands.FindTagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses tag keywords and creates a tag-search command.
 */
public class FindTagCommandParser implements Parser<FindTagCommand> {
    @Override
    public FindTagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTagCommand.MESSAGE_USAGE));
        }
        List<String> keywords = List.of(trimmedArgs.split("\\s+"));
        if (keywords.stream().anyMatch(keyword -> !Tag.isValidTagName(keyword))) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new FindTagCommand(new TagContainsKeywordsPredicate(keywords));
    }
}
