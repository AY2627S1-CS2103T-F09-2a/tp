package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses optional help for one implemented command without executing that command.
 * The target command word is matched in any letter case, as in {@link AddressBookParser}.
 */
public class HelpCommandParser implements Parser<HelpCommand> {
    @Override
    public HelpCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.strip();
        if (trimmedArgs.isEmpty()) {
            return new HelpCommand();
        }
        // Splits on the same whitespace that strip() removes, including Unicode spaces such as U+2003
        boolean hasExtraWords = trimmedArgs.split("\\p{javaWhitespace}+").length > 1;
        if (hasExtraWords) {
            throw new ParseException(HelpCommand.MESSAGE_EXTRA_WORDS);
        }
        String targetCommand = ParserUtil.normalizeCommandWord(trimmedArgs);
        if (!HelpCommand.isSupportedCommand(targetCommand)) {
            throw new ParseException(String.format(MESSAGE_UNKNOWN_COMMAND, trimmedArgs) + "\n"
                    + HelpCommand.getAvailableCommandsMessage());
        }
        return new HelpCommand(targetCommand);
    }
}
