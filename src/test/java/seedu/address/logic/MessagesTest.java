package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_withOutstandingFee_includesStudentGuardianAndFee() {
        Person person = new PersonBuilder().withGuardianName("Margaret Tan").withGuardianPhone("98765432")
                .withOutstandingFee("120.50").withTags("sec3").build();
        assertEquals("Name: Amy Bee\nPhone: 85355255\nEmail: amy@gmail.com\nAddress: 123, Jurong West Ave 6, #08-111"
                + "\nGuardian: Margaret Tan\nGuardian phone: 98765432\nOutstanding fee: S$120.50\nTags: sec3",
                Messages.format(person));
    }

    @Test
    public void format_withoutOutstandingFee_showsNone() {
        Person person = new PersonBuilder().build();
        assertEquals("Name: Amy Bee\nPhone: 85355255\nEmail: amy@gmail.com\nAddress: 123, Jurong West Ave 6, #08-111"
                + "\nGuardian: Guardian\nGuardian phone: 91234567\nOutstanding fee: None",
                Messages.format(person));
    }

    @Test
    public void format_multipleTags_listsTagsAlphabetically() {
        Person person = new PersonBuilder().withTags("sec3", "math").build();
        assertEquals("Name: Amy Bee\nPhone: 85355255\nEmail: amy@gmail.com\nAddress: 123, Jurong West Ave 6, #08-111"
                + "\nGuardian: Guardian\nGuardian phone: 91234567\nOutstanding fee: None\nTags: math, sec3",
                Messages.format(person));
    }

    @Test
    public void format_mixedCaseTags_listsTagsAlphabeticallyIgnoringCase() {
        Person person = new PersonBuilder().withTags("biology", "Chemistry", "aMath").build();
        assertEquals("Name: Amy Bee\nPhone: 85355255\nEmail: amy@gmail.com\nAddress: 123, Jurong West Ave 6, #08-111"
                + "\nGuardian: Guardian\nGuardian phone: 91234567\nOutstanding fee: None"
                + "\nTags: aMath, biology, Chemistry",
                Messages.format(person));
    }

    @Test
    public void sortTagNames_mixedCase_sortsAlphabeticallyIgnoringCase() {
        assertEquals(List.of("aMath", "biology", "Chemistry"),
                Messages.sortTagNames(toTags("Chemistry", "aMath", "biology")));
        assertEquals(List.of("AMath", "math", "Sec3"), Messages.sortTagNames(toTags("Sec3", "math", "AMath")));
    }

    @Test
    public void sortTagNames_namesDifferingOnlyInCase_ordersByCharacterCode() {
        assertEquals(List.of("MATH", "Math", "math"), Messages.sortTagNames(toTags("math", "MATH", "Math")));
        assertEquals(List.of("MATH", "Math", "math"), Messages.sortTagNames(toTags("Math", "math", "MATH")));
    }

    @Test
    public void sortTagNames_noTags_returnsEmptyList() {
        assertEquals(List.of(), Messages.sortTagNames(List.of()));
    }

    @Test
    public void sortTagNames_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Messages.sortTagNames(null));
    }

    /**
     * Returns tags with {@code names}, in the given order. A list is used rather than a set, so that names that differ
     * only in case are kept even if tags are compared without regard to case.
     */
    private static List<Tag> toTags(String... names) {
        return Stream.of(names).map(Tag::new).toList();
    }
}
