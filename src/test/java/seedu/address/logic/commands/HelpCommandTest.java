package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.HelpCommand.SHOWING_HELP_MESSAGE;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void shouldSaveAddressBook_helpCommand_returnsFalse() {
        assertFalse(new HelpCommand().shouldSaveAddressBook());
    }

    @Test
    public void execute_targetedHelp_showsUsageWithoutOpeningWindow() {
        HelpCommand command = new HelpCommand("paid");
        CommandResult result = command.execute(model);
        assertTrue(result.getFeedbackToUser().startsWith(PaidCommand.MESSAGE_USAGE));
        assertTrue(result.getFeedbackToUser().contains("whole outstanding fee"));
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
        assertTrue(result.getViewedPerson().isEmpty());
        assertFalse(command.shouldSaveAddressBook());
        assertEquals(expectedModel, model);
    }

    @Test
    public void constructor_invalidTarget_rejects() {
        assertThrows(NullPointerException.class, () -> new HelpCommand(null));
        assertThrows(IllegalArgumentException.class, () -> new HelpCommand("unknown"));
    }

    @Test
    public void equals_comparesHelpTarget() {
        HelpCommand command = new HelpCommand("paid");
        assertEquals(command, command);
        assertEquals(command, new HelpCommand("paid"));
        assertEquals(new HelpCommand(), new HelpCommand());
        assertNotEquals(command, new HelpCommand("fees"));
        assertNotEquals(command, new HelpCommand());
        assertNotEquals(command, null);
        assertNotEquals(command, "paid");
    }

    @Test
    public void hashCode_equalCommands_deduplicateInSet() {
        assertEquals(2, new HashSet<>(List.of(new HelpCommand("paid"), new HelpCommand("paid"),
                new HelpCommand(), new HelpCommand())).size());
    }
}
