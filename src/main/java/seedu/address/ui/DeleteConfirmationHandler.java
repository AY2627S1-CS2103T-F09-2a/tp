package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;

/**
 * Asks the user to confirm a {@code delete} command, and executes it through {@code Logic} only if the user confirms.
 */
public class DeleteConfirmationHandler {

    public static final String MESSAGE_DELETE_CANCELLED = "Deletion cancelled. No student was deleted.";
    public static final String MESSAGE_TARGET_CHANGED =
            "The displayed list changed before the deletion was confirmed. No student was deleted.";

    private final Logger logger = LogsCenter.getLogger(DeleteConfirmationHandler.class);

    private final Logic logic;
    private final DeletionConfirmer confirmer;

    /**
     * Creates a {@code DeleteConfirmationHandler} that asks {@code confirmer} before any student is deleted
     * and executes confirmed commands using {@code logic}.
     */
    public DeleteConfirmationHandler(Logic logic, DeletionConfirmer confirmer) {
        requireAllNonNull(logic, confirmer);
        this.logic = logic;
        this.confirmer = confirmer;
    }

    /**
     * Asks the user whether to delete {@code student}, the student that {@code commandText} would delete,
     * and executes {@code commandText} only if the user confirms.
     *
     * @return the result of the deletion, or a cancellation message if the user does not confirm.
     * @throws CommandException If an error occurs during command execution, or if the displayed student at the
     *     target index changes before the deletion is confirmed.
     * @throws ParseException If an error occurs during parsing.
     */
    public CommandResult confirmAndDelete(String commandText, Person student) throws CommandException, ParseException {
        requireAllNonNull(commandText, student);
        logger.info("Asking the user to confirm: " + commandText);
        if (!confirmer.confirm(student)) {
            logger.info("Deletion cancelled by the user: " + commandText);
            return new CommandResult(MESSAGE_DELETE_CANCELLED);
        }

        if (!logic.getDeletionTarget(commandText).equals(Optional.of(student))) {
            logger.warning("Displayed student changed during confirmation, so nothing was deleted: " + commandText);
            throw new CommandException(MESSAGE_TARGET_CHANGED);
        }
        return logic.execute(commandText);
    }

    /**
     * Represents a function that asks the user whether a student should be deleted.
     */
    @FunctionalInterface
    public interface DeletionConfirmer {
        /**
         * Returns true if the user confirms that {@code student} should be deleted.
         */
        boolean confirm(Person student);
    }
}
