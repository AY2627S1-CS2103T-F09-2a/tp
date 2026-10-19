package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
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
 * Exercises help for every implemented command without executing its action or writing data.
 */
public class CommandHelpLogicTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_allHelpTargets_preservesFilteredRecordsAndExistingFile() throws Exception {
        Model model = new ModelManager();
        Person hidden = new PersonBuilder().withName("Hidden Student").withOutstandingFee("180").build();
        Person visible = new PersonBuilder().withName("Visible Student").withOutstandingFee("75.50").build();
        model.addPerson(hidden);
        model.addPerson(visible);
        model.updateFilteredPersonList(visible::equals);
        AddressBook before = new AddressBook(model.getAddressBook());
        Path data = directory.resolve("students.json");
        Files.writeString(data, "Existing data must not be overwritten by help.");
        Logic logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));

        List<String> targets = List.of("add", "clear", "delete", "edit", "exit", "fees", "feesummary", "find",
                "help", "list", "paid", "setoutstanding", "view");
        assertEquals(String.join(", ", targets), HelpCommand.getAvailableCommands());
        for (String target : targets) {
            CommandResult result = logic.execute("help " + target);
            assertTrue(result.getFeedbackToUser().startsWith(target + ":"), target);
            assertTrue(result.getFeedbackToUser().contains("Example: "), target);
            assertFalse(result.isShowHelp());
            assertFalse(result.isExit());
            assertTrue(result.getViewedPerson().isEmpty());
            assertEquals(List.of(visible), model.getFilteredPersonList());
            assertEquals(before, model.getAddressBook());
            assertEquals("Existing data must not be overwritten by help.", Files.readString(data));
        }

        assertTrue(logic.execute("help").isShowHelp());
        assertThrows(ParseException.class, () -> logic.execute("help paid 1"));
        assertThrows(ParseException.class, () -> logic.execute("help unknown"));
        assertEquals(List.of(visible), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        assertEquals("Existing data must not be overwritten by help.", Files.readString(data));
    }

    @Test
    public void execute_help_doesNotCreateDataFile() throws Exception {
        Path data = directory.resolve("students.json");
        Logic logic = new LogicManager(new ModelManager(), new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        logic.execute("help");
        logic.execute("help clear");
        assertFalse(Files.exists(data));
    }
}
