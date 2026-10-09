package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.math.BigDecimal;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetOutstandingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.OutstandingFee;

/**
 * Parses the displayed student index and replacement fee.
 */
public class SetOutstandingCommandParser implements Parser<SetOutstandingCommand> {

    @Override
    public SetOutstandingCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetOutstandingCommand.MESSAGE_USAGE));
        }
        Index index = ParserUtil.parseIndex(parts[0]);
        if (!parts[1].matches(OutstandingFee.VALIDATION_REGEX)) {
            throw new ParseException(SetOutstandingCommand.MESSAGE_USAGE);
        }
        if (new BigDecimal(parts[1]).signum() == 0) {
            return new SetOutstandingCommand(index, null);
        }
        if (!OutstandingFee.isValidOutstandingFee(parts[1])) {
            throw new ParseException(SetOutstandingCommand.MESSAGE_USAGE);
        }
        return new SetOutstandingCommand(index, new OutstandingFee(parts[1]));
    }
}
