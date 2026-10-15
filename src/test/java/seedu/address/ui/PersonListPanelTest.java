package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.logic.commands.FeesCommand.PREDICATE_HAS_OUTSTANDING_FEE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies that the selected student is kept across list updates, as {@code MainWindow} does after each command.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class PersonListPanelTest {

    private Model model;
    private PersonListPanel panel;

    @BeforeAll
    public static void initializeToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        panel = runOnFxThread(() -> new PersonListPanel(model.getFilteredPersonList()));
    }

    @Test
    public void retainSelection_selectedStudentUnchanged_keepsStudent() throws Exception {
        assertEquals(ALICE, selectThenUpdate(ALICE, () -> model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)));
    }

    @Test
    public void retainSelection_selectedStudentEdited_selectsEditedStudent() throws Exception {
        Person editedAlice = new PersonBuilder(ALICE).withPhone("91234567").build();
        assertEquals(editedAlice, selectThenUpdate(ALICE, () -> {
            model.setPerson(ALICE, editedAlice);
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }));
    }

    @Test
    public void retainSelection_selectedStudentNameEdited_selectsEditedStudent() throws Exception {
        Person editedAlice = new PersonBuilder(ALICE).withName("Alicia Pauline").build();
        assertEquals(editedAlice, selectThenUpdate(ALICE, () -> model.setPerson(ALICE, editedAlice)));
    }

    @Test
    public void retainSelection_otherStudentEdited_keepsSelectedStudent() throws Exception {
        Person editedBenson = new PersonBuilder(BENSON).withPhone("91234567").build();
        assertEquals(ALICE, selectThenUpdate(ALICE, () -> model.setPerson(BENSON, editedBenson)));
    }

    @Test
    public void retainSelection_editedStudentFilteredOut_clearsSelection() throws Exception {
        Person aliceWithFee = new PersonBuilder(ALICE).withOutstandingFee("50").build();
        model.setPerson(ALICE, aliceWithFee);
        model.updateFilteredPersonList(PREDICATE_HAS_OUTSTANDING_FEE);
        Person aliceWithoutFee = new PersonBuilder(ALICE).withoutOutstandingFee().build();
        assertNull(selectThenUpdate(aliceWithFee, () -> model.setPerson(aliceWithFee, aliceWithoutFee)));
    }

    @Test
    public void retainSelection_selectedStudentDeleted_clearsSelection() throws Exception {
        assertNull(selectThenUpdate(ALICE, () -> model.deletePerson(ALICE)));
    }

    /**
     * Selects {@code student}, applies {@code update} to the model, retains the selection as
     * {@code MainWindow} does after a command, and returns the student selected afterwards.
     */
    private Person selectThenUpdate(Person student, Runnable update) throws Exception {
        return runOnFxThread(() -> {
            panel.selectPerson(student);
            Person previouslySelected = panel.getSelectedPerson();
            update.run();
            panel.retainSelection(previouslySelected);
            return panel.getSelectedPerson();
        });
    }

    private static <T> T runOnFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
