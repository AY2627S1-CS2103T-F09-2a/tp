package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a student identified using its displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the student identified by the index number used in the displayed student list.\n"
            + "Parameters: INDEX (" + Messages.MESSAGE_INDEX_CONSTRAINTS + ")\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted student:\n%1$s";

    private final Index targetIndex;

    /**
     * Creates a {@code DeleteCommand} that deletes the student at {@code targetIndex} in the displayed list.
     */
    public DeleteCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToDelete = getTarget(model.getFilteredPersonList())
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX));

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    /**
     * Returns the student that this command would delete from {@code displayedList},
     * or an empty {@code Optional} if the target index is beyond the end of the list.
     */
    public Optional<Person> getTarget(List<Person> displayedList) {
        requireNonNull(displayedList);
        if (targetIndex.getZeroBased() >= displayedList.size()) {
            return Optional.empty();
        }
        return Optional.of(displayedList.get(targetIndex.getZeroBased()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
