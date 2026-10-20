package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.function.Predicate;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Represents the command for displaying students with outstanding fees.
 */
public class FeesCommand extends Command {

    public static final String COMMAND_WORD = "fees";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists students with outstanding fees, optionally at least a minimum amount.\n"
            + "Parameters: [min/AMOUNT]\n"
            + "AMOUNT must be between 0 and 99999.99, with at most two decimal places and no sign.\n"
            + "Example: " + COMMAND_WORD + " min/100";

    public static final String MESSAGE_SUCCESS = "%1$d student(s) with outstanding fees.";

    public static final Predicate<Person> PREDICATE_HAS_OUTSTANDING_FEE =
            person -> person.getOutstandingFee().isPresent();

    private final BigDecimal minimumAmount;

    /**
     * Creates a command listing all students with outstanding fees.
     */
    public FeesCommand() {
        this(BigDecimal.ZERO);
    }

    /**
     * Creates a command listing students owing at least the given minimum amount.
     */
    public FeesCommand(BigDecimal minimumAmount) {
        this.minimumAmount = requireNonNull(minimumAmount);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(minimumAmount.signum() == 0
                ? PREDICATE_HAS_OUTSTANDING_FEE
                : person -> person.getOutstandingFee()
                        .filter(fee -> fee.value.compareTo(minimumAmount) >= 0).isPresent());
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FeesCommand command
                && minimumAmount.compareTo(command.minimumAmount) == 0;
    }

    @Override
    public int hashCode() {
        return minimumAmount.stripTrailingZeros().hashCode();
    }

    @Override
    public boolean shouldSaveAddressBook() {
        return false;
    }
}
