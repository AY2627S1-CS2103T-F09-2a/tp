package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ViewCommand}.
 */
public class ViewCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_fullRecord_displaysEveryField() throws Exception {
        Person student = new seedu.address.testutil.PersonBuilder().withName("John Doe")
                .withGuardianName("Jane Doe").withGuardianPhone("91234567").withOutstandingFee("25.5")
                .withTags("music", "advanced").build();
        Model isolatedModel = new ModelManager();
        isolatedModel.addPerson(student);
        String expected = "Viewing student: John Doe\nPhone: 85355255\nEmail: amy@gmail.com"
                + "\nAddress: 123, Jurong West Ave 6, #08-111\nGuardian name: Jane Doe"
                + "\nGuardian phone: 91234567\nOutstanding fee: 25.50\nTags: advanced, music";
        assertEquals(expected, new ViewCommand(INDEX_FIRST_PERSON).execute(isolatedModel).getFeedbackToUser());
        assertEquals(student, isolatedModel.getFilteredPersonList().getFirst());
    }

    @Test
    public void formatRecord_optionalFieldsMissing_displaysNone() {
        Person student = new Person(new seedu.address.model.person.Name("John Doe"),
                seedu.address.model.person.Phone.empty(), seedu.address.model.person.Email.empty(),
                seedu.address.model.person.Address.empty(), new seedu.address.model.person.GuardianName("Jane Doe"),
                new seedu.address.model.person.GuardianPhone("91234567"), null, java.util.Set.of());
        assertEquals("John Doe\nPhone: None\nEmail: None\nAddress: None\nGuardian name: Jane Doe"
                + "\nGuardian phone: 91234567\nOutstanding fee: None\nTags: None", ViewCommand.formatRecord(student));
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewCommand(null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_STUDENT_SUCCESS,
                ViewCommand.formatRecord(personToView));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_lastIndexUnfilteredList_success() {
        Index lastIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Person personToView = model.getFilteredPersonList().get(lastIndex.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(lastIndex);

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_STUDENT_SUCCESS,
                ViewCommand.formatRecord(personToView));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ViewCommand viewCommand = new ViewCommand(outOfBoundIndex);

        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        // the first index in the filtered list refers to the second person in the address book
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_STUDENT_SUCCESS,
                ViewCommand.formatRecord(BENSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(viewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        ViewCommand viewCommand = new ViewCommand(outOfBoundIndex);

        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyDisplayedList_throwsCommandException() {
        model.updateFilteredPersonList(p -> false);
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_PERSON);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        ViewCommand viewFirstCommandCopy = new ViewCommand(INDEX_FIRST_PERSON);
        assertTrue(viewFirstCommand.equals(viewFirstCommandCopy));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ViewCommand viewCommand = new ViewCommand(targetIndex);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, viewCommand.toString());
    }
}
