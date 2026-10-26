package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

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

    public static final String MESSAGE_UNEXPECTED_ARGUMENT =
            "Unexpected input: %1$s. fees takes only an optional min/AMOUNT.\n" + FeesCommand.MESSAGE_USAGE;
    public static final String MESSAGE_REPEATED_MINIMUM =
            "The min/ parameter must not be repeated.\n" + FeesCommand.MESSAGE_USAGE;
    public static final String MESSAGE_MISSING_MINIMUM =
            "Missing minimum amount after min/.\n" + FeesCommand.MESSAGE_USAGE;
    public static final String MESSAGE_INVALID_MINIMUM = "Invalid minimum amount: %1$s. The minimum must be from 0 "
            + "to 99999.99, with at most two decimal places and no sign or exponent.";

    private static final Logger logger = LogsCenter.getLogger(FeesCommandParser.class);
    private static final Prefix PREFIX_MINIMUM = new Prefix("min/");
    private static final BigDecimal MAXIMUM_AMOUNT = new BigDecimal("99999.99");

    /**
     * Parses the given {@code String} of arguments in the context of the FeesCommand.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public FeesCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.trim().isEmpty()) {
            logger.fine("Parsing fees command without a minimum amount.");
            return new FeesCommand();
        }

        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_MINIMUM);
        String amount = getSingleMinimum(arguments);
        BigDecimal minimumAmount = parseMinimumAmount(amount);

        logger.fine("Parsed fees command with minimum amount " + minimumAmount.toPlainString());
        return new FeesCommand(minimumAmount);
    }

    /**
     * Returns the text given after the only {@code min/} prefix.
     *
     * @throws ParseException if there is text other than one {@code min/} prefix and a single amount.
     */
    private static String getSingleMinimum(ArgumentMultimap arguments) throws ParseException {
        if (!arguments.getPreamble().isEmpty()) {
            logger.fine("Rejected fees command: unexpected text before min/.");
            throw new ParseException(String.format(MESSAGE_UNEXPECTED_ARGUMENT, arguments.getPreamble()));
        }
        if (arguments.getAllValues(PREFIX_MINIMUM).size() > 1) {
            logger.fine("Rejected fees command: repeated min/ prefix.");
            throw new ParseException(MESSAGE_REPEATED_MINIMUM);
        }

        String value = arguments.getValue(PREFIX_MINIMUM).orElseThrow();
        String[] words = value.split("\\s+", 2);
        if (words.length > 1) {
            logger.fine("Rejected fees command: unexpected text after the minimum amount.");
            throw new ParseException(String.format(MESSAGE_UNEXPECTED_ARGUMENT, words[1]));
        }
        return value;
    }

    /**
     * Parses {@code amount} as a minimum amount from 0 to {@link #MAXIMUM_AMOUNT}.
     *
     * @throws ParseException if {@code amount} is missing or is not a valid amount.
     */
    private static BigDecimal parseMinimumAmount(String amount) throws ParseException {
        if (amount.isEmpty()) {
            logger.fine("Rejected fees command: missing minimum amount.");
            throw new ParseException(MESSAGE_MISSING_MINIMUM);
        }
        if (!amount.matches(OutstandingFee.VALIDATION_REGEX)
                || new BigDecimal(amount).compareTo(MAXIMUM_AMOUNT) > 0) {
            logger.fine("Rejected fees command: invalid minimum amount.");
            throw new ParseException(String.format(MESSAGE_INVALID_MINIMUM, amount));
        }
        return new BigDecimal(amount);
    }
}
