package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_withOutstandingFee_includesAllFields() {
        Person person = new PersonBuilder().withGuardianName("Margaret Tan").withGuardianPhone("98765432")
                .withOutstandingFee("120.50").withTags("sec3").build();
        assertEquals("Amy Bee; Phone: 85355255; Email: amy@gmail.com; Address: 123, Jurong West Ave 6, #08-111; "
                + "Guardian: Margaret Tan; Guardian Phone: 98765432; Outstanding Fee: $120.50; Tags: [sec3]",
                Messages.format(person));
    }

    @Test
    public void format_withoutOutstandingFee_omitsFee() {
        Person person = new PersonBuilder().build();
        assertEquals("Amy Bee; Phone: 85355255; Email: amy@gmail.com; Address: 123, Jurong West Ave 6, #08-111; "
                + "Guardian: Guardian; Guardian Phone: 91234567; Tags: ",
                Messages.format(person));
    }
}
