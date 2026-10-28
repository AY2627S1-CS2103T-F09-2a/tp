package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies that selection follows a student when an updated record replaces it in the list.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class PersonListPanelSelectionTest {

    @BeforeAll
    public static void initializeToolkit() {
        JavaFxTestToolkit.startOrReuse();
    }

    @Test
    public void retainSelection_feeChanged_selectsUpdatedStudent() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            Person original = new PersonBuilder().withOutstandingFee("15.50").build();
            Person updated = new PersonBuilder(original).withOutstandingFee("20.00").build();
            ObservableList<Person> persons = FXCollections.observableArrayList(original);
            PersonListPanel panel = new PersonListPanel(persons, persons);
            panel.selectPerson(original);

            persons.set(0, updated);
            panel.retainSelection(original);

            assertEquals(updated, panel.getSelectedPerson());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void retainSelection_feeClearedFromFeesList_clearsSelection() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            Person original = new PersonBuilder().withOutstandingFee("15.50").build();
            Person cleared = new PersonBuilder(original).withoutOutstandingFee().build();
            ObservableList<Person> persons = FXCollections.observableArrayList(original);
            FilteredList<Person> fees = new FilteredList<>(persons, person -> person.getOutstandingFee().isPresent());
            PersonListPanel panel = new PersonListPanel(fees, persons);
            panel.selectPerson(original);

            persons.set(0, cleared);
            panel.retainSelection(original);

            assertNull(panel.getSelectedPerson());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void retainSelection_listReplacedBySameSizeList_clearsSelection() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            Person selected = new PersonBuilder().withName("Bernice Yu").build();
            ObservableList<Person> persons = FXCollections.observableArrayList(
                    new PersonBuilder().withName("Alex Yeoh").build(), selected);
            PersonListPanel panel = new PersonListPanel(persons, persons);
            panel.selectPerson(selected);

            persons.setAll(new PersonBuilder().withName("Charlotte Oliveiro").build(),
                    new PersonBuilder().withName("David Li").build());
            panel.retainSelection(selected);

            assertNull(panel.getSelectedPerson());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }
}
