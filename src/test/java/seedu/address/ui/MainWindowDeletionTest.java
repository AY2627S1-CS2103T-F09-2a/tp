package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests the deletion wiring in {@code MainWindow}: a {@code delete} of a displayed student reaches
 * {@code DeleteConfirmationHandler} before it runs, other commands and invalid indexes do not,
 * and the feedback and the displayed list follow the answer.
 *
 * <p>The window is driven through its own command box, with a confirmer that records each request
 * instead of showing a dialog, so no dialog has to be answered or dismissed.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class MainWindowDeletionTest {

    @TempDir
    public Path temporaryFolder;

    private final List<Person> confirmationRequests = new ArrayList<>();
    private Model model;
    private Logic logic;
    private Path addressBookFile;

    @BeforeAll
    public static void initializeToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
        // Hiding the window would otherwise shut JavaFX down, because no window stays showing
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        addressBookFile = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFile);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));
    }

    @Test
    public void executeCommand_confirmedDelete_asksThenDeletesAndShowsFeedback() throws Exception {
        Stage stage = openWindow(true);

        String feedback = runCommand(stage, "delete 1");

        assertEquals(List.of(ALICE), confirmationRequests);
        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(ALICE)), feedback);
        assertFalse(logic.getFilteredPersonList().contains(ALICE));
        assertEquals(BENSON, logic.getFilteredPersonList().get(0));
    }

    @Test
    public void executeCommand_cancelledDelete_keepsStudentAndShowsCancellation() throws Exception {
        Stage stage = openWindow(false);

        String feedback = runCommand(stage, "delete 1");

        assertEquals(List.of(ALICE), confirmationRequests);
        assertEquals(DeleteConfirmationHandler.MESSAGE_DELETE_CANCELLED, feedback);
        assertTrue(logic.getFilteredPersonList().contains(ALICE));
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    @Test
    public void executeCommand_otherCommand_doesNotAskForConfirmation() throws Exception {
        Stage stage = openWindow(true);

        String feedback = runCommand(stage, "list");

        assertTrue(confirmationRequests.isEmpty());
        assertEquals(ListCommand.MESSAGE_SUCCESS, feedback);
    }

    @Test
    public void executeCommand_deleteOfIndexOutsideDisplayedList_showsErrorWithoutAsking() throws Exception {
        Stage stage = openWindow(true);

        String feedback = runCommand(stage, "delete 99");

        assertTrue(confirmationRequests.isEmpty());
        assertEquals(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, feedback);
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    /**
     * Creates a main window whose deletions are answered with {@code answer}.
     */
    private Stage openWindow(boolean answer) throws Exception {
        return runOnFxThread(() -> {
            Stage stage = new Stage();
            DeleteConfirmationHandler handler = new DeleteConfirmationHandler(logic, student -> {
                confirmationRequests.add(student);
                return answer;
            });
            MainWindow window = new MainWindow(stage, logic, addressBookFile, handler);
            window.fillInnerParts();
            return stage;
        });
    }

    /**
     * Types {@code commandText} into the window's command box, presses Enter,
     * and returns the feedback the window shows.
     */
    private String runCommand(Stage stage, String commandText) throws Exception {
        return runOnFxThread(() -> {
            TextField commandTextField = (TextField) stage.getScene().lookup("#commandTextField");
            commandTextField.setText(commandText);
            commandTextField.fireEvent(new ActionEvent());
            TextArea resultDisplay = (TextArea) stage.getScene().lookup("#resultDisplay");
            return resultDisplay.getText();
        });
    }

    private static <T> T runOnFxThread(Callable<T> callable) throws Exception {
        FutureTask<T> task = new FutureTask<>(callable);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
