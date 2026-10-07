package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.text.Normalizer;

/**
 * Represents a guardian's name.
 * Guarantees: immutable; normalized to NFC with single ASCII spaces; valid as declared in
 * {@link #isValidName(String)}. Capitalization is preserved for display; comparison ignores case.
 */
public final class GuardianName {

    public static final String MESSAGE_CONSTRAINTS =
            "Names must be 1-70 characters long, contain at least one letter, and use only letters, numbers, "
            + "spaces, apostrophes, hyphens, periods, or parentheses.";
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{M}0-9 '.()\\-]+";

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 70;

    private final String fullName;

    /**
     * Constructs a {@code GuardianName}, normalizing Unicode and ASCII spaces.
     *
     * @param name A valid guardian name.
     */
    public GuardianName(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = normalize(name);
    }

    /**
     * Returns true if the name is valid after normalization, with 1-70 Unicode code points
     * and at least one letter. Non-ASCII whitespace and control characters are rejected.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        String normalized = normalize(test);
        int length = normalized.codePointCount(0, normalized.length());
        return length >= MIN_NAME_LENGTH && length <= MAX_NAME_LENGTH
                && normalized.matches(VALIDATION_REGEX)
                && normalized.codePoints().anyMatch(Character::isLetter);
    }

    /**
     * Returns the NFC name with surrounding ASCII spaces removed and internal ASCII spaces collapsed.
     */
    private static String normalize(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFC)
                .replaceAll("^ +| +$", "")
                .replaceAll(" +", " ");
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
