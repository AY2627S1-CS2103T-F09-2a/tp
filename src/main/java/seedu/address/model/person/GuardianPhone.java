package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a guardian's phone number.
 * Guarantees: immutable; spaces and hyphens removed; valid as declared in {@link #isValidPhone(String)}.
 * Contains exactly eight digits starting with 6, 8, or 9; a leading Singapore country code {@code +65} is removed.
 */
public final class GuardianPhone {

    public static final String MESSAGE_CONSTRAINTS = "Guardian phone numbers must be 8 digits starting with 6, 8, "
            + "or 9, optionally after +65. Spaces and hyphens are allowed.";
    public static final String VALIDATION_REGEX = Phone.VALIDATION_REGEX;

    private final String value;

    /**
     * Constructs a {@code GuardianPhone}, removing surrounding ASCII spaces and internal separators.
     *
     * @param phone A valid guardian phone number.
     */
    public GuardianPhone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = Phone.normalize(phone);
    }

    /**
     * Returns true if the phone has eight ASCII digits starting with 6, 8, or 9, optionally after {@code +65},
     * with surrounding ASCII spaces and spaces or hyphens between digits allowed.
     */
    public static boolean isValidPhone(String test) {
        return Phone.isValidPhone(test);
    }

    public String getValue() {
        return value;
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

        if (!(other instanceof GuardianPhone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
