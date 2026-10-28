package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GuardianNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GuardianName(null));
        assertThrows(NullPointerException.class, () -> GuardianName.isValidName(null));
    }

    @Test
    public void constructor_invalidNames_throwsIllegalArgumentException() {
        String[] invalidNames = {
            "", "   ", "12345", "-.'()", "\u0301", "Tan*", "Tan/Mei", "A\tB", "\tTan", "Tan\n", "A\rB",
            "A\u0000B", "A\u00a0B", "A\u200bB", "A\u2028B", "A😀", "A１", "a".repeat(71)
        };
        for (String name : invalidNames) {
            assertFalse(GuardianName.isValidName(name), name);
            assertThrows(IllegalArgumentException.class, GuardianName.MESSAGE_CONSTRAINTS, () -> {
                new GuardianName(name);
            });
        }
    }

    @Test
    public void constructor_validNames_success() {
        String[] validNames = {
            "A", "Tan Mei Ling", "Mary-Jane O'Neil", "王小明", "Alex Tan (Sec 2)", "Dr. Tan", "José", "नमस्ते",
            "  Tan   Mei Ling  ", "a".repeat(70)
        };
        for (String name : validNames) {
            assertTrue(GuardianName.isValidName(name), name);
            new GuardianName(name);
        }
    }

    @Test
    public void constructor_unicodeAndRepeatedSpaces_normalizesAndPreservesCapitalization() {
        GuardianName name = new GuardianName("  Jose\u0301   Tan  ");
        assertEquals("José Tan", name.getFullName());
        assertEquals("José Tan", name.toString());
        assertEquals(new GuardianName("José Tan"), name);
        assertEquals(new GuardianName("José Tan").hashCode(), name.hashCode());
    }

    @Test
    public void isValidName_lengthBoundaries_countsNormalizedUnicodeCodePoints() {
        assertTrue(GuardianName.isValidName("e\u0301".repeat(70)));
        assertFalse(GuardianName.isValidName("e\u0301".repeat(71)));
        String supplementaryLetter = "\ud801\udc00";
        assertTrue(GuardianName.isValidName(supplementaryLetter.repeat(70)));
        assertFalse(GuardianName.isValidName(supplementaryLetter.repeat(71)));
    }

    @Test
    public void equals_normalizedDisplayValues_ignoresCase() {
        GuardianName name = new GuardianName("Tan Mei Ling");
        assertTrue(name.equals(name));
        assertTrue(name.equals(new GuardianName("  Tan  Mei Ling  ")));
        assertFalse(name.equals(null));
        assertFalse(name.equals("Tan Mei Ling"));
        assertFalse(name.equals(new GuardianName("Other Name")));
        GuardianName differentlyCasedName = new GuardianName("  tAN  mEI lING  ");
        assertTrue(name.equals(differentlyCasedName));
        assertEquals(name.hashCode(), differentlyCasedName.hashCode());
        assertEquals("tAN mEI lING", differentlyCasedName.getFullName());
        assertFalse(new GuardianName("José").equals(new GuardianName("Jose")));
    }

    @Test
    public void equals_unicodeCaseVariants_hasConsistentHashCode() {
        String[][] namePairs = {
            {"José", "JOSÉ"}, {"Σ", "ς"}, {"I", "\u0131"}, {"i", "\u0130"},
            {"\ud801\udc00", "\ud801\udc28"}
        };
        for (String[] pair : namePairs) {
            GuardianName first = new GuardianName(pair[0]);
            GuardianName second = new GuardianName(pair[1]);
            assertEquals(first, second);
            assertEquals(second, first);
            assertEquals(first.hashCode(), second.hashCode());
        }
    }

    @Test
    public void isValidName_patronymic() {
        // S/O and D/O as whole words, in any case
        assertTrue(GuardianName.isValidName("Ravi S/O Raj"));
        assertTrue(GuardianName.isValidName("Priya d/o Kumar"));
        assertTrue(GuardianName.isValidName("Ravi s/O Raj"));

        // other slashes are still rejected
        assertFalse(GuardianName.isValidName("Ravi A/B Raj"));
        assertFalse(GuardianName.isValidName("Ravi S/Oh Raj"));
        assertFalse(GuardianName.isValidName("Ravi XS/O Raj"));
        assertFalse(GuardianName.isValidName("Ravi / Raj"));
    }

    @Test
    public void messageConstraints_describesSupportedCharacters() {
        assertEquals("Guardian names must be 1-70 characters long, contain at least one letter, and use only letters, "
                + "numbers, spaces, straight apostrophes ('), ASCII hyphens (-), periods, parentheses, or S/O and D/O "
                + "as separate words.",
                GuardianName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void isValidName_patronymicMarkersOnly_returnsFalse() {
        assertFalse(GuardianName.isValidName("S/O"));
        assertFalse(GuardianName.isValidName("D/O D/O"));
        assertTrue(GuardianName.isValidName("Ravi S/O Kumar"));
    }

}
