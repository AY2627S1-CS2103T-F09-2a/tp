package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's email.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    public static final String MESSAGE_CONSTRAINTS =
            "Emails must be one valid address such as student@example.com, without spaces. The part before @ "
            + "may use only letters, digits, periods, underscores, plus signs and hyphens.";

    private static final int MAX_LENGTH = 254;
    private static final String LOCAL_PART_REGEX = "(?=.{1,64}@)[A-Za-z0-9_+\\-]+(\\.[A-Za-z0-9_+\\-]+)*";
    private static final String DOMAIN_LABEL_REGEX = "[A-Za-z0-9](?:[A-Za-z0-9\\-]{0,61}[A-Za-z0-9])?";
    private static final String FINAL_LABEL_REGEX = "[A-Za-z]{2,63}";
    public static final String VALIDATION_REGEX =
            LOCAL_PART_REGEX + "@(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + FINAL_LABEL_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    /**
     * Returns true if a given string is a valid email of at most 254 ASCII characters.
     */
    public static boolean isValidEmail(String test) {
        requireNonNull(test);
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
