package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Represents the command for displaying students with outstanding fees.
 */
public class FeesCommand extends Command {

    public static final String COMMAND_WORD = "fees";

    public static final String MESSAGE_USAGE = COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Outstanding-fee filtering is not available yet.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FeesCommand;
    }
}
