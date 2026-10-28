package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GUARDIAN_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GUARDIAN_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.GuardianName;
import seedu.address.model.person.GuardianPhone;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedFields = new PersonBuilder().build();
        Person originalPerson = model.getFilteredPersonList().get(0);
        // Edit cannot change fee fields, so they are kept from the original student.
        Person editedPerson = new PersonBuilder(editedFields)
                .withOutstandingFee(originalPerson.getOutstandingFee().map(Object::toString).orElse(null))
                .build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedFields).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = "Edited student:\n" + Messages.format(editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_guardianFieldsSpecifiedUnfilteredList_success() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson)
                .withGuardianName(VALID_GUARDIAN_NAME_BOB)
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianName(VALID_GUARDIAN_NAME_BOB)
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB)
                .build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        // The six other students in the initial model share Alice's guardian phone.
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nNote: 6 other students still have the old guardian phone 91234567: Benson Meier, "
                + "Carl Kurz, Daniel Meier, Elle Meyer, Fiona Kunz, George Best. "
                + "Edit them too if the guardian changed number.";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_guardianNameOnly_success() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson)
                .withGuardianName(VALID_GUARDIAN_NAME_BOB)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianName(VALID_GUARDIAN_NAME_BOB)
                .build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_guardianPhoneSharedBySibling_namesSibling() {
        Model siblingModel = new ModelManager(addressBookOf(ALICE, BENSON, AMY), new UserPrefs());
        Person editedPerson = new PersonBuilder(ALICE).withGuardianPhone(VALID_GUARDIAN_PHONE_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB)
                .build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nNote: 1 other student still has the old guardian phone 91234567: Benson Meier. "
                + "Edit them too if the guardian changed number.";

        Model expectedModel = new ModelManager(addressBookOf(editedPerson, BENSON, AMY), new UserPrefs());
        assertCommandSuccess(editCommand, siblingModel, expectedMessage, expectedModel);
    }

    @Test
    public void execute_guardianPhoneNotShared_noNote() {
        Model siblingModel = new ModelManager(addressBookOf(ALICE, BENSON, AMY), new UserPrefs());
        Person editedPerson = new PersonBuilder(AMY).withGuardianPhone(VALID_GUARDIAN_PHONE_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB)
                .build();
        EditCommand editCommand = new EditCommand(Index.fromOneBased(3), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(addressBookOf(ALICE, BENSON, editedPerson), new UserPrefs());
        assertCommandSuccess(editCommand, siblingModel, expectedMessage, expectedModel);
    }

    @Test
    public void execute_guardianPhoneUnchanged_noNote() {
        Model siblingModel = new ModelManager(addressBookOf(ALICE, BENSON, AMY), new UserPrefs());
        Person editedPerson = new PersonBuilder(ALICE).withGuardianName("Anne Lim").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withGuardianName("Anne Lim").build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(addressBookOf(editedPerson, BENSON, AMY), new UserPrefs());
        assertCommandSuccess(editCommand, siblingModel, expectedMessage, expectedModel);
    }

    @Test
    public void execute_guardianPhoneOnly_success() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson)
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB)
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB)
                .build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        // The six other students in the initial model share Alice's guardian phone.
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nNote: 6 other students still have the old guardian phone 91234567: Benson Meier, "
                + "Carl Kurz, Daniel Meier, Elle Meyer, Fiona Kunz, George Best. "
                + "Edit them too if the guardian changed number.";

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sharedGuardianPhoneDifferentStudentName_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        // Give the second student a different guardian phone, so that sharing the first's is a real change
        Person typicalSecondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondPerson = new PersonBuilder(typicalSecondPerson)
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB).build();
        model.setPerson(typicalSecondPerson, secondPerson);
        Person editedSecondPerson = new PersonBuilder(secondPerson)
                .withGuardianPhone(firstPerson.getGuardianPhone().getValue())
                .build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianPhone(firstPerson.getGuardianPhone().getValue())
                .build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        String expectedMessage = String.format(
                EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedSecondPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(secondPerson, editedSecondPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        assertEquals(firstPerson, model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()));
    }

    @Test
    public void execute_filteredSecondStudentAbsentContactsGuardianEdit_persistsAndPreservesFields(
            @TempDir Path temporaryFolder) throws Exception {
        Person alpha = new PersonBuilder()
                .withName("Alpha Tan")
                .withPhone("81111111")
                .withEmail("alpha@example.com")
                .withAddress("1 Alpha Street")
                .withGuardianName("Alpha Guardian")
                .withGuardianPhone("91111111")
                .withOutstandingFee("50.00")
                .withTags("math")
                .build();
        Person beta = new PersonBuilder()
                .withName("Beta Lim")
                .withoutContactDetails()
                .withGuardianName("Old Guardian")
                .withGuardianPhone("91234567")
                .withOutstandingFee("120.50")
                .withTags("science", "scholarship")
                .build();

        AddressBook initialAddressBook = new AddressBook();
        initialAddressBook.addPerson(alpha);
        initialAddressBook.addPerson(beta);
        Model localModel = new ModelManager(initialAddressBook, new UserPrefs());

        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        Logic logic = new LogicManager(localModel, new StorageManager(addressBookStorage, userPrefsStorage));

        logic.execute("find Beta");
        assertEquals(List.of(beta), logic.getFilteredPersonList());

        CommandResult result = logic.execute("edit 1 g/New Guardian gp/92345678");

        Person expectedBeta = new PersonBuilder(beta)
                .withGuardianName("New Guardian")
                .withGuardianPhone("92345678")
                .build();
        assertEquals(String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(expectedBeta)),
                result.getFeedbackToUser());
        assertEquals(List.of(alpha, expectedBeta), localModel.getAddressBook().getPersonList());

        ReadOnlyAddressBook reloadedAddressBook = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(List.of(alpha, expectedBeta), reloadedAddressBook.getPersonList());

        Person reloadedAlpha = reloadedAddressBook.getPersonList().get(0);
        Person reloadedBeta = reloadedAddressBook.getPersonList().get(1);
        assertEquals(alpha, reloadedAlpha);
        assertEquals(Optional.empty(), reloadedBeta.getPhone());
        assertEquals(Optional.empty(), reloadedBeta.getEmail());
        assertEquals(Optional.empty(), reloadedBeta.getAddress());
        assertEquals(new GuardianName("New Guardian"), reloadedBeta.getGuardianName());
        assertEquals(new GuardianPhone("92345678"), reloadedBeta.getGuardianPhone());
        assertEquals(Optional.of(new OutstandingFee("120.50")), reloadedBeta.getOutstandingFee());
        assertEquals(beta.getTags(), reloadedBeta.getTags());
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_failure() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_NO_CHANGE);
    }

    @Test
    public void execute_sameValuesAsCurrent_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_NO_CHANGE);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateGuardianPhoneIdentity_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(firstPerson.getName().fullName.toUpperCase())
                .withGuardianPhone(firstPerson.getGuardianPhone().getValue())
                .build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateGuardianPhoneOnlyIdentity_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person sameNameDifferentGuardianPhone = new PersonBuilder(secondPerson)
                .withName(firstPerson.getName().fullName.toUpperCase())
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB)
                .build();
        model.setPerson(secondPerson, sameNameDifferentGuardianPhone);
        model.updateFilteredPersonList(person -> person.equals(sameNameDifferentGuardianPhone));

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGuardianPhone(firstPerson.getGuardianPhone().getValue())
                .build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(editCommand, model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(personInList).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        assertCommandFailure(editCommand, model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    @Test
    public void execute_emptyOptionalContactPrefix_clearsOnlyThatField() throws Exception {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        assertEquals(new PersonBuilder(original).withoutPhone().build(), editFirstPerson(" p/"));
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertEquals(new PersonBuilder(original).withoutEmail().build(), editFirstPerson(" e/"));
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertEquals(new PersonBuilder(original).withoutAddress().build(), editFirstPerson(" a/"));
    }

    @Test
    public void execute_emptyOptionalContactPrefixes_clearsAllAndPreservesOtherFields() throws Exception {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Person edited = editFirstPerson(" p/ e/ a/ n/Alice Lim");

        assertEquals(new PersonBuilder(original).withoutContactDetails().withName("Alice Lim").build(), edited);
        assertEquals(original.getOutstandingFee(), edited.getOutstandingFee());
        assertEquals(original.getTags(), edited.getTags());
    }

    @Test
    public void execute_clearThenSetPhone_success() throws Exception {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        editFirstPerson(" p/");
        Person edited = editFirstPerson(" p/81234567");

        assertEquals(new PersonBuilder(original).withPhone("81234567").build(), edited);
    }

    @Test
    public void parse_emptyGuardianPrefix_failure() {
        assertThrows(ParseException.class, () -> new EditCommandParser().parse("1 g/"));
        assertThrows(ParseException.class, () -> new EditCommandParser().parse("1 gp/"));
    }

    @Test
    public void execute_clearPhone_persistsAfterReload(@TempDir Path temporaryFolder) throws Exception {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        Logic logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        logic.execute("edit 1 p/");

        Person reloaded = addressBookStorage.readAddressBook().orElseThrow().getPersonList().get(0);
        assertEquals(new PersonBuilder(original).withoutPhone().build(), reloaded);
    }

    /**
     * Parses {@code args} as arguments to {@code edit 1}, executes it on {@code model} and returns the first student.
     */
    private Person editFirstPerson(String args) throws Exception {
        new EditCommandParser().parse(INDEX_FIRST_PERSON.getOneBased() + args).execute(model);
        return model.getAddressBook().getPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
    }

    private static AddressBook addressBookOf(Person... persons) {
        AddressBook addressBook = new AddressBook();
        for (Person person : persons) {
            addressBook.addPerson(person);
        }
        return addressBook;
    }
}
