package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.text.Normalizer;

/**
 * Represents a student's name.
 * Guarantees: immutable; normalized to NFC with single ASCII spaces; valid as declared in
 * {@link #isValidName(String)}. Capitalization is preserved for display.
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names must be 1-70 characters long, contain at least one letter, and use only letters, numbers, "
            + "spaces, apostrophes, hyphens, periods, parentheses, or S/O and D/O as separate words.";
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{M}0-9 '.()\\-]+";
    /** Matches S/O or D/O (son of, daughter of) written as a separate word, in any case. */
    public static final String PATRONYMIC_REGEX = "(?<=^| )[SsDd]/[Oo](?= |$)";

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 70;

    public final String fullName;

    /**
     * Constructs a {@code Name}, normalizing Unicode and ASCII spaces.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = normalize(name);
    }

    /**
     * Returns true if the name is valid after normalization, with 1-70 Unicode code points
     * and at least one letter outside S/O and D/O. Non-ASCII whitespace and control characters are rejected.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        String normalized = normalize(test);
        int length = normalized.codePointCount(0, normalized.length());
        return length >= MIN_NAME_LENGTH && length <= MAX_NAME_LENGTH
                && normalized.replaceAll(PATRONYMIC_REGEX, "SO").matches(VALIDATION_REGEX)
                && normalized.replaceAll(PATRONYMIC_REGEX, "").codePoints().anyMatch(Character::isLetter);
    }

    /**
     * Returns the NFC name with surrounding ASCII spaces removed and internal ASCII spaces collapsed.
     */
    static String normalize(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFC)
                .replaceAll("^ +| +$", "")
                .replaceAll(" +", " ");
    }

    /**
     * Returns true if both names are the same, ignoring case.
     */
    public boolean isSameNameIgnoreCase(Name otherName) {
        return otherName != null && fullName.equalsIgnoreCase(otherName.fullName);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
