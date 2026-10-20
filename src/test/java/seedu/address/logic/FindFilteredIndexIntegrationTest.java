package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Integration tests that run raw user input through {@link LogicManager} to check that,
 * after a {@code find} or {@code fees}, index-based commands refer to the filtered list rather than the full list.
 */
public class FindFilteredIndexIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));
    }

    @Test
    public void find_thenDelete_deletesPersonAtFilteredIndex() throws Exception {
        CommandResult findResult = logic.execute("find meier");
        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), findResult.getFeedbackToUser());
        assertEquals(List.of(BENSON, DANIEL), logic.getFilteredPersonList());

        // Index 2 is BENSON in the full list, but DANIEL in the filtered list
        CommandResult deleteResult = logic.execute("delete 2");

        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(DANIEL)),
                deleteResult.getFeedbackToUser());
        assertEquals(List.of(BENSON), logic.getFilteredPersonList());
        assertFalse(model.getAddressBook().getPersonList().contains(DANIEL));
        assertTrue(model.getAddressBook().getPersonList().contains(BENSON));
    }

    @Test
    public void fees_thenDelete_refusesPersonAtFilteredIndex() throws Exception {
        // ALICE and BENSON already owe fees; GEORGE (7th in the full list) is given one
        logic.execute("setoutstanding 7 50.00");
        logic.execute("fees");
        assertEquals(3, logic.getFilteredPersonList().size());
        Person george = logic.getFilteredPersonList().get(2);

        // Index 3 is CARL in the full list, but GEORGE in the fees list. A student who owes a fee
        // cannot be deleted, so the error names GEORGE and no student is deleted.
        assertThrows(CommandException.class, String.format(DeleteCommand.MESSAGE_STUDENT_OWES_FEE,
                george.getName(), "50.00"), () -> logic.execute("delete 3"));

        assertTrue(model.getAddressBook().getPersonList().contains(george));
        assertTrue(model.getAddressBook().getPersonList().contains(CARL));
    }

    @Test
    public void find_thenDeleteIndexOutsideFilteredList_throwsCommandException() throws Exception {
        logic.execute("find meier");
        int fullListSize = model.getAddressBook().getPersonList().size();

        // Index 3 exists in the full list (7 persons) but not in the filtered list (2 persons).
        // Only the exception type is checked; the exact error text is covered by DeleteCommandTest.
        assertThrows(CommandException.class, () -> logic.execute("delete 3"));
        assertEquals(fullListSize, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void findNoMatches_thenDeleteFirstIndex_throwsCommandException() throws Exception {
        CommandResult findResult = logic.execute("find zelda");
        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), findResult.getFeedbackToUser());

        int fullListSize = model.getAddressBook().getPersonList().size();

        assertThrows(CommandException.class, () -> logic.execute("delete 1"));
        assertEquals(fullListSize, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void find_thenInvalidFind_keepsDisplayedList() throws Exception {
        logic.execute("find meier");

        assertThrows(ParseException.class, () -> logic.execute("find @@@"));
        assertEquals(List.of(BENSON, DANIEL), logic.getFilteredPersonList());
    }

}
