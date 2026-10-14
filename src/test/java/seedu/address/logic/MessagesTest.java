package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
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
}
