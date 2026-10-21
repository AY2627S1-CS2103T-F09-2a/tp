package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;

/**
 * Clears a displayed student's full outstanding fee after payment.
 */
public class PaidCommand extends Command {
    public static final String COMMAND_WORD = "paid";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks a displayed student's outstanding fee as paid.\n"
            + "Parameters: INDEX (current displayed index, starting at 1, without leading zeros)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Marked outstanding fee as paid for %1$s: S$%2$s";
    public static final String MESSAGE_NO_FEE = "This student has no outstanding fee to mark as paid: %1$s";

    private final Index index;

    public PaidCommand(Index index) {
        this.index = requireNonNull(index);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }
        Person student = displayed.get(index.getZeroBased());
        OutstandingFee removed = student.getOutstandingFee().orElseThrow(() ->
                new CommandException(String.format(MESSAGE_NO_FEE, student.getName())));
        Person updated = new Person(student.getName(), student.getPhone().orElse(null),
                student.getEmail().orElse(null), student.getAddress().orElse(null), student.getGuardianName(),
                student.getGuardianPhone(), null, student.getTags());
        model.setPerson(student, updated);
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), removed));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof PaidCommand command && index.equals(command.index);
    }
}
