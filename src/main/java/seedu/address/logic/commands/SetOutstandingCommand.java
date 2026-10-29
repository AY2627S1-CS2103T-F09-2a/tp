package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;

/**
 * Replaces, adds to, or clears the outstanding fee of a displayed student.
 */
public class SetOutstandingCommand extends Command {

    public static final String COMMAND_WORD = "setoutstanding";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sets the outstanding fee of the student identified by the displayed index.\n"
            + "Write the amount as +AMOUNT to add it to the fee that is already recorded.\n"
            + "Parameters: INDEX AMOUNT or INDEX +AMOUNT\n"
            + "Use the " + Messages.MESSAGE_INDEX_CONSTRAINTS + ".\n"
            + "Example: " + COMMAND_WORD + " 1 50.00\n"
            + "Example: " + COMMAND_WORD + " 1 +50.00 (adds S$50.00 to the fee already recorded)";
    public static final String MESSAGE_SUCCESS = "Set outstanding fee for %1$s: S$%2$s";
    public static final String MESSAGE_SUCCESS_ADDED =
            "Added S$%2$s to the outstanding fee for %1$s: S$%3$s -> S$%4$s";
    public static final String MESSAGE_INVALID_AMOUNT =
            "Outstanding fee must be between 0 and 99999.99, with at most two decimal places.";
    public static final String MESSAGE_INVALID_INCREMENT =
            "Amount to add must be between 0.01 and 99999.99, with at most two decimal places.";
    public static final String MESSAGE_TOTAL_TOO_LARGE =
            "Adding S$%1$s would make the outstanding fee S$%2$s, above the maximum of S$99999.99. "
                    + "Record a smaller amount.";

    private final Index index;
    private final OutstandingFee outstandingFee;
    private final boolean isIncrement;

    /**
     * Creates a command targeting the displayed index; a null fee clears the amount.
     */
    public SetOutstandingCommand(Index index, OutstandingFee outstandingFee) {
        this(index, outstandingFee, false);
    }

    private SetOutstandingCommand(Index index, OutstandingFee outstandingFee, boolean isIncrement) {
        this.index = requireNonNull(index);
        this.outstandingFee = outstandingFee;
        this.isIncrement = isIncrement;
    }

    /**
     * Creates a command that adds {@code increment} to the fee already recorded for the displayed
     * student, or sets that amount when the student has no fee yet.
     */
    public static SetOutstandingCommand addToOutstandingFee(Index index, OutstandingFee increment) {
        return new SetOutstandingCommand(index, requireNonNull(increment), true);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }
        Person student = displayed.get(index.getZeroBased());
        OutstandingFee previousFee = student.getOutstandingFee().orElse(null);
        OutstandingFee updatedFee = isIncrement ? addTo(previousFee) : outstandingFee;
        Person updated = new Person(student.getName(), student.getPhone().orElse(null),
                student.getEmail().orElse(null), student.getAddress().orElse(null),
                student.getGuardianName(), student.getGuardianPhone(), updatedFee, student.getTags());
        model.setPerson(student, updated);
        if (isIncrement) {
            return new CommandResult(String.format(MESSAGE_SUCCESS_ADDED, updated.getName(),
                    outstandingFee.toString(), amountText(previousFee), updatedFee.toString()));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, updated.getName(), amountText(updatedFee)));
    }

    /**
     * Returns the result of adding this command's amount to {@code previousFee}, which is null when
     * the student has no fee yet.
     */
    private OutstandingFee addTo(OutstandingFee previousFee) throws CommandException {
        BigDecimal previous = previousFee == null ? BigDecimal.ZERO : previousFee.value;
        String total = previous.add(outstandingFee.value).toPlainString();
        if (!OutstandingFee.isValidOutstandingFee(total)) {
            throw new CommandException(String.format(MESSAGE_TOTAL_TOO_LARGE,
                    outstandingFee.toString(), total));
        }
        return new OutstandingFee(total);
    }

    private static String amountText(OutstandingFee fee) {
        return fee == null ? "0.00" : fee.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SetOutstandingCommand command
                && index.equals(command.index) && isIncrement == command.isIncrement
                && Objects.equals(outstandingFee, command.outstandingFee);
    }
}
