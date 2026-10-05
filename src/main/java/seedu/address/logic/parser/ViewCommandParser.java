package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;

import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewCommand object.
 */
public class ViewCommandParser implements Parser<ViewCommand> {

    /**
     * Used to accept exactly one unsigned base-10 integer without leading zeros, e.g. "1" but not "01" or "+1".
     */
    private static final Pattern INDEX_FORMAT = Pattern.compile("[1-9][0-9]*");

    /**
     * Parses the given {@code String} of arguments in the context of the ViewCommand
     * and returns a ViewCommand object for execution.
     *
     * @throws ParseException If the user input does not conform to the expected format,
     *     or if the index is too large to be in any displayed list.
     */
    public ViewCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (!INDEX_FORMAT.matcher(trimmedArgs).matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
        }

        try {
            return new ViewCommand(Index.fromOneBased(Integer.parseInt(trimmedArgs)));
        } catch (NumberFormatException nfe) {
            // A well-formed index that does not fit in an int is outside every displayed list.
            throw new ParseException(MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, nfe);
        }
    }

}
