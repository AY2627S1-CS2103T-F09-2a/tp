package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;
import seedu.address.model.person.Name;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Matches a token that looks like a prefix: letters followed by '/' at the start of a word. S/O and D/O
     * written as separate words are part of names, not prefixes.
     */
    private static final Pattern PREFIX_LIKE_TOKEN =
            Pattern.compile("(?<=^|\\s)(?!" + Name.PATRONYMIC_MARKER + "(?:\\s|$))([A-Za-z]+/)");

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed. Leading zeros and non-ASCII digits are not accepted.
     *
     * @throws ParseException if the index has invalid syntax or exceeds {@code Integer.MAX_VALUE}.
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        return parseIndex(oneBasedIndex, MESSAGE_INVALID_INDEX);
    }

    /**
     * Parses an index using {@code invalidFormatMessage} for malformed input. Positive integers exceeding
     * {@code Integer.MAX_VALUE} produce the invalid-student-index message instead.
     *
     * @throws ParseException if the index has invalid syntax or exceeds {@code Integer.MAX_VALUE}.
     */
    public static Index parseIndex(String oneBasedIndex, String invalidFormatMessage) throws ParseException {
        requireNonNull(oneBasedIndex);
        requireNonNull(invalidFormatMessage);
        String trimmedIndex = oneBasedIndex.trim();
        if (!trimmedIndex.matches("[1-9][0-9]*")) {
            throw new ParseException(invalidFormatMessage);
        }
        try {
            return Index.fromOneBased(Integer.parseInt(trimmedIndex));
        } catch (NumberFormatException nfe) {
            throw new ParseException(MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, nfe);
        }
    }

    /**
     * Returns {@code commandWord} in lower case so that a command word typed in any letter case, such as
     * {@code LIST}, matches its lower-case {@code COMMAND_WORD}.
     * {@code Locale.ROOT} is used so that the result does not depend on the computer's language; in a Turkish
     * locale, for example, the default lower case of {@code LIST} is not {@code list}.
     */
    public static String normalizeCommandWord(String commandWord) {
        requireNonNull(commandWord);
        return commandWord.toLowerCase(Locale.ROOT);
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String guardianName} into a {@code GuardianName}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code guardianName} is invalid.
     */
    public static GuardianName parseGuardianName(String guardianName) throws ParseException {
        requireNonNull(guardianName);
        String trimmedGuardianName = guardianName.trim();
        if (!GuardianName.isValidName(trimmedGuardianName)) {
            throw new ParseException(GuardianName.MESSAGE_CONSTRAINTS);
        }
        return new GuardianName(trimmedGuardianName);
    }

    /**
     * Parses a {@code String guardianPhone} into a {@code GuardianPhone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code guardianPhone} is invalid.
     */
    public static GuardianPhone parseGuardianPhone(String guardianPhone) throws ParseException {
        requireNonNull(guardianPhone);
        String trimmedGuardianPhone = guardianPhone.trim();
        if (!GuardianPhone.isValidPhone(trimmedGuardianPhone)) {
            throw new ParseException(GuardianPhone.MESSAGE_CONSTRAINTS);
        }
        return new GuardianPhone(trimmedGuardianPhone);
    }

    /**
     * Parses a {@code String outstandingFee} into an {@code OutstandingFee}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code outstandingFee} is invalid.
     */
    public static OutstandingFee parseOutstandingFee(String outstandingFee) throws ParseException {
        requireNonNull(outstandingFee);
        String trimmedOutstandingFee = outstandingFee.trim();
        if (hasMoneyFormatting(trimmedOutstandingFee)) {
            throw new ParseException(OutstandingFee.MESSAGE_PLAIN_AMOUNT);
        }
        if (!OutstandingFee.isValidOutstandingFee(trimmedOutstandingFee)) {
            throw new ParseException(OutstandingFee.MESSAGE_CONSTRAINTS);
        }
        return new OutstandingFee(trimmedOutstandingFee);
    }

    /**
     * Returns whether an amount uses currency notation or an extra field prefix rather than a plain number.
     */
    public static boolean hasMoneyFormatting(String amount) {
        requireNonNull(amount);
        return amount.startsWith("S$") || amount.startsWith("$") || amount.contains(",")
                || amount.startsWith("f/");
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Returns true if {@code value} contains a word that looks like a command prefix, such as {@code x/}.
     * Such text usually means the user mistyped a prefix and it was absorbed into this value.
     */
    public static boolean containsPrefixLikeToken(String value) {
        requireNonNull(value);
        return PREFIX_LIKE_TOKEN.matcher(value).find();
    }
}
