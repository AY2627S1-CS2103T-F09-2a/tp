package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_specMockUp_showsCompleteRecord() throws Exception {
        Person alex = new PersonBuilder().withName("Alex Tan").withGuardianName("Mei Tan")
                .withGuardianPhone("92345678").withoutContactDetails().withOutstandingFee("180").build();

        CommandResult result = new AddCommand(alex).execute(model);

        assertEquals("New student added:\n"
                + "Name: Alex Tan\n"
                + "Phone: Not provided\n"
                + "Email: Not provided\n"
                + "Address: Not provided\n"
                + "Guardian: Mei Tan\n"
                + "Guardian phone: 92345678\n"
                + "Outstanding fee: S$180.00", result.getFeedbackToUser());
    }

    @Test
    public void execute_noFee_showsNone() throws Exception {
        Person student = new PersonBuilder().withName("Riya Kumar").build();
        CommandResult result = new AddCommand(student).execute(model);
        assertTrue(result.getFeedbackToUser().endsWith("Outstanding fee: None"));
    }

    @Test
    public void execute_filteredList_showsFullListWithNewStudentLast() throws Exception {
        model.updateFilteredPersonList(person -> false);
        Person student = new PersonBuilder().withName("Riya Kumar").build();

        new AddCommand(student).execute(model);

        assertEquals(model.getAddressBook().getPersonList().size(), model.getFilteredPersonList().size());
        assertEquals(student, model.getFilteredPersonList().get(model.getFilteredPersonList().size() - 1));
    }

    @Test
    public void execute_sameNameDifferentCaseAndSameGuardianPhone_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person duplicate = new PersonBuilder(personInList)
                .withName(personInList.getName().fullName.toUpperCase())
                .withGuardianName("Someone Else").withoutContactDetails().withOutstandingFee("10").build();
        assertCommandFailure(new AddCommand(duplicate), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameDifferentGuardianPhone_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        Person namesake = new PersonBuilder(personInList).withGuardianPhone("81234567").build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(namesake);

        assertCommandSuccess(new AddCommand(namesake), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(namesake)), expectedModel);
    }

}
