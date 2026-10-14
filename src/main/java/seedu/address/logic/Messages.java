package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX = "The student index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d student(s) listed!";
    public static final String NOT_PROVIDED = "Not provided";
    public static final String NO_FEE = "None";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the complete record of {@code person} for display to the user, one field per line.
     * Missing contact fields are shown as {@value #NOT_PROVIDED} and an absent fee as {@value #NO_FEE}.
     */
    public static String format(Person person) {
        return "Name: " + person.getName()
                + "\nPhone: " + person.getPhone().map(Object::toString).orElse(NOT_PROVIDED)
                + "\nEmail: " + person.getEmail().map(Object::toString).orElse(NOT_PROVIDED)
                + "\nAddress: " + person.getAddress().map(Object::toString).orElse(NOT_PROVIDED)
                + "\nGuardian: " + person.getGuardianName()
                + "\nGuardian phone: " + person.getGuardianPhone()
                + "\nOutstanding fee: " + person.getOutstandingFee().map(fee -> "S$" + fee).orElse(NO_FEE);
    }

}
