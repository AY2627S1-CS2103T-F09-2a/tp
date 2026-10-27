package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.OutstandingFee;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests replacing and clearing fees in displayed lists and persisted records.
 */
public class SetOutstandingCommandTest {

    @TempDir
    private java.nio.file.Path directory;

    @Test
    public void execute_setReplaceClear_preservesOtherFieldsAndPersists() throws Exception {
        Model model = new ModelManager();
        Person student = new PersonBuilder().build();
        model.addPerson(student);
        new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("150.50")).execute(model);
        Person updated = model.getFilteredPersonList().get(0);
        assertEquals(new OutstandingFee("150.50"), updated.getOutstandingFee().orElseThrow());
        assertEquals(student.getGuardianName(), updated.getGuardianName());
        assertEquals(student.getGuardianPhone(), updated.getGuardianPhone());
        assertEquals(student.getTags(), updated.getTags());
        assertEquals(student.getName(), updated.getName());
        assertEquals(student.getPhone(), updated.getPhone());
        assertEquals(student.getEmail(), updated.getEmail());
        assertEquals(student.getAddress(), updated.getAddress());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("students.json"));
        storage.saveAddressBook(model.getAddressBook());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("1")).execute(model);
        assertEquals(new OutstandingFee("1.00"),
                model.getFilteredPersonList().get(0).getOutstandingFee().orElseThrow());
        CommandResult clearResult = new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        assertEquals("Set outstanding fee for Amy Bee: S$0.00", clearResult.getFeedbackToUser());
        assertEquals(1, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().get(0).getOutstandingFee().isEmpty());
        storage.saveAddressBook(model.getAddressBook());
        assertTrue(storage.readAddressBook().orElseThrow().getPersonList().get(0).getOutstandingFee().isEmpty());
    }

    @Test
    public void execute_preservesStudentAndOtherRecords() throws Exception {
        Model model = new ModelManager();
        Person hidden = new PersonBuilder().withName("Hidden Student").withOutstandingFee("5").build();
        Person student = new PersonBuilder().withGuardianName("Mr Tan").withGuardianPhone("98765432")
                .withTags("class").build();
        model.addPerson(hidden);
        model.addPerson(student);
        model.updateFilteredPersonList(person -> person.getName().equals(student.getName()));
        CommandResult result = new SetOutstandingCommand(Index.fromOneBased(1),
                new OutstandingFee("99999.99")).execute(model);
        Person expected = new PersonBuilder(student).withOutstandingFee("99999.99").build();
        assertEquals(List.of(expected), model.getFilteredPersonList());
        assertEquals(List.of(hidden, expected), model.getAddressBook().getPersonList());
        assertEquals("Set outstanding fee for Amy Bee: S$99999.99", result.getFeedbackToUser());
        new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        assertEquals(List.of(student), model.getFilteredPersonList());
        assertEquals(List.of(hidden, student), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_clearAlreadyPaidStudent_reportsZeroAmount() throws Exception {
        Model model = new ModelManager();
        Person student = new PersonBuilder().build();
        model.addPerson(student);
        CommandResult result = new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        assertEquals("Set outstanding fee for Amy Bee: S$0.00", result.getFeedbackToUser());
        assertEquals(List.of(student), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_emptyList_rejectsWithoutChangingData() {
        Model model = new ModelManager();
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () -> {
            new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        });
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void equals_distinguishesIndexAndFee() {
        SetOutstandingCommand clearFirst = new SetOutstandingCommand(Index.fromOneBased(1), null);
        assertTrue(clearFirst.equals(clearFirst));
        assertTrue(clearFirst.equals(new SetOutstandingCommand(Index.fromOneBased(1), null)));
        assertFalse(clearFirst.equals(new SetOutstandingCommand(Index.fromOneBased(2), null)));
        assertFalse(clearFirst.equals(new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("1.00"))));
        assertFalse(clearFirst.equals(null));
        assertFalse(clearFirst.equals("not a command"));
    }

    @Test
    public void equals_distinguishesReplaceFromAdd() {
        SetOutstandingCommand replace = new SetOutstandingCommand(Index.fromOneBased(1),
                new OutstandingFee("50"));
        SetOutstandingCommand add = SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(1),
                new OutstandingFee("50"));

        assertTrue(add.equals(SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(1),
                new OutstandingFee("50"))));
        assertFalse(add.equals(replace));
        assertFalse(replace.equals(add));
    }

    @Test
    public void execute_addToExistingFee_increasesTotalAndPreservesOtherFields() throws Exception {
        Model model = new ModelManager();
        Person student = new PersonBuilder().withOutstandingFee("120").build();
        model.addPerson(student);

        CommandResult result = SetOutstandingCommand
                .addToOutstandingFee(Index.fromOneBased(1), new OutstandingFee("50"))
                .execute(model);

        Person updated = model.getFilteredPersonList().get(0);
        assertEquals(new OutstandingFee("170.00"), updated.getOutstandingFee().orElseThrow());
        assertEquals("Added S$50.00 to the outstanding fee for Amy Bee: S$120.00 -> S$170.00",
                result.getFeedbackToUser());
        assertEquals(student.getGuardianName(), updated.getGuardianName());
        assertEquals(student.getGuardianPhone(), updated.getGuardianPhone());
        assertEquals(student.getTags(), updated.getTags());
        assertEquals(student.getName(), updated.getName());
    }

    @Test
    public void execute_addToStudentWithoutFee_setsTheAddedAmount() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());

        CommandResult result = SetOutstandingCommand
                .addToOutstandingFee(Index.fromOneBased(1), new OutstandingFee("50"))
                .execute(model);

        assertEquals(new OutstandingFee("50.00"),
                model.getFilteredPersonList().get(0).getOutstandingFee().orElseThrow());
        assertEquals("Added S$50.00 to the outstanding fee for Amy Bee: S$0.00 -> S$50.00",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_addBeyondMaximum_rejectsWithoutChangingData() {
        Model model = new ModelManager();
        Person student = new PersonBuilder().withOutstandingFee("99999.00").build();
        model.addPerson(student);

        CommandException exception = org.junit.jupiter.api.Assertions.assertThrows(CommandException.class, ()
                -> SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(1), new OutstandingFee("1"))
                        .execute(model));

        assertEquals(String.format(SetOutstandingCommand.MESSAGE_TOTAL_TOO_LARGE, "1.00", "100000.00"),
                exception.getMessage());
        assertEquals(List.of(student), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_addToExactlyMaximum_succeeds() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withOutstandingFee("99998.99").build());

        SetOutstandingCommand.addToOutstandingFee(Index.fromOneBased(1), new OutstandingFee("1"))
                .execute(model);

        assertEquals(new OutstandingFee("99999.99"),
                model.getFilteredPersonList().get(0).getOutstandingFee().orElseThrow());
    }

    @Test
    public void execute_feesListClear_removesStudentAndReindexes() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Paid Student").build());
        model.addPerson(new PersonBuilder().withName("First Owing").withOutstandingFee("2").build());
        Person remaining = new PersonBuilder().withName("Last Owing").withOutstandingFee("3").build();
        model.addPerson(remaining);
        new FeesCommand().execute(model);
        new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        assertEquals(List.of(remaining), model.getFilteredPersonList());
        assertEquals(3, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_findListRetainsPredicateAndInvalidIndexPreservesState() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        model.addPerson(new PersonBuilder().withName("Other Student").build());
        model.updateFilteredPersonList(person -> person.getName().fullName.equals("Amy Bee"));
        new SetOutstandingCommand(Index.fromOneBased(1), new OutstandingFee("0.01")).execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        Person before = model.getFilteredPersonList().get(0);
        assertThrows(CommandException.class,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () -> {
                    new SetOutstandingCommand(Index.fromOneBased(2), null).execute(model);
                });
        assertEquals(before, model.getFilteredPersonList().get(0));
    }
}
