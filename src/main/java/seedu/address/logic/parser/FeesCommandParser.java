package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.math.BigDecimal;

import seedu.address.logic.commands.FeesCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code FeesCommand} object.
 */
public class FeesCommandParser implements Parser<FeesCommand> {

    private static final Prefix PREFIX_MINIMUM = new Prefix("min/");
    private static final BigDecimal MAXIMUM_AMOUNT = new BigDecimal("99999.99");

    /**
     * Parses the given {@code String} of arguments in the context of the FeesCommand.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public FeesCommand parse(String args) throws ParseException {
        if (args.trim().isEmpty()) {
            return new FeesCommand();
        }

        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_MINIMUM);
        if (!arguments.getPreamble().isEmpty() || arguments.getAllValues(PREFIX_MINIMUM).size() != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE));
        }

        String amount = arguments.getValue(PREFIX_MINIMUM).orElseThrow();
        if (!amount.matches("[0-9]+(?:\\.[0-9]{1,2})?")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE));
        }

        BigDecimal minimumAmount = new BigDecimal(amount);
        if (minimumAmount.compareTo(MAXIMUM_AMOUNT) > 0) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE));
        }

        return new FeesCommand(minimumAmount);
    }
}
