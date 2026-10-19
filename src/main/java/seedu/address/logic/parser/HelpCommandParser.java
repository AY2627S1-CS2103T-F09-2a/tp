package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses optional help for one implemented command without executing that command.
 */
public class HelpCommandParser implements Parser<HelpCommand> {
    @Override
    public HelpCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String commandWord = args.strip();
        if (commandWord.isEmpty()) {
            return new HelpCommand();
        }
        if (!HelpCommand.isSupportedCommand(commandWord)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE)
                    + "\nAvailable commands: " + HelpCommand.getAvailableCommands());
        }
        return new HelpCommand(commandWord);
    }
}
