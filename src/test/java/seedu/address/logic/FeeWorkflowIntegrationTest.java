package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies parsed fee commands, displayed indexes and persistence together.
 */
public class FeeWorkflowIntegrationTest {

    @TempDir
    private Path directory;

    @Test
    public void execute_feeWorkflow_preservesRecordsAndPersists() throws Exception {
        Model model = new ModelManager();
        Person paid = new PersonBuilder().withName("Paid Student").build();
        Person first = new PersonBuilder().withName("First Owing").withOutstandingFee("2").build();
        Person last = new PersonBuilder().withName("Last Owing").withOutstandingFee("3").build();
        model.addPerson(paid);
        model.addPerson(first);
        model.addPerson(last);
        JsonAddressBookStorage data = new JsonAddressBookStorage(directory.resolve("students.json"));
        Logic logic = new LogicManager(model,
                new StorageManager(data, new JsonUserPrefsStorage(directory.resolve("preferences.json"))));

        assertEquals("2 student(s) with outstanding fees.", logic.execute("fees").getFeedbackToUser());
        assertEquals(List.of(first, last), model.getFilteredPersonList());
        assertEquals(List.of(paid, first, last), model.getAddressBook().getPersonList());
        assertThrows(ParseException.class, () -> logic.execute("fees 1"));
        assertThrows(ParseException.class, () -> logic.execute("fees f/1"));
        assertEquals(List.of(first, last), model.getFilteredPersonList());

        logic.execute("setoutstanding 1 0");
        assertEquals(List.of(last), model.getFilteredPersonList());
        assertTrue(model.getAddressBook().getPersonList().get(1).getOutstandingFee().isEmpty());
        assertEquals("Set outstanding fee for Last Owing: S$15.50",
                logic.execute("setoutstanding 1 15.50").getFeedbackToUser());
        Person updated = new PersonBuilder(last).withOutstandingFee("15.50").build();
        assertEquals(List.of(updated), model.getFilteredPersonList());
        assertEquals(List.of(paid, new PersonBuilder(first).withOutstandingFee(null).build(), updated),
                model.getAddressBook().getPersonList());

        for (String command : new String[]{"setoutstanding", "setoutstanding 0 1", "setoutstanding 1 -1",
            "setoutstanding 1 100000", "setoutstanding 1 1.234", "setoutstanding 2147483648 1",
            "setoutstanding 1 2 3"}) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(List.of(updated), model.getFilteredPersonList());
        }
        assertThrows(CommandException.class, () -> logic.execute("setoutstanding 2 1"));
        assertEquals(List.of(updated), model.getFilteredPersonList());
        assertEquals(model.getAddressBook(), data.readAddressBook().orElseThrow());

        Model restarted = new ModelManager(new AddressBook(data.readAddressBook().orElseThrow()), new UserPrefs());
        Logic restartedLogic = new LogicManager(restarted,
                new StorageManager(data, new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        restartedLogic.execute("fees");
        assertEquals(List.of(updated), restarted.getFilteredPersonList());
        assertEquals(new OutstandingFee("15.50"), restarted.getFilteredPersonList().get(0)
                .getOutstandingFee().orElseThrow());
        restartedLogic.execute("list");
        assertEquals(3, restarted.getFilteredPersonList().size());
        restartedLogic.execute("find First");
        restartedLogic.execute("setoutstanding 1 0.01");
        assertEquals(1, restarted.getFilteredPersonList().size());
        restartedLogic.execute("setoutstanding 1 0");
        assertEquals(1, restarted.getFilteredPersonList().size());
        assertTrue(restarted.getFilteredPersonList().get(0).getOutstandingFee().isEmpty());
        assertEquals(restarted.getAddressBook(), data.readAddressBook().orElseThrow());
    }
}
