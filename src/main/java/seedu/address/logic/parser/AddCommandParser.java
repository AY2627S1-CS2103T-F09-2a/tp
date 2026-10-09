package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OUTSTANDING_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(" " + args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE, PREFIX_OUTSTANDING_FEE, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        rejectUnexpectedPrefixes(args);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE, PREFIX_OUTSTANDING_FEE);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = argMultimap.getValue(PREFIX_PHONE).isPresent()
                ? ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()) : Phone.empty();
        Email email = argMultimap.getValue(PREFIX_EMAIL).isPresent()
                ? ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()) : Email.empty();
        Address address = argMultimap.getValue(PREFIX_ADDRESS).isPresent()
                ? ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()) : Address.empty();
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        String guardianNameValue = argMultimap.getValue(PREFIX_GUARDIAN_NAME).get();
        if (!GuardianName.isValidName(guardianNameValue)) {
            throw new ParseException(GuardianName.MESSAGE_CONSTRAINTS);
        }
        String guardianPhoneValue = argMultimap.getValue(PREFIX_GUARDIAN_PHONE).get();
        if (!GuardianPhone.isValidPhone(guardianPhoneValue)) {
            throw new ParseException(GuardianPhone.MESSAGE_CONSTRAINTS);
        }
        GuardianName guardianName = new GuardianName(guardianNameValue);
        GuardianPhone guardianPhone = new GuardianPhone(guardianPhoneValue);
        OutstandingFee outstandingFee = null;
        if (argMultimap.getValue(PREFIX_OUTSTANDING_FEE).isPresent()) {
            String fee = argMultimap.getValue(PREFIX_OUTSTANDING_FEE).get();
            if (!OutstandingFee.isValidOutstandingFee(fee)) {
                throw new ParseException(OutstandingFee.MESSAGE_CONSTRAINTS);
            }
            outstandingFee = new OutstandingFee(fee);
        }

        Person person = new Person(name, phone, email, address, guardianName, guardianPhone, outstandingFee, tagList);

        return new AddCommand(person);
    }

    private static void rejectUnexpectedPrefixes(String args) throws ParseException {
        Set<String> acceptedPrefixes = Set.of("n/", "p/", "e/", "a/", "g/", "gp/", "f/", "t/");
        Matcher matcher = Pattern.compile("(?:^|\\s)([A-Za-z]+/)").matcher(args);
        while (matcher.find()) {
            if (!acceptedPrefixes.contains(matcher.group(1))) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
