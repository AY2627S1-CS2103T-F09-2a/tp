package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.math.BigDecimal;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.FeesCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.OutstandingFee;

/**
 * Parses input arguments and creates a new {@code FeesCommand} object.
 */
public class FeesCommandParser implements Parser<FeesCommand> {

    private static final Logger logger = LogsCenter.getLogger(FeesCommandParser.class);
    private static final Prefix PREFIX_MINIMUM = new Prefix("min/");
    private static final BigDecimal MAXIMUM_AMOUNT = new BigDecimal("99999.99");

    /**
     * Parses the given {@code String} of arguments in the context of the FeesCommand.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public FeesCommand parse(String args) throws ParseException {
        if (args.trim().isEmpty()) {
            logger.fine("Parsing fees command without a minimum amount.");
            return new FeesCommand();
        }

        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_MINIMUM);
        if (!arguments.getPreamble().isEmpty() || arguments.getAllValues(PREFIX_MINIMUM).size() != 1) {
            logger.fine("Rejected fees command: expected exactly one min/ prefix and no preamble.");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE));
        }

        String amount = arguments.getValue(PREFIX_MINIMUM).orElseThrow();
        if (!amount.matches(OutstandingFee.VALIDATION_REGEX)) {
            logger.fine("Rejected fees command: invalid minimum amount format.");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE));
        }

        BigDecimal minimumAmount = new BigDecimal(amount);
        if (minimumAmount.compareTo(MAXIMUM_AMOUNT) > 0) {
            logger.fine("Rejected fees command: minimum amount exceeds the supported maximum.");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE));
        }

        logger.fine("Parsed fees command with minimum amount " + minimumAmount.toPlainString());
        return new FeesCommand(minimumAmount);
    }
}
