package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FeesCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code FeesCommand} object.
 */
public class FeesCommandParser implements Parser<FeesCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FeesCommand.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public FeesCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FeesCommand.MESSAGE_USAGE));
        }

        return new FeesCommand();
    }
}
