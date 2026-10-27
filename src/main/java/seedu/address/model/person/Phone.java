package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's phone number.
 * Guarantees: immutable; spaces and hyphens removed; valid as declared in {@link #isValidPhone(String)}.
 * Contains exactly eight digits starting with 6, 8, or 9; a leading Singapore country code {@code +65} is removed.
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers must be 8 digits starting with 6, 8, or 9, optionally after +65. "
            + "Spaces and hyphens are allowed.";
    public static final String VALIDATION_REGEX = "(?:\\+65[ -]*)?[689](?:[ -]*[0-9]){7}";
    private static final String COUNTRY_CODE = "+65";
    public final String value;

    /**
     * Constructs a {@code Phone}, removing surrounding ASCII spaces, internal separators and the country code.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        String digits = phone.replace(" ", "").replace("-", "");
        value = digits.startsWith(COUNTRY_CODE) ? digits.substring(COUNTRY_CODE.length()) : digits;
    }

    /**
     * Returns true if the phone has eight ASCII digits starting with 6, 8, or 9, optionally after {@code +65},
     * with surrounding ASCII spaces and spaces or hyphens between digits allowed.
     */
    public static boolean isValidPhone(String test) {
        requireNonNull(test);
        return test.replaceAll("^ +| +$", "").matches(VALIDATION_REGEX);
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
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
