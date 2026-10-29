package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only disallowed characters
        assertFalse(Name.isValidName("peter*")); // contains a disallowed character
        assertFalse(Name.isValidName("12345")); // no letter
        assertFalse(Name.isValidName("Alex\tTan")); // control character
        assertFalse(Name.isValidName("A".repeat(71))); // 71 characters

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("A")); // 1 character
        assertTrue(Name.isValidName("A".repeat(70))); // 70 characters
        assertTrue(Name.isValidName("O'Brien-Lee Jr. (Sec 2)")); // apostrophe, hyphen, period, parentheses
        assertTrue(Name.isValidName("Nguyễn Thị Ánh")); // non-ASCII letters
        assertTrue(Name.isValidName("  Alex   Tan  ")); // extra spaces are collapsed
    }

    @Test
    public void constructor_extraSpaces_normalizesSpaces() {
        assertEquals("Alex Tan", new Name("  Alex   Tan ").fullName);
    }

    @Test
    public void isSameNameIgnoreCase() {
        Name name = new Name("Alex Tan");
        assertTrue(name.isSameNameIgnoreCase(new Name("alex  TAN")));
        assertFalse(name.isSameNameIgnoreCase(new Name("Alex Tan Jr")));
        assertFalse(name.isSameNameIgnoreCase(null));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }

    @Test
    public void isValidName_patronymic() {
        // S/O and D/O as whole words, in any case
        assertTrue(Name.isValidName("Ravi S/O Raj"));
        assertTrue(Name.isValidName("Priya d/o Kumar"));
        assertTrue(Name.isValidName("Ravi s/O Raj"));

        // other slashes are still rejected
        assertFalse(Name.isValidName("Ravi A/B Raj"));
        assertFalse(Name.isValidName("Ravi S/Oh Raj"));
        assertFalse(Name.isValidName("Ravi XS/O Raj"));
        assertFalse(Name.isValidName("S/O Raj")); // marker must come between names
        assertFalse(Name.isValidName("Ravi d/o"));
        assertFalse(Name.isValidName(" Ravi S/O "));
        assertFalse(Name.isValidName("S/O")); // marker only, no other letter
        assertFalse(Name.isValidName("D/O s/o")); // markers only
        assertFalse(Name.isValidName("Ravi / Raj"));
    }
}
