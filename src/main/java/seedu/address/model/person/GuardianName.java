package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a guardian's name.
 * Guarantees: immutable; normalized to NFC with single ASCII spaces; valid as declared in
 * {@link #isValidName(String)}. Capitalization is preserved for display; comparison ignores case.
 */
public final class GuardianName {

    public static final String MESSAGE_CONSTRAINTS =
            "Guardian names must be 1-70 characters long, contain at least one letter, and use only letters, "
            + "numbers, spaces, apostrophes, hyphens, periods, parentheses, or S/O and D/O as separate words "
            + "between names.";
    public static final String VALIDATION_REGEX = Name.VALIDATION_REGEX;

    private final String fullName;

    /**
     * Constructs a {@code GuardianName}, normalizing Unicode and ASCII spaces.
     *
     * @param name A valid guardian name.
     */
    public GuardianName(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = Name.normalize(name);
    }

    /**
     * Returns true if the name is valid after normalization, with 1-70 Unicode code points
     * and at least one letter outside S/O and D/O. Non-ASCII whitespace and control characters are rejected.
     */
    public static boolean isValidName(String test) {
        // A guardian name follows the same rules as a student name.
        return Name.isValidName(test);
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return fullName;
    }

    /**
     * Returns true if both names have the same normalized value, ignoring case.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GuardianName otherName)) {
            return false;
        }

        return fullName.equalsIgnoreCase(otherName.fullName);
    }

    @Override
    public int hashCode() {
        // Use the same character case folding as String.equalsIgnoreCase.
        return fullName.codePoints()
                .map(Character::toUpperCase)
                .map(Character::toLowerCase)
                .reduce(0, (hash, codePoint) -> 31 * hash + codePoint);
    }
}
