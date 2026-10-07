package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GuardianPhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GuardianPhone(null));
        assertThrows(NullPointerException.class, () -> GuardianPhone.isValidPhone(null));
    }

    @Test
    public void constructor_invalidPhones_throwsIllegalArgumentException() {
        String[] invalidPhones = {
            "", " ", "123", "000", "9123456", "912345678", "12345678", "31234567", "71234567",
            "00000000", "+65 9123 4567", "+91234567", "65 9123 4567", "912345678901234", "phone",
            "9011p041", "-91234567", "91234567-", "++91234567", "9123+4567", "(91234567)",
            "91234567 ext 4", "9123\t4567", "\t91234567", "91234567\n", "9123\r4567", "9123\u00004567",
            "9123\u00a04567", "９１２３４５６７", "٩١٢٣٤٥٦٧"
        };
        for (String phone : invalidPhones) {
            assertFalse(GuardianPhone.isValidPhone(phone), phone);
            assertThrows(IllegalArgumentException.class, GuardianPhone.MESSAGE_CONSTRAINTS, () -> {
                new GuardianPhone(phone);
            });
        }
    }

    @Test
    public void constructor_validPhones_success() {
        String[] validPhones = {
            "61234567", "81234567", "91234567", "60000000", "99999999", "9123 4567", "9123-4567",
            "  92345678  ", "9 -- 2  3-4 5-6 7 8"
        };
        for (String phone : validPhones) {
            assertTrue(GuardianPhone.isValidPhone(phone), phone);
            new GuardianPhone(phone);
        }
    }

    @Test
    public void constructor_spacesAndHyphens_normalizes() {
        GuardianPhone phone = new GuardianPhone("  9666-4321  ");
        assertEquals("96664321", phone.getValue());
        assertEquals("96664321", phone.toString());
        assertEquals("96664321", new GuardianPhone("9666 4321").getValue());
    }

    @Test
    public void equals_normalizedNumbers_comparesDigits() {
        GuardianPhone phone = new GuardianPhone("92345678");
        GuardianPhone formattedPhone = new GuardianPhone("  9234-5678  ");
        assertTrue(phone.equals(phone));
        assertTrue(phone.equals(formattedPhone));
        assertEquals(phone.hashCode(), formattedPhone.hashCode());
        assertFalse(phone.equals(null));
        assertFalse(phone.equals("92345678"));
        assertFalse(phone.equals(new GuardianPhone("92345679")));
    }

    @Test
    public void constructor_invalidPhone_usesSpecifiedErrorMessage() {
        String expectedMessage =
                "Phone numbers must be 8 digits starting with 6, 8, or 9. Spaces and hyphens are allowed.";
        assertThrows(IllegalArgumentException.class, expectedMessage, () -> {
            new GuardianPhone("12345678");
        });
    }
}
