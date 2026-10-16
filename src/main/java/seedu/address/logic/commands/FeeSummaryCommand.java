package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Reports all stored outstanding fees without changing the displayed list.
 */
public class FeeSummaryCommand extends Command {
    public static final String COMMAND_WORD = "feesummary";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows the total outstanding fees across all students.\nExample: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "%1$d student(s) owe a total of S$%2$s.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        BigDecimal total = new BigDecimal("0.00");
        int count = 0;
        for (Person student : model.getAddressBook().getPersonList()) {
            if (student.getOutstandingFee().isPresent()) {
                total = total.add(student.getOutstandingFee().get().value);
                count++;
            }
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, count, total.toPlainString()));
    }

    @Override
    public boolean shouldSaveAddressBook() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FeeSummaryCommand;
    }
}
