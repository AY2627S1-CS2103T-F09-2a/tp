package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Represents the command for listing students with outstanding fees.
 *
 * <p>The filtering behaviour will be added after outstanding-fee data is available in the model.
 */
public class FeesCommand extends Command {

    public static final String COMMAND_WORD = "fees";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all students with outstanding fees.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Outstanding-fee filtering is not available yet.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
