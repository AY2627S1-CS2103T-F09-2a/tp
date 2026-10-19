package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FeeSummaryCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Rejects arguments to the all-student outstanding-fee summary.
 */
public class FeeSummaryCommandParser implements Parser<FeeSummaryCommand> {
    @Override
    public FeeSummaryCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeeSummaryCommand.MESSAGE_USAGE));
        }
        return new FeeSummaryCommand();
    }
}
