package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies the content, keyboard behavior and size of {@code DeleteConfirmationDialog}.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class DeleteConfirmationDialogTest {

    /** Usable area of a 1280x720 screen at 150% scaling, above a 48-pixel Windows 11 taskbar. */
    private static final double SMALL_SCREEN_WIDTH = 1280 / 1.5;
    private static final double SMALL_SCREEN_HEIGHT = 720 / 1.5 - 48;

    /** Owns every dialog in these tests, as the main window owns every dialog in the app. */
    private static Stage owner;

    @BeforeAll
    public static void initializeToolkit() throws Exception {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
        // Closing a test's dialog would otherwise shut JavaFX down, because no other window is showing
        Platform.setImplicitExit(false);
        owner = runOnFxThread(() -> {
            Stage stage = new Stage();
            // A dialog copies its owner's stylesheets, so the owner needs a scene, as the main window has
            stage.setScene(new Scene(new Pane()));
            return stage;
        });
    }

    @Test
    public void constructor_showsDetailsAndKeyInstructions() throws Exception {
        runOnFxThread(() -> {
            DialogPane dialogPane = newDialog(CARL).getDialogPane();

            assertEquals("Name: Carl Kurz\nPhone: 95352563\nGuardian: Guardian\nGuardian phone: 91234567",
                    labelText(dialogPane, DeleteConfirmationDialog.DETAILS_STYLE_CLASS));
            assertEquals("Deleted students cannot be restored.", labelText(dialogPane,
                    DeleteConfirmationDialog.PERMANENCE_NOTE_STYLE_CLASS));
            assertEquals("Press Y to delete this student, or N to cancel.", labelText(dialogPane,
                    DeleteConfirmationDialog.KEY_INSTRUCTIONS_STYLE_CLASS));
            // An alert's style class matches its type, which decides its icon
            assertTrue(dialogPane.getStyleClass().contains("confirmation"));
            return null;
        });
    }

    @Test
    public void constructor_noStudentPhone_showsNotProvided() throws Exception {
        Person withoutPhone = new PersonBuilder().withoutContactDetails().build();

        String details = runOnFxThread(() -> labelText(newDialog(withoutPhone).getDialogPane(),
                DeleteConfirmationDialog.DETAILS_STYLE_CLASS));

        assertTrue(details.contains("\nPhone: Not provided\n"), details);
    }

    @Test
    public void constructor_studentOwesFee_throwsIllegalArgumentException() throws Exception {
        Person studentOwingFee = new PersonBuilder(CARL).withOutstandingFee("450.00").build();

        runOnFxThread(() -> {
            assertThrows(IllegalArgumentException.class, () -> newDialog(studentOwingFee));
            return null;
        });
    }

    @Test
    public void constructor_buttonsCannotBeReachedByKeyboardFocus() throws Exception {
        runOnFxThread(() -> {
            DialogPane dialogPane = newDialog(CARL).getDialogPane();
            Button deleteButton = (Button) dialogPane.lookupButton(DeleteConfirmationDialog.DELETE);
            Button cancelButton = (Button) dialogPane.lookupButton(DeleteConfirmationDialog.CANCEL);

            assertFalse(deleteButton.isDefaultButton());
            assertFalse(deleteButton.isFocusTraversable());
            assertFalse(cancelButton.isDefaultButton());
            assertFalse(cancelButton.isFocusTraversable());
            assertTrue(cancelButton.isCancelButton());
            return null;
        });
    }

    @Test
    public void showAndConfirm_yPressed_confirms() throws Exception {
        assertTrue(respond(CARL, dialogPane -> press(dialogPane, KeyCode.Y)));
    }

    @Test
    public void showAndConfirm_nPressed_keepsStudent() throws Exception {
        assertFalse(respond(CARL, dialogPane -> press(dialogPane, KeyCode.N)));
    }

    @Test
    public void showAndConfirm_escapePressed_keepsStudent() throws Exception {
        assertFalse(respond(CARL, dialogPane -> press(dialogPane, KeyCode.ESCAPE)));
    }

    @Test
    public void showAndConfirm_windowClosed_keepsStudent() throws Exception {
        assertFalse(respond(CARL, dialogPane -> {
            Window window = dialogPane.getScene().getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        }));
    }

    @Test
    public void showAndConfirm_deleteClicked_confirms() throws Exception {
        assertTrue(respond(CARL, dialogPane -> {
            Button deleteButton = (Button) dialogPane.lookupButton(DeleteConfirmationDialog.DELETE);
            deleteButton.fire();
        }));
    }

    @Test
    public void showAndConfirm_cancelClicked_keepsStudent() throws Exception {
        assertFalse(respond(CARL, dialogPane -> {
            Button cancelButton = (Button) dialogPane.lookupButton(DeleteConfirmationDialog.CANCEL);
            cancelButton.fire();
        }));
    }

    @Test
    public void showAndConfirm_enterTabAndCommandKeysPressed_staysOpen() throws Exception {
        AtomicBoolean isStillShowing = new AtomicBoolean();
        AtomicReference<Node> focusOwner = new AtomicReference<>();

        boolean isConfirmed = respond(CARL, dialogPane -> {
            focusOwner.set(dialogPane.getScene().getFocusOwner());
            // A habitual Enter, Tab to a button, Space, and the start of a typed "delete 2" command
            for (KeyCode key : List.of(KeyCode.ENTER, KeyCode.TAB, KeyCode.ENTER, KeyCode.SPACE,
                    KeyCode.D, KeyCode.E, KeyCode.L, KeyCode.DIGIT2)) {
                press(dialogPane, key);
            }
            isStillShowing.set(dialogPane.getScene().getWindow().isShowing());
            press(dialogPane, KeyCode.N);
        });

        assertTrue(isStillShowing.get());
        assertFalse(isConfirmed);
        assertFalse(focusOwner.get() instanceof Button);
    }

    @Test
    public void showAndConfirm_yPressedWithShortcut_staysOpen() throws Exception {
        AtomicBoolean isStillShowing = new AtomicBoolean();

        boolean isConfirmed = respond(CARL, dialogPane -> {
            fire(dialogPane, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.Y, false, true, false, true));
            isStillShowing.set(dialogPane.getScene().getWindow().isShowing());
            press(dialogPane, KeyCode.N);
        });

        assertTrue(isStillShowing.get());
        assertFalse(isConfirmed);
    }

    @Test
    public void showAndConfirm_longestNames_fitsSmallScreenWithoutCutOffText() throws Exception {
        // Valid names are at most 70 characters; "W" is among the widest letters
        Person longNames = new PersonBuilder().withName("W".repeat(70)).withGuardianName("W".repeat(70)).build();
        AtomicReference<Dimension2D> size = new AtomicReference<>();
        List<String> cutOffTexts = new ArrayList<>();

        respond(longNames, dialogPane -> {
            Window window = dialogPane.getScene().getWindow();
            size.set(new Dimension2D(window.getWidth(), window.getHeight()));
            for (Node node : dialogPane.getContent().lookupAll(".label")) {
                Label label = (Label) node;
                Text displayedText = (Text) label.lookup(".text");
                if (!label.getText().equals(displayedText.getText())) {
                    cutOffTexts.add(displayedText.getText());
                }
            }
            press(dialogPane, KeyCode.N);
        });

        assertTrue(size.get().getWidth() <= SMALL_SCREEN_WIDTH, "Dialog is too wide: " + size.get());
        assertTrue(size.get().getHeight() <= SMALL_SCREEN_HEIGHT, "Dialog is too tall: " + size.get());
        assertEquals(List.of(), cutOffTexts);
    }

    /**
     * Returns a dialog for {@code student} owned by the shared owner window. Must be called on the JavaFX thread.
     */
    private static DeleteConfirmationDialog newDialog(Person student) {
        return new DeleteConfirmationDialog(owner, student);
    }

    private static String labelText(DialogPane dialogPane, String styleClass) {
        return ((Label) dialogPane.lookup("." + styleClass)).getText();
    }

    /**
     * Shows a dialog for {@code student}, performs {@code userAction} once it is showing,
     * and returns whether the user confirmed the deletion.
     * Fails if the dialog is still open after {@code userAction}, instead of waiting for an answer that never comes.
     */
    private static boolean respond(Person student, Consumer<DialogPane> userAction) throws Exception {
        AtomicBoolean isLeftOpen = new AtomicBoolean();
        boolean isConfirmed = runOnFxThread(() -> {
            DeleteConfirmationDialog dialog = newDialog(student);
            DialogPane dialogPane = dialog.getDialogPane();
            Platform.runLater(() -> {
                try {
                    userAction.accept(dialogPane);
                } finally {
                    Window window = dialogPane.getScene().getWindow();
                    isLeftOpen.set(window.isShowing());
                    // Hiding the dialog ends showAndConfirm, so the test fails below instead of timing out
                    window.hide();
                }
            });
            return dialog.showAndConfirm();
        });
        assertFalse(isLeftOpen.get(), "The dialog was still open after the user's action");
        return isConfirmed;
    }

    /**
     * Presses and releases {@code key} without modifiers.
     */
    private static void press(DialogPane dialogPane, KeyCode key) {
        fire(dialogPane, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, false, false, false, false));
        fire(dialogPane, new KeyEvent(KeyEvent.KEY_RELEASED, "", "", key, false, false, false, false));
    }

    /**
     * Sends {@code keyEvent} to the focused node of the dialog, or to its scene if nothing has focus.
     */
    private static void fire(DialogPane dialogPane, KeyEvent keyEvent) {
        Scene scene = dialogPane.getScene();
        EventTarget target = scene.getFocusOwner() != null ? scene.getFocusOwner() : scene;
        Event.fireEvent(target, keyEvent);
    }

    private static <T> T runOnFxThread(Callable<T> callable) throws Exception {
        FutureTask<T> task = new FutureTask<>(callable);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
