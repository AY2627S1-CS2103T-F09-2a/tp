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
            + "A student who still owes a fee cannot be deleted.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted student:\n%1$s";
    public static final String MESSAGE_STUDENT_OWES_FEE = "Cannot delete %1$s, who still owes S$%2$s.\n"
            + "Before deleting, record the payment with paid INDEX or clear the fee with setoutstanding INDEX 0.";

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
        if (!isDeletable(personToDelete)) {
            // A student who cannot be deleted always owes a fee (see isDeletable), so the fee is present here
            throw new CommandException(String.format(MESSAGE_STUDENT_OWES_FEE, personToDelete.getName(),
                    personToDelete.getOutstandingFee().orElseThrow()));
        }

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    /**
     * Returns true if {@code student} can be deleted. A student who still owes a fee cannot be deleted,
     * so that an unpaid fee is never lost by accident.
     */
    public static boolean isDeletable(Person student) {
        requireNonNull(student);
        return student.getOutstandingFee().isEmpty();
    }

    /**
     * Returns the student at this command's index in {@code displayedList},
     * or an empty {@code Optional} if the index is beyond the end of the list.
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
