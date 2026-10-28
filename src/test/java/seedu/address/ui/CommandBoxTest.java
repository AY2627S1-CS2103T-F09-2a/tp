package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Checks what the command box sends to the command executor when Enter is pressed.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class CommandBoxTest {

    @BeforeAll
    public static void startToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test has already started the toolkit.
        }
        Platform.setImplicitExit(false);
    }

    @Test
    public void handleCommandEntered_blankInput_clearsWithoutExecuting() throws Exception {
        for (String blank : new String[] {" ", "   ", "\t "}) {
            List<String> executed = new ArrayList<>();
            TextField field = enter(blank, executed, false);

            assertEquals(List.of(), executed);
            assertEquals("", field.getText());
            assertFalse(field.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        }
    }

    @Test
    public void handleCommandEntered_emptyInput_doesNotExecute() throws Exception {
        List<String> executed = new ArrayList<>();
        enter("", executed, false);

        assertEquals(List.of(), executed);
    }

    @Test
    public void handleCommandEntered_validCommand_executesAndClears() throws Exception {
        List<String> executed = new ArrayList<>();
        TextField field = enter(" list ", executed, false);

        assertEquals(List.of(" list "), executed);
        assertEquals("", field.getText());
    }

    @Test
    public void handleCommandEntered_failingCommand_keepsTextAndShowsError() throws Exception {
        List<String> executed = new ArrayList<>();
        TextField field = enter("lsit", executed, true);

        assertEquals(List.of("lsit"), executed);
        assertEquals("lsit", field.getText());
        assertTrue(field.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
    }

    /**
     * Types {@code text} into a new command box and presses Enter. Each executed command is added to
     * {@code executed}; the executor fails if {@code shouldFail} is true.
     */
    private static TextField enter(String text, List<String> executed, boolean shouldFail) throws Exception {
        FutureTask<TextField> task = new FutureTask<>(() -> {
            CommandBox commandBox = new CommandBox(commandText -> {
                executed.add(commandText);
                if (shouldFail) {
                    throw new ParseException("failed");
                }
                return new CommandResult("done");
            });
            TextField field = (TextField) commandBox.getRoot().lookup("#commandTextField");
            field.setText(text);
            field.fireEvent(new ActionEvent());
            return field;
        });
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
