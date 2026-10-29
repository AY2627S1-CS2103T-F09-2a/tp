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
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
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
 * <p>The window is driven through its own command box. Most tests use a confirmer that records each
 * request instead of showing a dialog. Two tests use the public constructor and answer the real dialog
 * with Y or N.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class MainWindowDeletionTest {

    /** Tries to find the deletion dialog for about five seconds, 20 milliseconds apart. */
    private static final int MAX_DIALOG_ATTEMPTS = 250;

    @TempDir
    public Path temporaryFolder;

    private final List<Person> confirmationRequests = new ArrayList<>();
    private final List<Stage> openedStages = new ArrayList<>();
    /** Whether a dialog answer from this test may still be pressed. Read and written on the JavaFX thread. */
    private boolean isAnswerPending;
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

    /**
     * Closes the windows a test opened, so that they do not accumulate in the shared JavaFX runtime.
     */
    @AfterEach
    public void closeWindows() throws Exception {
        runOnFxThread(() -> {
            isAnswerPending = false;
            openedStages.forEach(Stage::close);
            openedStages.clear();
            return null;
        });
    }

    @Test
    public void executeCommand_deleteConfirmedInTheDialog_asksThenDeletesAndShowsFeedback() throws Exception {
        Stage stage = openWindowWithDialog();
        answerDialogWith(KeyCode.Y);

        String feedback = runCommand(stage, "delete 1");

        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(ALICE)), feedback);
        assertFalse(logic.getFilteredPersonList().contains(ALICE));
    }

    @Test
    public void executeCommand_deleteKeptInTheDialog_keepsStudentAndShowsCancellation() throws Exception {
        Stage stage = openWindowWithDialog();
        answerDialogWith(KeyCode.N);

        String feedback = runCommand(stage, "delete 1");

        assertEquals(DeleteConfirmationHandler.MESSAGE_DELETE_CANCELLED, feedback);
        assertTrue(logic.getFilteredPersonList().contains(ALICE));
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
            openedStages.add(stage);
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
     * Creates a main window that asks through the real dialog, as the application does.
     */
    private Stage openWindowWithDialog() throws Exception {
        return runOnFxThread(() -> {
            Stage stage = new Stage();
            openedStages.add(stage);
            MainWindow window = new MainWindow(stage, logic, addressBookFile);
            window.fillInnerParts();
            return stage;
        });
    }

    /**
     * Presses {@code key} in the deletion dialog as soon as it is showing. Must be called on the
     * test thread before the command that opens the dialog is sent, because the dialog blocks the
     * JavaFX thread until it is answered.
     */
    private void answerDialogWith(KeyCode key) {
        Platform.runLater(new Runnable() {
            private int attempts;

            @Override
            public void run() {
                isAnswerPending = true;
                answerOrRetry();
            }

            /**
             * Answers the dialog if it is showing, or tries again shortly. Stops once the test has ended or
             * after {@code MAX_DIALOG_ATTEMPTS} tries, so a dialog that never appears cannot be answered by a
             * later test.
             */
            private void answerOrRetry() {
                if (!isAnswerPending) {
                    return;
                }
                Optional<Window> dialog = showingDialog();
                if (dialog.isEmpty()) {
                    if (++attempts >= MAX_DIALOG_ATTEMPTS) {
                        isAnswerPending = false;
                        return;
                    }
                    // The dialog is attached shortly after the nested event loop starts
                    PauseTransition retry = new PauseTransition(Duration.millis(20));
                    retry.setOnFinished(event -> answerOrRetry());
                    retry.play();
                    return;
                }
                isAnswerPending = false;
                Scene scene = dialog.get().getScene();
                EventTarget target = scene.getFocusOwner() != null ? scene.getFocusOwner() : scene;
                Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key,
                        false, false, false, false));
                Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_RELEASED, "", "", key,
                        false, false, false, false));
            }
        });
    }

    /**
     * Returns the showing window that holds a dialog pane, which is the deletion dialog.
     */
    private static Optional<Window> showingDialog() {
        return Window.getWindows().stream()
                .filter(Window::isShowing)
                .filter(window -> window.getScene() != null)
                .filter(window -> window.getScene().getRoot() instanceof DialogPane)
                .findFirst();
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
