package seedu.address.logic.commands;

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
    public void execute_fromFilteredList_showsAllAndConfirmsCompleteRecord() throws Exception {
        model.updateFilteredPersonList(person -> false);
        Person student = new PersonBuilder().withName("John Doe").withGuardianName("Jane Doe")
                .withGuardianPhone("91234567").withOutstandingFee("25.5").withTags("music").build();
        String result = new AddCommand(student).execute(model).getFeedbackToUser();
        org.junit.jupiter.api.Assertions.assertEquals(model.getAddressBook().getPersonList(),
                model.getFilteredPersonList());
        org.junit.jupiter.api.Assertions.assertTrue(result.contains("Guardian name: Jane Doe"));
        org.junit.jupiter.api.Assertions.assertTrue(result.contains("Guardian phone: 91234567"));
        org.junit.jupiter.api.Assertions.assertTrue(result.contains("Outstanding fee: 25.50"));
        org.junit.jupiter.api.Assertions.assertTrue(result.contains("[music]"));
    }

    @Test
    public void execute_normalizedNameAndGuardianPhone_rejectsDuplicate() throws Exception {
        Person original = new PersonBuilder().withName("John Doe").withGuardianPhone("91234567").build();
        new AddCommand(original).execute(model);
        Person duplicate = new PersonBuilder(original).withName("john  doe ").withPhone("99999999").build();
        assertCommandFailure(new AddCommand(duplicate), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameDifferentGuardianPhone_success() throws Exception {
        Person original = new PersonBuilder().withName("John Doe").withGuardianPhone("91234567").build();
        new AddCommand(original).execute(model);
        Person differentStudent = new PersonBuilder(original).withGuardianPhone("81234567").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addPerson(differentStudent);
        assertCommandSuccess(new AddCommand(differentStudent), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(differentStudent)), expectedModel);
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

}
