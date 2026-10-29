package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.math.BigDecimal;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetOutstandingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.OutstandingFee;

/**
 * Parses the displayed student index and the replacement fee, or the amount to add when the
 * fee is written with a leading {@code +}.
 */
public class SetOutstandingCommandParser implements Parser<SetOutstandingCommand> {

    private static final String INCREMENT_PREFIX = "+";

    @Override
    public SetOutstandingCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 2 || !parts[0].matches("[1-9][0-9]*")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetOutstandingCommand.MESSAGE_USAGE));
        }
        Index index = ParserUtil.parseIndex(parts[0],
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetOutstandingCommand.MESSAGE_USAGE));
        boolean isIncrement = parts[1].startsWith(INCREMENT_PREFIX);
        String amount = isIncrement ? parts[1].substring(INCREMENT_PREFIX.length()) : parts[1];
        if (ParserUtil.hasMoneyFormatting(amount)) {
            throw new ParseException(OutstandingFee.MESSAGE_PLAIN_AMOUNT);
        }
        if (!amount.matches(OutstandingFee.VALIDATION_REGEX)) {
            throw new ParseException(isIncrement
                    ? SetOutstandingCommand.MESSAGE_INVALID_INCREMENT
                    : SetOutstandingCommand.MESSAGE_INVALID_AMOUNT);
        }
        if (new BigDecimal(amount).signum() == 0) {
            if (isIncrement) {
                throw new ParseException(SetOutstandingCommand.MESSAGE_INVALID_INCREMENT);
            }
            return new SetOutstandingCommand(index, null);
        }
        if (!OutstandingFee.isValidOutstandingFee(amount)) {
            throw new ParseException(isIncrement
                    ? SetOutstandingCommand.MESSAGE_INVALID_INCREMENT
                    : SetOutstandingCommand.MESSAGE_INVALID_AMOUNT);
        }
        OutstandingFee fee = new OutstandingFee(amount);
        return isIncrement
                ? SetOutstandingCommand.addToOutstandingFee(index, fee)
                : new SetOutstandingCommand(index, fee);
    }
}
