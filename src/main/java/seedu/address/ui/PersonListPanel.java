package seedu.address.ui;

import java.util.HashMap;
import java.util.Map;
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

    /** Maps each student replaced in the list since the last {@link #retainSelection} to its replacement. */
    private final Map<Person, Person> replacements = new HashMap<>();

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        personList.addListener(this::recordReplacements);
    }

    /**
     * Selects and scrolls to a student in the currently displayed list.
     */
    public void selectPerson(Person person) {
        personListView.getSelectionModel().select(person);
        personListView.scrollTo(person);
    }

    public Person getSelectedPerson() {
        return personListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Restores the same student after a list update, or clears a reused list-index selection.
     * A student edited during the update is restored as their edited version.
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
     * Records the students replaced in place by {@code change}, such as by an edit that keeps them displayed.
     */
    private void recordReplacements(ListChangeListener.Change<? extends Person> change) {
        while (change.next()) {
            if (!change.wasReplaced() || change.getRemovedSize() != change.getAddedSize()) {
                continue;
            }
            for (int i = 0; i < change.getRemovedSize(); i++) {
                replacements.put(change.getRemoved().get(i), change.getAddedSubList().get(i));
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
