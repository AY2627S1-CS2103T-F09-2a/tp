package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;

/**
 * Replaces or clears the outstanding fee of a displayed student.
 */
public class SetOutstandingCommand extends Command {

    public static final String COMMAND_WORD = "setoutstanding";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " INDEX AMOUNT\n"
            + "AMOUNT must be 0 to clear, or 0.01 to 99999.99 with at most two decimal places.";
    public static final String MESSAGE_SUCCESS = "Outstanding fee for %1$s: S$%2$s";

    private final Index index;
    private final OutstandingFee outstandingFee;

    /**
     * Creates a command targeting the displayed index; a null fee clears the amount.
     */
    public SetOutstandingCommand(Index index, OutstandingFee outstandingFee) {
        this.index = requireNonNull(index);
        this.outstandingFee = outstandingFee;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }
        Person student = displayed.get(index.getZeroBased());
        Person updated = new Person(student.getName(), student.getPhone(), student.getEmail(), student.getAddress(),
                student.getGuardianName(), student.getGuardianPhone(), outstandingFee, student.getTags());
        model.setPerson(student, updated);
        return new CommandResult(String.format(MESSAGE_SUCCESS, updated.getName(),
                outstandingFee == null ? "0.00" : outstandingFee));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SetOutstandingCommand command
                && index.equals(command.index) && Objects.equals(outstandingFee, command.outstandingFee);
    }
}
