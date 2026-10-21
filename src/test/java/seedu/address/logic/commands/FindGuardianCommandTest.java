package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.FindGuardianCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.GuardianContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindGuardianCommand}.
 */
public class FindGuardianCommandTest {
    private static final Person ZACK = new PersonBuilder().withName("Zack Lim")
            .withGuardianName("Mary Lim").withGuardianPhone("87654321").build();
    private static final Person YVONNE = new PersonBuilder().withName("Yvonne Lim")
            .withGuardianName("Mary Lim").withGuardianPhone("87654321").build();

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindGuardianCommand(null));
    }

    @Test
    public void equals() {
        GuardianContainsKeywordsPredicate firstPredicate =
                new GuardianContainsKeywordsPredicate(List.of("first"));
        GuardianContainsKeywordsPredicate secondPredicate =
                new GuardianContainsKeywordsPredicate(List.of("second"));

        FindGuardianCommand findFirstCommand = new FindGuardianCommand(firstPredicate);
        FindGuardianCommand findSecondCommand = new FindGuardianCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindGuardianCommand findFirstCommandCopy = new FindGuardianCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different keywords -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_guardianNameKeyword_personsFound() {
        // "Meier" is also a student surname, but only Benson's guardian has it
        assertFindGuardianResult("Meier", List.of(BENSON));
    }

    @Test
    public void execute_keywordInDifferentCase_personsFound() {
        assertFindGuardianResult("pAULINE", List.of(ALICE));
    }

    @Test
    public void execute_multipleKeywords_matchesAnyKeyword() {
        assertFindGuardianResult("Zelda Meier Pauline", List.of(ALICE, BENSON));
    }

    @Test
    public void execute_guardianPhoneKeyword_personFound() {
        model.addPerson(ZACK);
        expectedModel.addPerson(ZACK);
        assertFindGuardianResult("87654321", List.of(ZACK));
    }

    @Test
    public void execute_hyphenatedPhoneKeyword_personFound() throws ParseException {
        model.addPerson(ZACK);
        expectedModel.addPerson(ZACK);
        assertParsedFindGuardianResult("8765-4321", List.of(ZACK));
    }

    @Test
    public void execute_sharedGuardian_allSiblingsFoundOnce() throws ParseException {
        model.addPerson(ZACK);
        model.addPerson(YVONNE);
        expectedModel.addPerson(ZACK);
        expectedModel.addPerson(YVONNE);

        // a prior filter hides both siblings
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Kurz")));

        // both siblings match the name and the phone, but each is listed once in stored order
        assertParsedFindGuardianResult("  Mary \t 87654321 ", List.of(ZACK, YVONNE));

        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(expectedModel.getAddressBook(), model.getAddressBook());
    }

    @Test
    public void execute_partialKeyword_noPersonFound() {
        // "Paul" is only a prefix of "Pauline", and "8765" only part of a phone
        model.addPerson(ZACK);
        expectedModel.addPerson(ZACK);
        assertFindGuardianResult("Paul 8765", List.of());
    }

    @Test
    public void execute_studentNameKeyword_noPersonFound() {
        // "Kurz" is Carl's surname but not his guardian's name
        assertFindGuardianResult("Kurz", List.of());
    }

    @Test
    public void execute_filteredList_searchesAllStudents() {
        NameContainsKeywordsPredicate carlOnly = new NameContainsKeywordsPredicate(List.of("Kurz"));
        model.updateFilteredPersonList(carlOnly);
        assertEquals(List.of(CARL), model.getFilteredPersonList());

        assertFindGuardianResult("Pauline", List.of(ALICE));
    }

    @Test
    public void execute_findGuardian_addressBookUnchanged() {
        assertFindGuardianResult("Pauline", List.of(ALICE));
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    @Test
    public void toStringMethod() {
        GuardianContainsKeywordsPredicate predicate = new GuardianContainsKeywordsPredicate(List.of("keyword"));
        FindGuardianCommand findGuardianCommand = new FindGuardianCommand(predicate);
        String expected = FindGuardianCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findGuardianCommand.toString());
    }

    @Test
    public void shouldSaveAddressBook_findGuardianCommand_returnsFalse() {
        assertFalse(new FindGuardianCommand(new GuardianContainsKeywordsPredicate(List.of("Mei")))
                .shouldSaveAddressBook());
    }

    /**
     * Parses {@code userInput} with {@code FindGuardianCommandParser}, executes the command and asserts
     * that exactly {@code expectedPersons} are listed, in the given order.
     */
    private void assertParsedFindGuardianResult(String userInput, List<Person> expectedPersons)
            throws ParseException {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size());
        FindGuardianCommand command = new FindGuardianCommandParser().parse(userInput);
        expectedModel.updateFilteredPersonList(new GuardianContainsKeywordsPredicate(
                List.of(userInput.trim().split("\\s+"))));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(expectedPersons, model.getFilteredPersonList());
    }

    /**
     * Executes a guardian search with {@code userInput} as keywords and asserts that exactly
     * {@code expectedPersons} are listed, in the given order.
     */
    private void assertFindGuardianResult(String userInput, List<Person> expectedPersons) {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size());
        GuardianContainsKeywordsPredicate predicate =
                new GuardianContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
        FindGuardianCommand command = new FindGuardianCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(expectedPersons, model.getFilteredPersonList());
    }
}
