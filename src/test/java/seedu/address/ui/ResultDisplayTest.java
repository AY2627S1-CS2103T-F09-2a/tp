package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies that the result display grows to show multi-line feedback without scrolling.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class ResultDisplayTest {

    @BeforeAll
    public static void initializeToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
    }

    @Test
    public void setFeedbackToUser_rowCountFollowsLineCountWithinBounds() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            TextArea textArea = (TextArea) resultDisplay.getRoot().lookup("#resultDisplay");

            // a full add confirmation is shown in full
            String addFeedback = String.format(AddCommand.MESSAGE_SUCCESS,
                    Messages.format(new PersonBuilder().withTags("math").build()));
            resultDisplay.setFeedbackToUser(addFeedback);
            assertEquals(addFeedback.split("\n").length, textArea.getPrefRowCount());

            // short feedback keeps the minimum height
            resultDisplay.setFeedbackToUser("Listed all students");
            assertEquals(ResultDisplay.MIN_ROWS, textArea.getPrefRowCount());

            // very long feedback, such as help text, is capped and scrolls
            resultDisplay.setFeedbackToUser("line\n".repeat(50));
            assertEquals(ResultDisplay.MAX_ROWS, textArea.getPrefRowCount());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }
}
