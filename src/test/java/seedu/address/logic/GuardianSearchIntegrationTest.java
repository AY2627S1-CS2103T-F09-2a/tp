package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises guardian search through the real dispatcher and storage boundary.
 */
public class GuardianSearchIntegrationTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_guardianSearchAndPayment_usesDisplayedIndexAndSurvivesReload() throws Exception {
        Person hidden = new PersonBuilder().withName("Hidden Student").withGuardianName("Other Guardian")
                .withGuardianPhone("98765432").withOutstandingFee("250").build();
        Person first = new PersonBuilder().withName("First Sibling").withGuardianName("Former Guardian")
                .withGuardianPhone("92345678").withOutstandingFee("180").build();
        Person second = new PersonBuilder().withName("Second Sibling").withGuardianName("Mei Tan")
                .withGuardianPhone("92345678").withOutstandingFee("75.50").build();
        Model model = new ModelManager();
        for (Person person : List.of(hidden, first, second)) {
            model.addPerson(person);
        }
        Path data = directory.resolve("students.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(data);
        storage.saveAddressBook(model.getAddressBook());
        Logic logic = createLogic(model, storage);
        logic.execute("edit 2 g/Mei Tan");
        Person editedFirst = new PersonBuilder(first).withGuardianName("Mei Tan").build();
        byte[] savedBeforeSearch = Files.readAllBytes(data);

        logic.execute("find Hidden");
        CommandResult result = logic.execute("findguardian mei 9234-5678");
        assertEquals("2 student(s) listed!", result.getFeedbackToUser());
        assertEquals(List.of(editedFirst, second), model.getFilteredPersonList());
        assertArrayEquals(savedBeforeSearch, Files.readAllBytes(data));
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());

        logic.execute("paid 1");
        logic.execute("fees min/100");
        assertEquals(List.of(hidden), model.getFilteredPersonList());
        assertEquals("Across all stored students: 2 student(s) owe a total of S$325.50. Displayed list unchanged.",
                logic.execute("feesummary").getFeedbackToUser());

        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic reloaded = createLogic(restored, storage);
        reloaded.execute("findguardian 92345678");
        assertEquals(2, restored.getFilteredPersonList().size());
        Person paid = restored.getFilteredPersonList().get(0);
        assertEquals(first.getName(), paid.getName());
        assertEquals(editedFirst.getGuardianName(), paid.getGuardianName());
        assertEquals(first.getGuardianPhone(), paid.getGuardianPhone());
        assertTrue(paid.getOutstandingFee().isEmpty());
        assertEquals(second, restored.getFilteredPersonList().get(1));
    }

    @Test
    public void execute_invalidSearchOrHelp_preservesRecordsFilterAndExistingFile() throws Exception {
        Person person = new PersonBuilder().withGuardianName("Mei Tan").withGuardianPhone("92345678").build();
        Model model = new ModelManager();
        model.addPerson(person);
        model.updateFilteredPersonList(ignored -> false);
        AddressBook before = new AddressBook(model.getAddressBook());
        Path data = directory.resolve("students.json");
        Files.writeString(data, "Read-only commands must preserve this sentinel.");
        Logic logic = createLogic(model, new JsonAddressBookStorage(data));
        for (String input : List.of("findguardian", "findguardian g/Mei", "help findguardian extra")) {
            assertThrows(ParseException.class, () -> logic.execute(input));
            assertTrue(model.getFilteredPersonList().isEmpty());
        }
        assertTrue(logic.execute("help findguardian").getFeedbackToUser().startsWith("findguardian:"));
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(before, model.getAddressBook());
        assertEquals("Read-only commands must preserve this sentinel.", Files.readString(data));
    }

    private Logic createLogic(Model model, JsonAddressBookStorage storage) {
        return new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
    }
}
