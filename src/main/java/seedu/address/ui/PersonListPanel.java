package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    /** Maps each student replaced in the full list since the selection was last restored to their replacement. */
    private final Map<Person, Person> replacements = new HashMap<>();

    /**
     * Creates a {@code PersonListPanel} that displays {@code displayedPersons}, a filtered view of
     * {@code allPersons}.
     */
    public PersonListPanel(ObservableList<Person> displayedPersons, ObservableList<Person> allPersons) {
        super(FXML);
        requireAllNonNull(displayedPersons, allPersons);
        personListView.setItems(displayedPersons);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        // A search replaces the whole displayed list, so only changes to the full list identify edited students.
        allPersons.addListener(this::recordReplacements);
    }

    /**
     * Selects and scrolls to a student in the currently displayed list.
     */
    public void selectPerson(Person person) {
        personListView.getSelectionModel().select(person);
        personListView.scrollTo(person);
    }

    /**
     * Returns the selected student's current record, or null if no student is selected.
     * The record is read from the list by index: when an edit replaces a student with a record that is
     * {@code equals} to the old one, such as a change in letter case only, the selection model keeps the old record.
     */
    public Person getSelectedPerson() {
        int index = personListView.getSelectionModel().getSelectedIndex();
        return index < 0 ? null : personListView.getItems().get(index);
    }

    /**
     * Selects {@code person} again after a list update if they are still displayed, or clears the selection.
     * A student whose record was replaced during the update, such as by an edit, is selected as the new record.
     */
    public void retainSelection(Person person) {
        Person current = person == null ? null : replacements.getOrDefault(person, person);
        replacements.clear();
        if (current != null && personListView.getItems().contains(current)) {
            selectPerson(current);
        } else {
            personListView.getSelectionModel().clearSelection();
        }
    }

    /**
     * Corrects the selection after a failed command, which may still have changed the list, such as when saving
     * fails. If the selection moved off {@code person}, restores it as {@link #retainSelection} does. Otherwise,
     * leaves the selection and the scroll position unchanged.
     */
    public void retainSelectionAfterFailure(Person person) {
        if (Objects.equals(getSelectedPerson(), person)) {
            replacements.clear();
            return;
        }
        retainSelection(person);
    }

    /**
     * Records the student replaced in place by {@code change} to the full list, such as by an edit.
     * Only one-for-one replacements are recorded, as an edit replaces one student. When several students
     * are replaced at once, such as by a same-size {@code setAll}, they cannot be paired reliably.
     */
    private void recordReplacements(ListChangeListener.Change<? extends Person> change) {
        while (change.next()) {
            if (change.getRemovedSize() == 1 && change.getAddedSize() == 1) {
                replacements.put(change.getRemoved().get(0), change.getAddedSubList().get(0));
            }
        }
    }

    public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return personListView.getSelectionModel().selectedItemProperty();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
