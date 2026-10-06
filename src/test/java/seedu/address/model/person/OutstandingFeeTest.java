package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class OutstandingFeeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new OutstandingFee(null));
    }

    @Test
    public void constructor_invalidOutstandingFee_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, OutstandingFee.MESSAGE_CONSTRAINTS, () ->
                new OutstandingFee("0"));
    }

    @Test
    public void isValidOutstandingFee() {
        // null outstanding fee
        assertThrows(NullPointerException.class, () -> OutstandingFee.isValidOutstandingFee(null));

        // invalid formats
        assertFalse(OutstandingFee.isValidOutstandingFee(""));
        assertFalse(OutstandingFee.isValidOutstandingFee(" "));
        assertFalse(OutstandingFee.isValidOutstandingFee("1."));
        assertFalse(OutstandingFee.isValidOutstandingFee(".01"));
        assertFalse(OutstandingFee.isValidOutstandingFee("1.001")); // excess precision
        assertFalse(OutstandingFee.isValidOutstandingFee("$1.00"));
        assertFalse(OutstandingFee.isValidOutstandingFee("1e2"));
        assertFalse(OutstandingFee.isValidOutstandingFee("\u0661.\u0660\u0660")); // non-ASCII digits

        // out-of-range values
        assertFalse(OutstandingFee.isValidOutstandingFee("0"));
        assertFalse(OutstandingFee.isValidOutstandingFee("0.00"));
        assertFalse(OutstandingFee.isValidOutstandingFee("-0.01"));
        assertFalse(OutstandingFee.isValidOutstandingFee("100000"));
        assertFalse(OutstandingFee.isValidOutstandingFee("99999.991"));

        // valid outstanding fees
        assertTrue(OutstandingFee.isValidOutstandingFee("0.01"));
        assertTrue(OutstandingFee.isValidOutstandingFee("1"));
        assertTrue(OutstandingFee.isValidOutstandingFee("1.2"));
        assertTrue(OutstandingFee.isValidOutstandingFee("00001.20"));
        assertTrue(OutstandingFee.isValidOutstandingFee("99999.99"));
    }

    @Test
    public void constructor_validOutstandingFee_storesExactNormalizedValue() {
        OutstandingFee outstandingFee = new OutstandingFee("00001.2");

        assertEquals(new BigDecimal("1.20"), outstandingFee.value);
        assertEquals("1.20", outstandingFee.toString());
    }

    @Test
    public void toString_validOutstandingFee_formatsWithTwoDecimalPlaces() {
        assertEquals("1.00", new OutstandingFee("1").toString());
        assertEquals("1.20", new OutstandingFee("1.2").toString());
        assertEquals("99999.99", new OutstandingFee("99999.99").toString());
    }

    @Test
    public void equals() {
        OutstandingFee outstandingFee = new OutstandingFee("1.00");

        // same normalized values -> returns true
        assertTrue(outstandingFee.equals(new OutstandingFee("01.0")));

        // same object -> returns true
        assertTrue(outstandingFee.equals(outstandingFee));

        // null -> returns false
        assertFalse(outstandingFee.equals(null));

        // different types -> returns false
        assertFalse(outstandingFee.equals(1.00));

        // different values -> returns false
        assertFalse(outstandingFee.equals(new OutstandingFee("1.01")));
    }
}
