package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;

/**
 * Represents a Person's outstanding fee in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidOutstandingFee(String)}
 */
public final class OutstandingFee {

    public static final String MESSAGE_CONSTRAINTS =
            "Outstanding fee must be between 0.01 and 99999.99, with at most two decimal places.";
    public static final String VALIDATION_REGEX = "[0-9]+(?:\\.[0-9]{1,2})?";

    private static final BigDecimal MINIMUM_FEE = new BigDecimal("0.01");
    private static final BigDecimal MAXIMUM_FEE = new BigDecimal("99999.99");

    public final BigDecimal value;

    /**
     * Constructs an {@code OutstandingFee}.
     *
     * @param outstandingFee A valid outstanding fee.
     */
    public OutstandingFee(String outstandingFee) {
        requireNonNull(outstandingFee);
        checkArgument(isValidOutstandingFee(outstandingFee), MESSAGE_CONSTRAINTS);
        value = new BigDecimal(outstandingFee).setScale(2);
    }

    /**
     * Returns true if a given string is a valid outstanding fee.
     */
    public static boolean isValidOutstandingFee(String test) {
        requireNonNull(test);
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }

        BigDecimal fee = new BigDecimal(test);
        return fee.compareTo(MINIMUM_FEE) >= 0 && fee.compareTo(MAXIMUM_FEE) <= 0;
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof OutstandingFee otherOutstandingFee)) {
            return false;
        }

        return value.equals(otherOutstandingFee.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
