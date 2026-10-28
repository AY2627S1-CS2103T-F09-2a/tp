package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FeesCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

/**
 * Tests {@code AddressBookParser}. The class runs in isolation because one test changes the JVM-wide default locale,
 * which must not affect tests in other classes if JUnit parallel execution is ever enabled.
 */
@Isolated
public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_fees() throws Exception {
        assertTrue(parser.parseCommand(FeesCommand.COMMAND_WORD) instanceof FeesCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertEquals(new HelpCommand(), parser.parseCommand(HelpCommand.COMMAND_WORD));
        assertEquals(new HelpCommand("paid"), parser.parseCommand("help paid"));
        assertThrows(ParseException.class, () -> parser.parseCommand("help 3"));
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(
                ViewCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_commandWordInAnyCase_matchesLowerCaseCommandWord() throws Exception {
        assertTrue(parser.parseCommand("LIST") instanceof ListCommand);
        assertTrue(parser.parseCommand("List") instanceof ListCommand);
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), parser.parseCommand("DELETE 1"));
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), parser.parseCommand("vIeW 1"));
        assertEquals(new HelpCommand(), parser.parseCommand("Help"));
        assertEquals(new HelpCommand("paid"), parser.parseCommand("HELP PAID"));
    }

    @Test
    public void parseCommand_commandWordInAnyCase_passesArgumentsUnchanged() throws Exception {
        Person person = new PersonBuilder().build();
        assertEquals(new AddCommand(person), parser.parseCommand("ADD " + PersonUtil.getPersonDetails(person)));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(List.of("Alex", "YEOH"))),
                parser.parseCommand("FIND Alex YEOH"));
        // Prefixes are still matched exactly, so upper-case prefixes are read as preamble text
        String upperCasePrefixes = PersonUtil.getPersonDetails(person).toUpperCase(Locale.ROOT);
        String addFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, addFormatMessage, () -> parser.parseCommand("add " + upperCasePrefixes));
    }

    @Test
    public void parseCommand_upperCaseInTurkishLocale_matchesCommandWord() throws Exception {
        Locale defaultLocale = Locale.getDefault();
        Locale displayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        Locale formatLocale = Locale.getDefault(Locale.Category.FORMAT);
        try {
            // In Turkish, the default lower case of "I" is a dotless "ı", so "LIST" would become "lıst"
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(parser.parseCommand("LIST") instanceof ListCommand);
            assertEquals(new HelpCommand("findguardian"), parser.parseCommand("HELP FINDGUARDIAN"));
        } finally {
            // setDefault(Locale) also replaces the category defaults, so restore those afterwards
            Locale.setDefault(defaultLocale);
            Locale.setDefault(Locale.Category.DISPLAY, displayLocale);
            Locale.setDefault(Locale.Category.FORMAT, formatLocale);
        }
    }

    @Test
    public void parseCommand_everyAvailableCommandWord_isRecognisedInAnyCase() {
        for (String commandWord : HelpCommand.getAvailableCommands().split(", ")) {
            assertRecognisedCommandWord(commandWord);
            assertRecognisedCommandWord(commandWord.toUpperCase(Locale.ROOT));
        }
    }

    @Test
    public void parseCommand_unknownCommand_namesWordAndListsAvailableCommands() {
        for (String commandWord : List.of("unknownCommand", "lsit", "LSIT", "list.", "listt")) {
            assertThrows(ParseException.class, getUnknownCommandMessage(commandWord), ()
                -> parser.parseCommand(commandWord + " 1"));
        }
        assertThrows(ParseException.class, "Unknown command: \"lsit\". "
                + "Type help COMMAND to see a command's format and an example.\n"
                + "Available commands: " + HelpCommand.getAvailableCommands(), () -> parser.parseCommand("lsit"));
    }

    /**
     * Asserts that {@code typedWord} is passed to a command or its parser instead of being reported as unknown.
     * A command that needs arguments may still report its own format error.
     */
    private void assertRecognisedCommandWord(String typedWord) {
        try {
            parser.parseCommand(typedWord);
        } catch (ParseException e) {
            assertNotEquals(getUnknownCommandMessage(typedWord), e.getMessage(), typedWord);
        }
    }

    private static String getUnknownCommandMessage(String commandWord) {
        return String.format(MESSAGE_UNKNOWN_COMMAND, commandWord) + " " + HelpCommand.MESSAGE_HELP_HINT + "\n"
                + HelpCommand.getAvailableCommandsMessage();
    }
}
