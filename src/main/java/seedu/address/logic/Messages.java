package seedu.address.logic;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_INDEX_CONSTRAINTS =
            "current displayed index, starting at 1, without leading zeros";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command: \"%1$s\".";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX = "The student index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d student(s) listed!";
    public static final String NOT_PROVIDED = "Not provided";
    public static final String NO_FEE = "None";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Alphabetical order ignoring case, then character code (uppercase first) for names that differ only in case.
     * The second rule matters only if the names to sort include some that differ only in case. It makes their order
     * the same every time, rather than dependent on the order in which they are given.
     * Names are compared character by character, so length matters only when one name starts with the other,
     * ignoring case: the shorter name comes first ({@code bio} before {@code biology}). Digits are compared as
     * characters too, so {@code Sec10} comes before {@code Sec3}.
     */
    private static final Comparator<String> TAG_NAME_ORDER =
            String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.naturalOrder());

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
     * Returns the details of {@code person} as text, one field per line, for example after {@code add} or
     * {@code edit}. A missing phone, email or address is shown as {@value #NOT_PROVIDED}, and a missing fee as
     * {@value #NO_FEE}. If {@code person} has tags, they are listed on the last line in the order of
     * {@link #sortTagNames(Collection)}.
     */
    public static String format(Person person) {
        return "Name: " + person.getName()
                + "\nPhone: " + person.getPhone().map(Object::toString).orElse(NOT_PROVIDED)
                + "\nEmail: " + person.getEmail().map(Object::toString).orElse(NOT_PROVIDED)
                + "\nAddress: " + person.getAddress().map(Object::toString).orElse(NOT_PROVIDED)
                + "\nGuardian: " + person.getGuardianName()
                + "\nGuardian phone: " + person.getGuardianPhone()
                + "\nOutstanding fee: " + person.getOutstandingFee().map(fee -> "S$" + fee).orElse(NO_FEE)
                + formatTags(person);
    }

    /**
     * Returns a line listing the tags of {@code person} in the order of {@link #sortTagNames(Collection)},
     * or an empty string if there are none.
     */
    private static String formatTags(Person person) {
        if (person.getTags().isEmpty()) {
            return "";
        }
        return "\nTags: " + String.join(", ", sortTagNames(person.getTags()));
    }

    /**
     * Returns the names of {@code tags} in alphabetical order, ignoring case, so that {@code aMath} comes before
     * {@code biology} and {@code Chemistry}. Names that differ only in case are then ordered by character code
     * (uppercase first), so {@code Math} always comes before {@code math}.
     */
    public static List<String> sortTagNames(Collection<Tag> tags) {
        requireNonNull(tags);
        return tags.stream()
                .map(tag -> tag.tagName)
                .sorted(TAG_NAME_ORDER)
                .toList();
    }

}
