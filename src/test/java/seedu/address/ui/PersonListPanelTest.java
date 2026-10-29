package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.logic.commands.FeesCommand.PREDICATE_HAS_OUTSTANDING_FEE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.layout.Region;
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
        JavaFxTestToolkit.startOrReuse();
    }

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        panel = runOnFxThread(() -> new PersonListPanel(model.getFilteredPersonList(),
                model.getAddressBook().getPersonList()));
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

    @Test
    public void retainSelection_searchShowsOtherStudentAtSameIndex_clearsSelection() throws Exception {
        showOnly(ALICE);
        assertNull(selectThenUpdate(ALICE, () -> showOnly(BENSON)));
    }

    @Test
    public void retainSelection_searchShowsSameNumberOfOtherStudents_clearsSelection() throws Exception {
        showOnly(ALICE, BENSON);
        assertNull(selectThenUpdate(BENSON, () -> showOnly(CARL, DANIEL)));
    }

    @Test
    public void retainSelection_searchMovesSelectedStudentInSameSizedList_keepsStudent() throws Exception {
        showOnly(ALICE, BENSON);
        assertEquals(BENSON, selectThenUpdate(BENSON, () -> showOnly(BENSON, CARL)));
    }

    @Test
    public void retainSelection_selectedStudentRenamedOutOfSearch_selectsEditedStudent() throws Exception {
        showOnly(ALICE, BENSON);
        Person renamedBenson = new PersonBuilder(BENSON).withName("Beatrice Yu").build();
        assertEquals(renamedBenson, selectThenUpdate(BENSON, () -> {
            model.setPerson(BENSON, renamedBenson);
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }));
    }

    @Test
    public void retainSelection_searchShowsFewerOtherStudents_clearsSelection() throws Exception {
        showOnly(ALICE, BENSON);
        assertNull(selectThenUpdate(ALICE, () -> showOnly(CARL)));
    }

    @Test
    public void retainSelection_searchShowsMoreOtherStudents_clearsSelection() throws Exception {
        showOnly(ALICE);
        assertNull(selectThenUpdate(ALICE, () -> showOnly(BENSON, CARL)));
    }

    @Test
    public void retainSelectionAfterFailure_selectedStudentDeleted_clearsSelection() throws Exception {
        assertNull(selectThenFailedUpdate(ALICE, () -> model.deletePerson(ALICE)));
    }

    @Test
    public void retainSelectionAfterFailure_selectedStudentEdited_selectsEditedStudent() throws Exception {
        Person editedAlice = new PersonBuilder(ALICE).withPhone("91234567").build();
        assertEquals(editedAlice, selectThenFailedUpdate(ALICE, () -> model.setPerson(ALICE, editedAlice)));
    }

    @Test
    public void retainSelectionAfterFailure_feeClearedFromFeesList_clearsSelection() throws Exception {
        Person aliceWithFee = new PersonBuilder(ALICE).withOutstandingFee("50").build();
        model.setPerson(ALICE, aliceWithFee);
        model.updateFilteredPersonList(PREDICATE_HAS_OUTSTANDING_FEE);
        Person aliceWithoutFee = new PersonBuilder(ALICE).withoutOutstandingFee().build();
        assertNull(selectThenFailedUpdate(aliceWithFee, () -> model.setPerson(aliceWithFee, aliceWithoutFee)));
    }

    @Test
    public void retainSelectionAfterFailure_nothingChanged_keepsStudent() throws Exception {
        assertEquals(BENSON, selectThenFailedUpdate(BENSON, () -> {}));
    }

    @Test
    public void retainSelectionAfterFailure_nothingChanged_doesNotScroll() throws Exception {
        List<Integer> scrollTargets = runOnFxThread(() -> {
            Region root = panel.getRoot();
            new Scene(root); // The list only scrolls once it has a skin, which needs a scene.
            root.applyCss();
            root.layout();
            panel.selectPerson(BENSON);
            List<Integer> targets = new ArrayList<>();
            root.addEventHandler(ScrollToEvent.scrollToTopIndex(), event -> targets.add(event.getScrollTarget()));
            panel.retainSelectionAfterFailure(BENSON);
            panel.selectPerson(CARL); // Shows that scrolling is observed.
            return targets;
        });
        assertEquals(List.of(2), scrollTargets);
    }

    /**
     * Filters the displayed list to {@code students}, as a search that matches only them does.
     */
    private void showOnly(Person... students) {
        model.updateFilteredPersonList(List.of(students)::contains);
    }

    /**
     * Returns the student selected after {@code update}, handled as {@code MainWindow} does after a command succeeds.
     */
    private Person selectThenUpdate(Person student, Runnable update) throws Exception {
        return selectUpdateThenRestore(student, update, person -> panel.retainSelection(person));
    }

    /**
     * Returns the student selected after {@code update}, handled as {@code MainWindow} does after a command fails.
     */
    private Person selectThenFailedUpdate(Person student, Runnable update) throws Exception {
        return selectUpdateThenRestore(student, update, person -> panel.retainSelectionAfterFailure(person));
    }

    /**
     * Selects {@code student}, applies {@code update} to the model, passes the previously selected student to
     * {@code restore}, and returns the student selected afterwards.
     */
    private Person selectUpdateThenRestore(Person student, Runnable update, Consumer<Person> restore)
            throws Exception {
        return runOnFxThread(() -> {
            panel.selectPerson(student);
            Person previouslySelected = panel.getSelectedPerson();
            update.run();
            restore.accept(previouslySelected);
            return panel.getSelectedPerson();
        });
    }

    private static <T> T runOnFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
