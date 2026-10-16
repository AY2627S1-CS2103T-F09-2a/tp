package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the real command dispatcher and automatic-save boundary for v1.4 commands.
 */
public class V14FeeLogicManagerTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_payment_savesAutomatically() throws Exception {
        Path data = directory.resolve("students.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(data);
        Model model = new ModelManager();
        Person original = new PersonBuilder().withoutContactDetails().withOutstandingFee("180").build();
        model.addPerson(original);
        Logic logic = new LogicManager(model,
                new StorageManager(storage, new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        logic.execute("paid 1");
        assertEquals(List.of(new PersonBuilder(original).withoutOutstandingFee().build()),
                storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_queriesAndRejectedPayment_doNotWriteData() throws Exception {
        Path data = directory.resolve("students.json");
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withGuardianName("Mei Tan").build());
        Logic logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        logic.execute("feesummary");
        assertThrows(CommandException.class, () -> logic.execute("paid 1"));
        assertFalse(Files.exists(data));
    }

    @Test
    public void execute_paymentSaveFailure_reportsErrorWithoutClaimingPersistence() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withOutstandingFee("10").build());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("students.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw new IOException("Deliberate verification failure");
            }
        };
        Logic logic = new LogicManager(model,
                new StorageManager(storage, new JsonUserPrefsStorage(directory.resolve("prefs.json"))));
        CommandException failure = assertThrows(CommandException.class, () -> logic.execute("paid 1"));
        assertEquals(String.format(LogicManager.FILE_OPS_ERROR_FORMAT, "Deliberate verification failure"),
                failure.getMessage());
        assertFalse(model.getAddressBook().getPersonList().get(0).getOutstandingFee().isPresent());
        assertFalse(Files.exists(directory.resolve("students.json")));
    }
}
