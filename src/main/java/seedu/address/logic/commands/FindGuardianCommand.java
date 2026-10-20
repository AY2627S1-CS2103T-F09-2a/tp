package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.GuardianContainsKeywordsPredicate;

/**
 * Finds and lists all students whose guardian name contains any of the argument keywords,
 * or whose guardian phone equals any of them. Name matching is case insensitive.
 */
public class FindGuardianCommand extends Command {

    public static final String COMMAND_WORD = "findguardian";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all students whose guardian names contain "
            + "any of the specified words (case-insensitive) or whose guardian phone matches any 8-digit number "
            + "given, and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " mei 97654321";

    private final GuardianContainsKeywordsPredicate predicate;

    public FindGuardianCommand(GuardianContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindGuardianCommand otherFindGuardianCommand)) {
            return false;
        }

        return predicate.equals(otherFindGuardianCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }

    @Override
    public boolean shouldSaveAddressBook() {
        return false;
    }
}
