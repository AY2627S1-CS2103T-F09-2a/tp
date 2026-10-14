package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.text.Normalizer;

/**
 * Represents a student's address.
 * Guarantees: immutable; normalized to NFC with single ASCII spaces; is valid as declared in
 * {@link #isValidAddress(String)}
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS =
            "Addresses must be 1-150 printable characters and must not contain line breaks.";

    /*
     * Letters, combining marks, numbers, punctuation, symbols and ASCII spaces only,
     * so control characters and line breaks are rejected.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{M}\\p{N}\\p{P}\\p{S} ]+";

    private static final int MAX_LENGTH = 150;

    public final String value;

    /**
     * Constructs an {@code Address}, normalizing Unicode and ASCII spaces.
     *
     * @param address A valid address.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        value = normalize(address);
    }

    /**
     * Returns true if the address has 1-150 printable code points after normalization.
     */
    public static boolean isValidAddress(String test) {
        requireNonNull(test);
        String normalized = normalize(test);
        int length = normalized.codePointCount(0, normalized.length());
        return length >= 1 && length <= MAX_LENGTH && normalized.matches(VALIDATION_REGEX);
    }

    /**
     * Returns the NFC address with surrounding ASCII spaces removed and internal ASCII spaces collapsed.
     */
    private static String normalize(String address) {
        return Normalizer.normalize(address, Normalizer.Form.NFC)
                .replaceAll("^ +| +$", "")
                .replaceAll(" +", " ");
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
        if (!(other instanceof Address otherAddress)) {
            return false;
        }

        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
