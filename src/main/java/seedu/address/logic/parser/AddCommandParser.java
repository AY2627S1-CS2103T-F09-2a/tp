package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OUTSTANDING_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;
import seedu.address.model.person.Name;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final String MESSAGE_MISSING_PARAMETER = "Missing required parameter: %1$s.";
    public static final String MESSAGE_REPEATED_PARAMETER = "The %1$s parameter must not be repeated.";

    /** Matches a token that looks like a prefix: letters followed by '/' at the start of a word. */
    private static final Pattern PREFIX_LIKE_TOKEN = Pattern.compile("(?<=^|\\s)([A-Za-z]+/)");

    private static final List<Prefix> REQUIRED_PREFIXES =
            List.of(PREFIX_NAME, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);

    /** Every prefix accepted by {@code add}, mapped to the full parameter name used in error messages. */
    private static final Map<Prefix, String> PARAMETER_NAMES = new LinkedHashMap<>();

    static {
        PARAMETER_NAMES.put(PREFIX_NAME, "name");
        PARAMETER_NAMES.put(PREFIX_GUARDIAN_NAME, "guardian name");
        PARAMETER_NAMES.put(PREFIX_GUARDIAN_PHONE, "guardian phone");
        PARAMETER_NAMES.put(PREFIX_PHONE, "phone");
        PARAMETER_NAMES.put(PREFIX_EMAIL, "email");
        PARAMETER_NAMES.put(PREFIX_ADDRESS, "address");
        PARAMETER_NAMES.put(PREFIX_OUTSTANDING_FEE, "outstanding fee");
    }

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        Prefix[] prefixes = PARAMETER_NAMES.keySet().toArray(Prefix[]::new);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, prefixes);

        if (!argMultimap.getPreamble().isEmpty() || hasUnknownPrefix(argMultimap)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        verifyRequiredPrefixesPresent(argMultimap);
        verifyNoRepeatedPrefixes(argMultimap, prefixes);

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        GuardianName guardianName = ParserUtil.parseGuardianName(argMultimap.getValue(PREFIX_GUARDIAN_NAME).get());
        GuardianPhone guardianPhone =
                ParserUtil.parseGuardianPhone(argMultimap.getValue(PREFIX_GUARDIAN_PHONE).get());
        Phone phone = parseOptional(argMultimap.getValue(PREFIX_PHONE), ParserUtil::parsePhone);
        Email email = parseOptional(argMultimap.getValue(PREFIX_EMAIL), ParserUtil::parseEmail);
        Address address = parseOptional(argMultimap.getValue(PREFIX_ADDRESS), ParserUtil::parseAddress);
        OutstandingFee outstandingFee =
                parseOptional(argMultimap.getValue(PREFIX_OUTSTANDING_FEE), ParserUtil::parseOutstandingFee);

        Person person = new Person(name, phone, email, address, guardianName, guardianPhone, outstandingFee,
                Set.of());

        return new AddCommand(person);
    }

    /**
     * Returns true if a parameter value other than the address contains a prefix-like token that {@code add}
     * does not accept, such as {@code t/}. Addresses may contain such text, for example {@code c/o} or
     * {@code Blk A/B}.
     */
    private static boolean hasUnknownPrefix(ArgumentMultimap argMultimap) {
        return PARAMETER_NAMES.keySet().stream()
                .filter(prefix -> !prefix.equals(PREFIX_ADDRESS))
                .flatMap(prefix -> argMultimap.getAllValues(prefix).stream())
                .anyMatch(value -> PREFIX_LIKE_TOKEN.matcher(value).find());
    }

    /**
     * Throws a {@code ParseException} naming the first required prefix that is absent.
     */
    private static void verifyRequiredPrefixesPresent(ArgumentMultimap argMultimap) throws ParseException {
        Optional<Prefix> missing = REQUIRED_PREFIXES.stream()
                .filter(prefix -> argMultimap.getValue(prefix).isEmpty())
                .findFirst();
        if (missing.isPresent()) {
            throw new ParseException(String.format(MESSAGE_MISSING_PARAMETER, missing.get()) + "\n"
                    + AddCommand.MESSAGE_USAGE);
        }
    }

    /**
     * Throws a {@code ParseException} naming the first prefix that appears more than once.
     */
    private static void verifyNoRepeatedPrefixes(ArgumentMultimap argMultimap, Prefix... prefixes)
            throws ParseException {
        for (Prefix prefix : prefixes) {
            if (argMultimap.getAllValues(prefix).size() > 1) {
                throw new ParseException(String.format(MESSAGE_REPEATED_PARAMETER, PARAMETER_NAMES.get(prefix)));
            }
        }
    }

    /**
     * Parses {@code value} with {@code parser} if present, or returns null if the parameter was omitted.
     */
    private static <T> T parseOptional(Optional<String> value, FieldParser<T> parser) throws ParseException {
        return value.isPresent() ? parser.parse(value.get()) : null;
    }

    /**
     * Parses one field value, throwing a {@code ParseException} with the field's constraint message.
     */
    @FunctionalInterface
    private interface FieldParser<T> {
        T parse(String value) throws ParseException;
    }

}
