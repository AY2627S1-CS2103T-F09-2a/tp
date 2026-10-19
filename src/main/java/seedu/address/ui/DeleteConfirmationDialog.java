package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import seedu.address.logic.Messages;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;

/**
 * A dialog that asks the user to confirm the deletion of a student.
 * On the keyboard, only Y deletes the student; N, Escape or closing the dialog keeps them.
 * Other keys, including Enter and Space, are ignored, so a habitual Enter or the start of the next command
 * cannot answer the dialog. A student with an outstanding fee is shown with a prominent warning.
 */
public class DeleteConfirmationDialog {

    /**
     * Delete uses {@code APPLY} rather than {@code OK_DONE}: the button bar gives initial focus to a button whose
     * data is a default action, and Delete must not start focused. {@code APPLY} also keeps it beside Cancel.
     */
    static final ButtonType DELETE = new ButtonType("Delete (Y)", ButtonData.APPLY);
    static final ButtonType CANCEL = new ButtonType("Cancel (N)", ButtonData.CANCEL_CLOSE);

    static final String DETAILS_STYLE_CLASS = "delete-confirmation-details";
    static final String PERMANENCE_NOTE_STYLE_CLASS = "delete-confirmation-permanence-note";
    static final String FEE_WARNING_STYLE_CLASS = "delete-confirmation-fee-warning";
    static final String KEY_INSTRUCTIONS_STYLE_CLASS = "delete-confirmation-key-instructions";

    private static final KeyCode DELETE_KEY = KeyCode.Y;
    private static final KeyCode CANCEL_KEY = KeyCode.N;
    private static final String KEY_INSTRUCTIONS = "Press Y to delete this student, or N to cancel.";
    private static final String TITLE = "Confirm deletion";
    private static final String HEADER = "Delete this student?";
    private static final String DETAILS_FORMAT = "Name: %1$s\nPhone: %2$s\nGuardian: %3$s\nGuardian phone: %4$s";
    private static final String PERMANENCE_NOTE = "Deleted students cannot be restored.";
    private static final String FEE_WARNING_FORMAT =
            "This student still owes S$%1$s. Deleting them also deletes this unpaid fee.";
    private static final String STYLESHEET = "view/DeleteConfirmationDialog.css";

    /**
     * A fixed width stops long names from widening the dialog; they wrap instead. The dialog then stays well within
     * a 1280x720 screen at 150% scaling, which leaves about 853x480 pixels for windows.
     */
    private static final double DIALOG_WIDTH = 520;
    private static final double CONTENT_SPACING = 8;

    private final Alert alert;

    /**
     * Creates a dialog, owned by {@code owner}, that asks whether to delete {@code student}.
     */
    public DeleteConfirmationDialog(Window owner, Person student) {
        requireAllNonNull(owner, student);
        AlertType type = student.getOutstandingFee().isPresent() ? AlertType.WARNING : AlertType.CONFIRMATION;
        alert = new Alert(type, "", DELETE, CANCEL);
        alert.initOwner(owner);
        alert.setTitle(TITLE);
        alert.setHeaderText(HEADER);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().addAll("view/DarkTheme.css", STYLESHEET);
        dialogPane.setContent(createContent(student));
        dialogPane.setPrefWidth(DIALOG_WIDTH);
        // Without this, some platforms cut off wrapped text instead of growing the dialog.
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);

        for (ButtonType buttonType : List.of(DELETE, CANCEL)) {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            assert button != null : "Dialog must contain a button for " + buttonType.getText();
            // Buttons are for the mouse only, so no key press can reach Delete through focus or the default button.
            button.setDefaultButton(false);
            button.setFocusTraversable(false);
        }
        assert dialogPane.getScene() != null : "An alert places its dialog pane in a scene when it is created";
        dialogPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    }

    /**
     * Shows the dialog, waits for the user to respond, and returns true only if the user chooses to delete.
     */
    public boolean showAndConfirm() {
        return alert.showAndWait().filter(DELETE::equals).isPresent();
    }

    /**
     * Returns the dialog pane. This is package-private so that tests in this package can inspect the dialog
     * and send key presses to it, without making the pane part of the public API.
     */
    DialogPane getDialogPane() {
        return alert.getDialogPane();
    }

    /**
     * Returns the student's details, a note that deletion is permanent, an outstanding-fee warning if they owe a fee,
     * and the keys that answer the dialog.
     */
    private static Node createContent(Person student) {
        VBox content = new VBox(CONTENT_SPACING);
        content.getChildren().add(createLabel(formatDetails(student), DETAILS_STYLE_CLASS));
        content.getChildren().add(createLabel(PERMANENCE_NOTE, PERMANENCE_NOTE_STYLE_CLASS));
        student.getOutstandingFee().ifPresent(fee ->
                content.getChildren().add(createLabel(formatFeeWarning(fee), FEE_WARNING_STYLE_CLASS)));
        content.getChildren().add(createLabel(KEY_INSTRUCTIONS, KEY_INSTRUCTIONS_STYLE_CLASS));
        return content;
    }

    private static Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMinHeight(Region.USE_PREF_SIZE);
        return label;
    }

    private static String formatDetails(Person student) {
        String phone = student.getPhone().map(Object::toString).orElse(Messages.NOT_PROVIDED);
        return String.format(DETAILS_FORMAT, student.getName(), phone, student.getGuardianName(),
                student.getGuardianPhone());
    }

    private static String formatFeeWarning(OutstandingFee fee) {
        return String.format(FEE_WARNING_FORMAT, fee);
    }

    /**
     * Answers the dialog when the user presses Y or N. Escape is left to the Cancel button.
     * Enter and Space are blocked, because they would otherwise activate a focused or default button.
     */
    private void handleKeyPressed(KeyEvent event) {
        KeyCode key = event.getCode();
        if (key == KeyCode.ENTER || key == KeyCode.SPACE) {
            event.consume();
            return;
        }
        if (event.isShortcutDown() || event.isAltDown()) {
            return;
        }

        if (key == DELETE_KEY) {
            answer(DELETE, event);
        } else if (key == CANCEL_KEY) {
            answer(CANCEL, event);
        }
    }

    /**
     * Closes the dialog with {@code buttonType} as the answer, just as clicking that button would.
     */
    private void answer(ButtonType buttonType, KeyEvent event) {
        Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
        button.fire();
        event.consume();
    }
}
