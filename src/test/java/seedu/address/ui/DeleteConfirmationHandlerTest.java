package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests that {@code DeleteConfirmationHandler} deletes a student only after the user confirms,
 * using a confirmer that records each request instead of showing a dialog.
 * Input without a deletion target never reaches the handler; see {@code LogicManagerTest} for those cases.
 */
public class DeleteConfirmationHandlerTest {

    @TempDir
    public Path temporaryFolder;

    private final List<Person> confirmationRequests = new ArrayList<>();
    private Model model;
    private Logic logic;
    private Path addressBookFile;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        addressBookFile = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFile);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));
    }

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteConfirmationHandler(null, student -> true));
        assertThrows(NullPointerException.class, () -> new DeleteConfirmationHandler(logic, null));
    }

    @Test
    public void confirmAndDelete_nullArgument_throwsNullPointerException() {
        DeleteConfirmationHandler handler = handlerAnswering(true);

        assertThrows(NullPointerException.class, () -> handler.confirmAndDelete(null, ALICE));
        assertThrows(NullPointerException.class, () -> handler.confirmAndDelete("delete 1", null));
        assertTrue(confirmationRequests.isEmpty());
    }

    @Test
    public void confirmAndDelete_confirmed_deletesStudentAndSaves() throws Exception {
        // The third student, CARL, owes no fee
        CommandResult result = confirmAndDelete(handlerAnswering(true), "delete 3");

        assertEquals(List.of(CARL), confirmationRequests);
        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(CARL)),
                result.getFeedbackToUser());
        assertFalse(model.getAddressBook().getPersonList().contains(CARL));
        assertEquals(DANIEL, logic.getFilteredPersonList().get(2));

        ReadOnlyAddressBook savedAddressBook = new JsonAddressBookStorage(addressBookFile).readAddressBook()
                .orElseThrow();
        assertEquals(model.getAddressBook().getPersonList(), savedAddressBook.getPersonList());
    }

    @Test
    public void confirmAndDelete_cancelled_keepsStudentsAndDisplayedList() throws Exception {
        logic.execute("find meier");
        List<Person> displayedBefore = List.copyOf(logic.getFilteredPersonList());

        CommandResult result = confirmAndDelete(handlerAnswering(false), "delete 2");

        assertEquals(List.of(DANIEL), confirmationRequests);
        assertEquals(DeleteConfirmationHandler.MESSAGE_DELETE_CANCELLED, result.getFeedbackToUser());
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
        assertEquals(displayedBefore, logic.getFilteredPersonList());
        assertFalse(Files.exists(addressBookFile));
    }

    @Test
    public void confirmAndDelete_confirmedFromFindResults_deletesStudentAtFilteredIndex() throws Exception {
        logic.execute("find meier");
        assertEquals(List.of(BENSON, DANIEL), logic.getFilteredPersonList());

        // Index 2 is BENSON in the full list, but DANIEL in the search results
        confirmAndDelete(handlerAnswering(true), "delete 2");

        assertEquals(List.of(DANIEL), confirmationRequests);
        assertEquals(List.of(BENSON), logic.getFilteredPersonList());
        assertFalse(model.getAddressBook().getPersonList().contains(DANIEL));
    }

    @Test
    public void confirmAndDelete_feeClearedWithSetOutstanding_deletesStudent() throws Exception {
        assertDeletableOnlyAfterFeeCleared("setoutstanding 1 0");
    }

    @Test
    public void confirmAndDelete_feeRecordedAsPaid_deletesStudent() throws Exception {
        assertDeletableOnlyAfterFeeCleared("paid 1");
    }

    @Test
    public void confirmAndDelete_displayedStudentChangesBeforeConfirmation_throwsWithoutDeleting() {
        // The confirmer hides CARL, so DANIEL moves up to the index that the user confirmed for CARL
        DeleteConfirmationHandler handler = new DeleteConfirmationHandler(logic, student -> {
            model.updateFilteredPersonList(person -> !person.equals(student));
            return true;
        });

        assertThrows(CommandException.class, DeleteConfirmationHandler.MESSAGE_TARGET_CHANGED, () ->
                confirmAndDelete(handler, "delete 3"));
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    @Test
    public void confirmAndDelete_displayedListEmptiedBeforeConfirmation_throwsWithoutDeleting() {
        DeleteConfirmationHandler handler = new DeleteConfirmationHandler(logic, student -> {
            model.updateFilteredPersonList(person -> false);
            return true;
        });

        assertThrows(CommandException.class, DeleteConfirmationHandler.MESSAGE_TARGET_CHANGED, () ->
                confirmAndDelete(handler, "delete 3"));
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    /**
     * Asserts that ALICE, who owes a fee, cannot be deleted until {@code clearFeeCommand} clears her fee,
     * and that she can then be confirmed and deleted.
     */
    private void assertDeletableOnlyAfterFeeCleared(String clearFeeCommand) throws Exception {
        assertEquals(Optional.empty(), logic.getDeletionTarget("delete 1"));
        assertThrows(CommandException.class, String.format(DeleteCommand.MESSAGE_STUDENT_OWES_FEE, ALICE.getName(),
                "0.01"), () -> logic.execute("delete 1"));

        logic.execute(clearFeeCommand);
        Person aliceWithoutFee = new PersonBuilder(ALICE).withoutOutstandingFee().build();
        CommandResult result = confirmAndDelete(handlerAnswering(true), "delete 1");

        assertEquals(List.of(aliceWithoutFee), confirmationRequests);
        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(aliceWithoutFee)),
                result.getFeedbackToUser());
        assertFalse(model.getAddressBook().getPersonList().contains(aliceWithoutFee));
    }

    /**
     * Confirms and deletes the student that {@code commandText} targets, as {@code MainWindow} does.
     */
    private CommandResult confirmAndDelete(DeleteConfirmationHandler handler, String commandText) throws Exception {
        Person student = logic.getDeletionTarget(commandText).orElseThrow();
        return handler.confirmAndDelete(commandText, student);
    }

    /**
     * Returns a handler whose confirmer records each student it is asked about and answers {@code isConfirmed}.
     */
    private DeleteConfirmationHandler handlerAnswering(boolean isConfirmed) {
        return new DeleteConfirmationHandler(logic, student -> {
            confirmationRequests.add(student);
            return isConfirmed;
        });
    }
}
