package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.PaidCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests payment success and failure atomicity with active filters.
 */
public class PaidCommandTest {
    @Test
    public void execute_feesFilter_clearsOnlyDisplayedStudentAndReindexes() throws Exception {
        Model model = new ModelManager();
        Person hidden = new PersonBuilder().withName("Hidden Student").build();
        Person first = new PersonBuilder().withName("First Student").withoutContactDetails()
                .withOutstandingFee("0.01").build();
        Person second = new PersonBuilder().withName("Second Student").withOutstandingFee("99999.99").build();
        model.addPerson(hidden);
        model.addPerson(first);
        model.addPerson(second);
        new FeesCommand().execute(model);
        PaidCommand command = new PaidCommandParser().parse(" 1 ");
        assertEquals("Marked outstanding fee as paid for First Student: S$0.01",
                command.execute(model).getFeedbackToUser());
        assertEquals(List.of(second), model.getFilteredPersonList());
        assertEquals(List.of(hidden, new PersonBuilder(first).withoutOutstandingFee().build(), second),
                model.getAddressBook().getPersonList());
        assertTrue(command.shouldSaveAddressBook());
    }

    @Test
    public void execute_nameFilter_keepsPaidStudentVisible() throws Exception {
        Model model = new ModelManager();
        Person student = new PersonBuilder().withOutstandingFee("12.30").build();
        model.addPerson(student);
        model.updateFilteredPersonList(person -> person.getName().equals(student.getName()));
        new PaidCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(new PersonBuilder(student).withoutOutstandingFee().build()),
                model.getFilteredPersonList());
    }

    @Test
    public void execute_noFeeOrOutOfRange_rejectsWithoutChangingState() {
        Model model = new ModelManager();
        Person student = new PersonBuilder().build();
        model.addPerson(student);
        AddressBook before = new AddressBook(model.getAddressBook());
        assertThrows(CommandException.class, () -> new PaidCommand(Index.fromOneBased(1)).execute(model));
        assertThrows(CommandException.class, () -> new PaidCommand(Index.fromOneBased(2)).execute(model));
        assertEquals(before, model.getAddressBook());
        assertEquals(List.of(student), model.getFilteredPersonList());
        model.updateFilteredPersonList(person -> false);
        assertThrows(CommandException.class, () -> new PaidCommand(Index.fromOneBased(1)).execute(model));
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void parse_invalidIndex_rejects() {
        PaidCommandParser parser = new PaidCommandParser();
        for (String input : new String[]{"", "0", "-1", "+1", "01", "1.0", "one", "1 2", "2147483648"}) {
            assertThrows(ParseException.class, () -> parser.parse(input), input);
        }
    }
}
