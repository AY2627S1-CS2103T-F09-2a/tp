package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.person.OutstandingFee;

/**
 * Opens the help window or displays usage instructions for one command in the result area.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Opens the help window, or shows help for one command in the result area.\n"
            + "Parameters: [COMMAND]\nExample: " + COMMAND_WORD + " paid";

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    private static final Map<String, String> COMMAND_USAGE = Map.ofEntries(
            Map.entry(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE + "\n" + OutstandingFee.MESSAGE_CONSTRAINTS),
            Map.entry(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_USAGE),
            Map.entry(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE),
            Map.entry(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE),
            Map.entry(ExitCommand.COMMAND_WORD, ExitCommand.MESSAGE_USAGE),
            Map.entry(FeesCommand.COMMAND_WORD, FeesCommand.MESSAGE_USAGE),
            Map.entry(FeeSummaryCommand.COMMAND_WORD, FeeSummaryCommand.MESSAGE_USAGE
                    + "\nIncludes all stored students and leaves the displayed list unchanged."),
            Map.entry(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE),
            Map.entry(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE),
            Map.entry(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_USAGE),
            Map.entry(PaidCommand.COMMAND_WORD, PaidCommand.MESSAGE_USAGE
                    + "\nClears the whole outstanding fee; the student must have a fee to pay."),
            Map.entry(SetOutstandingCommand.COMMAND_WORD, SetOutstandingCommand.MESSAGE_USAGE
                    + "\n" + SetOutstandingCommand.MESSAGE_INVALID_AMOUNT + " Use 0 to clear the fee."),
            Map.entry(ViewCommand.COMMAND_WORD, ViewCommand.MESSAGE_USAGE));

    private final String targetCommand;

    /**
     * Creates a command that opens the existing help window.
     */
    public HelpCommand() {
        targetCommand = null;
    }

    /**
     * Creates read-only help for an implemented command word.
     */
    public HelpCommand(String targetCommand) {
        requireNonNull(targetCommand);
        checkArgument(isSupportedCommand(targetCommand), "Unknown help command: " + targetCommand);
        this.targetCommand = targetCommand;
    }

    /**
     * Returns whether help is available for this exact command word.
     */
    public static boolean isSupportedCommand(String commandWord) {
        return COMMAND_USAGE.containsKey(requireNonNull(commandWord));
    }

    /**
     * Returns the implemented command words in a stable alphabetical order.
     */
    public static String getAvailableCommands() {
        return COMMAND_USAGE.keySet().stream().sorted().collect(Collectors.joining(", "));
    }

    @Override
    public CommandResult execute(Model model) {
        return targetCommand == null ? new CommandResult(SHOWING_HELP_MESSAGE, true, false)
                : new CommandResult(COMMAND_USAGE.get(targetCommand));
    }

    @Override
    public boolean shouldSaveAddressBook() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof HelpCommand command)) {
            return false;
        }
        return Objects.equals(targetCommand, command.targetCommand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetCommand);
    }
}
