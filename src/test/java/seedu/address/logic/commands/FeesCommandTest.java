package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests filtering, list restoration and data preservation.
 */
public class FeesCommandTest {

    @Test
    public void execute_multipleFees_filtersWithoutChangingData() throws Exception {
        Model model = new ModelManager();
        Person first = new PersonBuilder().withName("First Student").withOutstandingFee("180").build();
        Person paid = new PersonBuilder().withName("Paid Student").build();
        Person last = new PersonBuilder().withName("Last Student").withOutstandingFee("99999.99").build();
        model.addPerson(first);
        model.addPerson(paid);
        model.addPerson(last);
        assertEquals("2 student(s) with outstanding fees.", new FeesCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(first, last), model.getFilteredPersonList());
        assertEquals(List.of(first, paid, last), model.getAddressBook().getPersonList());
        new ListCommand().execute(model);
        assertEquals(List.of(first, paid, last), model.getFilteredPersonList());
    }

    @Test
    public void execute_noFees_showsEmptyList() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        assertEquals("0 student(s) with outstanding fees.", new FeesCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }
}
