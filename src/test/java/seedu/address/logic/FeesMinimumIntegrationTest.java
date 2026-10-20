package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ListChangeListener;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies minimum-fee queries through the command dispatcher and storage boundary.
 */
public class FeesMinimumIntegrationTest {

    @TempDir
    private Path directory;

    @Test
    public void execute_minimumQueries_doNotWriteData() throws Exception {
        Model model = new ModelManager();
        Person below = new PersonBuilder().withName("Below Student").withOutstandingFee("99.99").build();
        Person equal = new PersonBuilder().withName("Equal Student").withOutstandingFee("100").build();
        Person paid = new PersonBuilder().withName("Paid Student").build();
        List<Person> students = List.of(below, equal, paid);
        students.forEach(model::addPerson);
        Path data = directory.resolve("students.json");
        Logic logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));

        logic.execute("find Below");
        assertEquals("1 student(s) with outstanding fees.", logic.execute("fees min/100").getFeedbackToUser());
        assertEquals(List.of(equal), logic.getFilteredPersonList());
        logic.execute("fees min/0");
        assertEquals(List.of(below, equal), logic.getFilteredPersonList());
        logic.execute("fees min/99999.99");
        assertEquals(List.of(), logic.getFilteredPersonList());
        logic.execute("list");
        assertEquals(students, logic.getFilteredPersonList());
        assertEquals(students, model.getAddressBook().getPersonList());
        assertFalse(Files.exists(data));
    }

    @Test
    public void execute_rejectedMinimum_preservesDataAndFilterWithoutListChanges() throws Exception {
        Model model = new ModelManager();
        Person below = new PersonBuilder().withName("Below Student").withOutstandingFee("50").build();
        Person owing = new PersonBuilder().withName("Owing Student").withOutstandingFee("100").build();
        model.addPerson(below);
        model.addPerson(owing);
        Path data = directory.resolve("students.json");
        Logic logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        logic.execute("fees min/100");
        AtomicInteger listChanges = new AtomicInteger();
        model.getFilteredPersonList().addListener((ListChangeListener<Person>) change -> listChanges.incrementAndGet());

        for (String arguments : new String[]{"min/", "min/-1", "min/100000", "min/1.234", "min/1e2",
            "min/100 min/200", "max/100", "min/100 extra", "min/100 max/200"}) {
            assertThrows(ParseException.class, () -> logic.execute("fees " + arguments));
            assertEquals(List.of(owing), logic.getFilteredPersonList());
            assertEquals(List.of(below, owing), model.getAddressBook().getPersonList());
        }
        assertEquals(0, listChanges.get());
        assertFalse(Files.exists(data));

        logic.execute("setoutstanding 1 0");
        assertEquals(List.of(), logic.getFilteredPersonList());
        Person cleared = new PersonBuilder(owing).withoutOutstandingFee().build();
        assertEquals(List.of(below, cleared), model.getAddressBook().getPersonList());
        logic.execute("list");
        assertEquals(List.of(below, cleared), logic.getFilteredPersonList());
    }
}
