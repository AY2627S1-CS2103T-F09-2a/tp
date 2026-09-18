package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Lists persons with any of the specified tags without modifying their records.
 */
public class FindTagCommand extends Command {
    public static final String COMMAND_WORD = "findtag";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists persons with any specified tag (exact matching, ignoring case).\n"
            + "Parameters: TAG [MORE_TAGS]...\n"
            + "Example: " + COMMAND_WORD + " friends colleagues";

    private final TagContainsKeywordsPredicate predicate;

    /**
     * Creates a tag-search command with the given matching predicate.
     */
    public FindTagCommand(TagContainsKeywordsPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FindTagCommand otherCommand)) {
            return false;
        }
        return predicate.equals(otherCommand.predicate);
    }
}
