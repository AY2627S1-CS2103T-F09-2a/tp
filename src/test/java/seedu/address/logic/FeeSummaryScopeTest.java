package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies all-student feedback through the dispatcher while a search hides outstanding fees.
 */
public class FeeSummaryScopeTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_filteredList_reportsAllStudentsWithoutSaving() throws Exception {
        Model model = new ModelManager();
        Person hidden = new PersonBuilder().withName("Hidden Student").withOutstandingFee("99999.99").build();
        Person visible = new PersonBuilder().withName("Visible Student").withOutstandingFee("0.01").build();
        model.addPerson(hidden);
        model.addPerson(visible);
        AddressBook before = new AddressBook(model.getAddressBook());
        Path data = directory.resolve("students.json");
        Logic logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));

        logic.execute("find Visible");
        assertEquals("Across all stored students: 2 student(s) owe a total of S$100000.00. Displayed list unchanged.",
                logic.execute("feesummary").getFeedbackToUser());
        assertEquals(List.of(visible), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        assertFalse(Files.exists(data));

        logic.execute("find Nobody");
        assertEquals("Across all stored students: 2 student(s) owe a total of S$100000.00. Displayed list unchanged.",
                logic.execute("feesummary").getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertThrows(ParseException.class, () -> logic.execute("feesummary extra"));
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        assertFalse(Files.exists(data));
    }
}
