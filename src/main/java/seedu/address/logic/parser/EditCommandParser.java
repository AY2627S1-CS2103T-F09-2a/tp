package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OUTSTANDING_FEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_CLEAR_TAGS_WITH_TAGS = "t/ on its own clears all tags, so it cannot be "
            + "combined with other tags. Use t/ alone to clear the tags, or give only the tags to keep.";
    public static final String MESSAGE_REPEATED_CLEAR_TAGS = "t/ on its own clears all tags, so give it only once.";

    private static final Logger logger = LogsCenter.getLogger(EditCommandParser.class);

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_TAG, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE, PREFIX_OUTSTANDING_FEE);

        Index index = ParserUtil.parseIndex(argMultimap.getPreamble(),
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        // Outstanding fees cannot be edited via edit; reject the prefix rather than absorbing it into another value.
        if (argMultimap.getValue(PREFIX_OUTSTANDING_FEE).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        // An optional contact prefix with no value, such as p/, removes that field.
        Optional<String> phone = argMultimap.getValue(PREFIX_PHONE);
        if (phone.isPresent()) {
            if (phone.get().isBlank()) {
                editPersonDescriptor.clearPhone();
            } else {
                editPersonDescriptor.setPhone(ParserUtil.parsePhone(phone.get()));
            }
        }
        Optional<String> email = argMultimap.getValue(PREFIX_EMAIL);
        if (email.isPresent()) {
            if (email.get().isBlank()) {
                editPersonDescriptor.clearEmail();
            } else {
                editPersonDescriptor.setEmail(ParserUtil.parseEmail(email.get()));
            }
        }
        Optional<String> address = argMultimap.getValue(PREFIX_ADDRESS);
        if (address.isPresent()) {
            if (address.get().isBlank()) {
                editPersonDescriptor.clearAddress();
            } else {
                editPersonDescriptor.setAddress(ParserUtil.parseAddress(address.get()));
            }
        }
        if (argMultimap.getValue(PREFIX_GUARDIAN_NAME).isPresent()) {
            editPersonDescriptor.setGuardianName(
                    ParserUtil.parseGuardianName(argMultimap.getValue(PREFIX_GUARDIAN_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_GUARDIAN_PHONE).isPresent()) {
            editPersonDescriptor.setGuardianPhone(
                    ParserUtil.parseGuardianPhone(argMultimap.getValue(PREFIX_GUARDIAN_PHONE).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        logger.fine("Parsed EditCommand for index " + index.getOneBased());
        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        if (!tags.contains("")) {
            return Optional.of(ParserUtil.parseTags(tags));
        }
        if (tags.size() == 1) {
            return Optional.of(Collections.emptySet());
        }
        boolean onlyClears = tags.stream().allMatch(String::isEmpty);
        throw new ParseException(onlyClears ? MESSAGE_REPEATED_CLEAR_TAGS : MESSAGE_CLEAR_TAGS_WITH_TAGS);
    }

}
