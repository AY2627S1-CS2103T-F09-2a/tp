package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests filtering, list restoration and data preservation.
 */
public class FeesCommandTest {

    @Test
    public void execute_multipleFees_filtersWithoutChangingData() throws Exception {
        Model model = new ModelManager();
        Person first = new PersonBuilder().withName("First Student").withOutstandingFee("180").build();
        Person paid = new PersonBuilder().withName("Paid Student").build();
        Person last = new PersonBuilder().withName("Last Student").withOutstandingFee("99999.99").build();
        model.addPerson(first);
        model.addPerson(paid);
        model.addPerson(last);
        assertEquals("2 student(s) with outstanding fees.", new FeesCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(first, last), model.getFilteredPersonList());
        assertEquals(List.of(first, paid, last), model.getAddressBook().getPersonList());
        new ListCommand().execute(model);
        assertEquals(List.of(first, paid, last), model.getFilteredPersonList());
    }

    @Test
    public void execute_noFees_showsEmptyList() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        assertEquals("0 student(s) with outstanding fees.", new FeesCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void shouldSaveAddressBook_feesCommand_returnsFalse() {
        assertFalse(new FeesCommand().shouldSaveAddressBook());
        assertFalse(new FeesCommand(new BigDecimal("100")).shouldSaveAddressBook());
    }

    @Test
    public void execute_minimumAmount_filtersInclusivelyInStoredOrder() throws Exception {
        Model model = new ModelManager();
        Person below = new PersonBuilder().withName("Below Student").withOutstandingFee("99.99").build();
        Person above = new PersonBuilder().withName("Above Student").withOutstandingFee("100.01").build();
        Person paid = new PersonBuilder().withName("Paid Student").build();
        Person equal = new PersonBuilder().withName("Equal Student").withOutstandingFee("100").build();
        List<Person> students = List.of(below, above, paid, equal);
        students.forEach(model::addPerson);

        assertEquals("2 student(s) with outstanding fees.",
                new FeesCommand(new BigDecimal("100")).execute(model).getFeedbackToUser());
        assertEquals(List.of(above, equal), model.getFilteredPersonList());
        assertEquals(students, model.getAddressBook().getPersonList());
        new ListCommand().execute(model);
        assertEquals(students, model.getFilteredPersonList());
    }

    @Test
    public void execute_zeroMinimum_excludesStudentsWithoutFees() {
        Model model = new ModelManager();
        Person paid = new PersonBuilder().withName("Paid Student").build();
        Person owing = new PersonBuilder().withName("Owing Student").withOutstandingFee("0.01").build();
        model.addPerson(paid);
        model.addPerson(owing);

        new FeesCommand(BigDecimal.ZERO).execute(model);
        assertEquals(List.of(owing), model.getFilteredPersonList());
    }

    @Test
    public void execute_maximumMinimum_includesMaximumFee() {
        Model model = new ModelManager();
        Person below = new PersonBuilder().withName("Below Student").withOutstandingFee("99999.98").build();
        Person maximum = new PersonBuilder().withName("Maximum Student").withOutstandingFee("99999.99").build();
        model.addPerson(below);
        model.addPerson(maximum);

        new FeesCommand(new BigDecimal("99999.99")).execute(model);
        assertEquals(List.of(maximum), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatchingFees_showsEmptyList() {
        Model model = new ModelManager();
        FeesCommand command = new FeesCommand(new BigDecimal("100"));
        assertEquals("0 student(s) with outstanding fees.", command.execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());

        model.addPerson(new PersonBuilder().withName("Paid Student").build());
        model.addPerson(new PersonBuilder().withName("Below Student").withOutstandingFee("99.99").build());
        assertEquals("0 student(s) with outstanding fees.", command.execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_existingSearch_replacesPreviousFilter() throws Exception {
        Model model = new ModelManager();
        Person first = new PersonBuilder().withName("First Student").withOutstandingFee("100").build();
        Person last = new PersonBuilder().withName("Last Student").withOutstandingFee("200").build();
        model.addPerson(first);
        model.addPerson(last);
        new FindCommand(new NameContainsKeywordsPredicate(List.of("First"))).execute(model);
        assertEquals(List.of(first), model.getFilteredPersonList());

        new FeesCommand(new BigDecimal("100")).execute(model);
        assertEquals(List.of(first, last), model.getFilteredPersonList());
    }

    @Test
    public void execute_clearDisplayedFee_updatesFilterAndPreservesOtherRecords() throws Exception {
        Model model = new ModelManager();
        Person below = new PersonBuilder().withName("Below Student").withOutstandingFee("50").build();
        Person first = new PersonBuilder().withName("First Student").withOutstandingFee("100").build();
        Person last = new PersonBuilder().withName("Last Student").withOutstandingFee("200").build();
        List.of(below, first, last).forEach(model::addPerson);
        new FeesCommand(new BigDecimal("100")).execute(model);

        new SetOutstandingCommand(Index.fromOneBased(1), null).execute(model);
        assertEquals(List.of(last), model.getFilteredPersonList());
        assertEquals(List.of(below, new PersonBuilder(first).withoutOutstandingFee().build(), last),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void equals_thresholds_comparesNumericAmounts() {
        FeesCommand command = new FeesCommand(new BigDecimal("100"));
        assertEquals(command, command);
        assertEquals(command, new FeesCommand(new BigDecimal("100.00")));
        assertEquals(new FeesCommand(), new FeesCommand(BigDecimal.ZERO));
        assertNotEquals(command, new FeesCommand(new BigDecimal("100.01")));
        assertNotEquals(command, new FeesCommand());
        assertNotEquals(command, null);
        assertNotEquals(command, "100");
        assertThrows(NullPointerException.class, () -> new FeesCommand(null));
    }
}
