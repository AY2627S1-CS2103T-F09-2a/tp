package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.PaidCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a displayed-list index for recording full payment.
 */
public class PaidCommandParser implements Parser<PaidCommand> {
    @Override
    public PaidCommand parse(String args) throws ParseException {
        requireNonNull(args);
        return new PaidCommand(ParserUtil.parseIndex(args.trim(),
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PaidCommand.MESSAGE_USAGE)));
    }
}
