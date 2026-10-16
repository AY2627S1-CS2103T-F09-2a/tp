package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.GuardianContainsKeywordsPredicate;

/**
 * Lists students matching a guardian-name word or exact guardian phone number.
 */
public class FindGuardianCommand extends Command {
    public static final String COMMAND_WORD = "findguardian";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds students by whole guardian-name words (case-insensitive) or exact guardian phone.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\nExample: " + COMMAND_WORD + " Mei 91234567";

    private final GuardianContainsKeywordsPredicate predicate;

    public FindGuardianCommand(GuardianContainsKeywordsPredicate predicate) {
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
    public boolean shouldSaveAddressBook() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FindGuardianCommand command && predicate.equals(command.predicate);
    }
}
