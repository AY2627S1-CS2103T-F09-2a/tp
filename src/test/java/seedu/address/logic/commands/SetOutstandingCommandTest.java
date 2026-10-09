package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests replacing and clearing fees in displayed lists and persisted records.
 */
public class SetOutstandingCommandTest {

    @TempDir
    private java.nio.file.Path directory;

    @Test
    public void execute_setReplaceClear_preservesOtherFieldsAndPersists() throws Exception {
        Model model = new ModelManager();
        Person student = new PersonBuilder().build();
        model.addPerson(student);
        new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("150.50")).execute(model);
        Person updated = model.getFilteredPersonList().get(0);
        assertEquals(new OutstandingFee("150.50"), updated.getOutstandingFee());
        assertEquals(student.getGuardianName(), updated.getGuardianName());
        assertEquals(student.getGuardianPhone(), updated.getGuardianPhone());
        assertEquals(student.getTags(), updated.getTags());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("students.json"));
        storage.saveAddressBook(model.getAddressBook());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("1")).execute(model);
        assertEquals(new OutstandingFee("1.00"), model.getFilteredPersonList().get(0).getOutstandingFee());
        new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        assertNull(model.getFilteredPersonList().get(0).getOutstandingFee());
        storage.saveAddressBook(model.getAddressBook());
        assertNull(storage.readAddressBook().orElseThrow().getPersonList().get(0).getOutstandingFee());
    }

    @Test
    public void equals_distinguishesIndexAndFee() {
        SetOutstandingCommand clearFirst = new SetOutstandingCommand(Index.fromOneBased(1), null);
        assertTrue(clearFirst.equals(clearFirst));
        assertTrue(clearFirst.equals(new SetOutstandingCommand(Index.fromOneBased(1), null)));
        assertFalse(clearFirst.equals(new SetOutstandingCommand(Index.fromOneBased(2), null)));
        assertFalse(clearFirst.equals(new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("1.00"))));
        assertFalse(clearFirst.equals(null));
        assertFalse(clearFirst.equals("not a command"));
    }

    @Test
    public void execute_feesListClear_removesStudentAndReindexes() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Paid Student").build());
        model.addPerson(new PersonBuilder().withName("First Owing").withOutstandingFee("2").build());
        Person remaining = new PersonBuilder().withName("Last Owing").withOutstandingFee("3").build();
        model.addPerson(remaining);
        new FeesCommand().execute(model);
        new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        assertEquals(List.of(remaining), model.getFilteredPersonList());
        assertEquals(3, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_findListRetainsPredicateAndInvalidIndexPreservesState() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        model.addPerson(new PersonBuilder().withName("Other Student").build());
        model.updateFilteredPersonList(person -> person.getName().fullName.equals("Amy Bee"));
        new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("0.01")).execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        Person before = model.getFilteredPersonList().get(0);
        assertThrows(CommandException.class,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () -> {
                    new SetOutstandingCommand(Index.fromOneBased(2), null).execute(model);
                });
        assertEquals(before, model.getFilteredPersonList().get(0));
    }
}
