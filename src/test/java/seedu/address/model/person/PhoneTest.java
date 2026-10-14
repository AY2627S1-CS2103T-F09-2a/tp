package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("9123456")); // 7 digits
        assertFalse(Phone.isValidPhone("912345678")); // 9 digits
        assertFalse(Phone.isValidPhone("12345678")); // starts with 1
        assertFalse(Phone.isValidPhone("71234567")); // starts with 7
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("+65 9123 4567")); // country code
        assertFalse(Phone.isValidPhone("9123 4567 x12")); // extension

        // valid phone numbers
        assertTrue(Phone.isValidPhone("61234567")); // starts with 6
        assertTrue(Phone.isValidPhone("81234567")); // starts with 8
        assertTrue(Phone.isValidPhone("93121534")); // starts with 9
        assertTrue(Phone.isValidPhone("9123 4567")); // space between digits
        assertTrue(Phone.isValidPhone("9123-4567")); // hyphen between digits
    }

    @Test
    public void constructor_separators_removedBeforeStorage() {
        assertEquals("91234567", new Phone("9123 4567").value);
        assertEquals("91234567", new Phone("9123-4567").value);
    }

    @Test
    public void equals() {
        Phone phone = new Phone("99999999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("99999999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("99999995")));
    }
}
