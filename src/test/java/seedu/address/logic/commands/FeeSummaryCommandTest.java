package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.FeeSummaryCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests exact totals independent of the displayed filter.
 */
public class FeeSummaryCommandTest {
    @Test
    public void execute_hiddenFees_usesAllStudentsWithoutChangingFilter() {
        Model model = new ModelManager();
        Person first = new PersonBuilder().withName("First Student").withOutstandingFee("99999.99").build();
        Person second = new PersonBuilder().withName("Second Student").withOutstandingFee("0.01").build();
        Person paid = new PersonBuilder().withName("Paid Student").build();
        model.addPerson(first);
        model.addPerson(second);
        model.addPerson(paid);
        model.updateFilteredPersonList(person -> person.equals(paid));
        AddressBook before = new AddressBook(model.getAddressBook());
        assertEquals("2 student(s) owe a total of S$100000.00.",
                new FeeSummaryCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(paid), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        assertFalse(new FeeSummaryCommand().shouldSaveAddressBook());
    }

    @Test
    public void execute_noFees_returnsZero() {
        Model model = new ModelManager();
        assertEquals("0 student(s) owe a total of S$0.00.",
                new FeeSummaryCommand().execute(model).getFeedbackToUser());
        model.addPerson(new PersonBuilder().build());
        assertEquals("0 student(s) owe a total of S$0.00.",
                new FeeSummaryCommand().execute(model).getFeedbackToUser());
    }

    @Test
    public void parse_extraArguments_rejects() throws Exception {
        FeeSummaryCommandParser parser = new FeeSummaryCommandParser();
        assertEquals(new FeeSummaryCommand(), parser.parse("  "));
        for (String input : new String[]{"1", "f/10", "all"}) {
            assertThrows(ParseException.class, () -> parser.parse(input));
        }
    }
}
