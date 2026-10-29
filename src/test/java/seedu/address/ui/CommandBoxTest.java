package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import seedu.address.logic.commands.CommandResult;

/**
 * Verifies which command box entries are passed on for execution.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class CommandBoxTest {

    @BeforeAll
    public static void initializeToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
    }

    @Test
    public void handleCommandEntered_blankInput_notExecuted() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            List<String> executedCommands = new ArrayList<>();
            CommandBox commandBox = new CommandBox(commandText -> {
                executedCommands.add(commandText);
                return new CommandResult("ok");
            });
            TextField commandTextField = (TextField) commandBox.getRoot().lookup("#commandTextField");

            for (String blankInput : new String[] {"", "   ", " \t "}) {
                commandTextField.setText(blankInput);
                commandTextField.fireEvent(new ActionEvent());
                assertFalse(commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            }
            assertEquals(List.of(), executedCommands);

            commandTextField.setText("list");
            commandTextField.fireEvent(new ActionEvent());
            assertEquals(List.of("list"), executedCommands);
            assertEquals("", commandTextField.getText());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }
}
