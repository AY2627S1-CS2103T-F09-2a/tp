package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Represents the command for displaying students with outstanding fees.
 */
public class FeesCommand extends Command {

    public static final String COMMAND_WORD = "fees";

    public static final String MESSAGE_USAGE = COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "%1$d student(s) with outstanding fees.";

    public static final Predicate<Person> PREDICATE_HAS_OUTSTANDING_FEE =
            person -> person.getOutstandingFee() != null;

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_HAS_OUTSTANDING_FEE);
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FeesCommand;
    }
}
