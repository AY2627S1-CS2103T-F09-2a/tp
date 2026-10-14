package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Shows all stored information for the selected student.
 */
public class PersonDetailsPanel extends UiPart<Region> {
    private static final String FXML = "PersonDetailsPanel.fxml";

    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
    @FXML
    private Label guardianName;
    @FXML
    private Label guardianPhone;
    @FXML
    private Label outstandingFee;
    @FXML
    private Label tags;

    /**
     * Creates an initially empty student details pane.
     */
    public PersonDetailsPanel() {
        super(FXML);
        showPerson(null);
    }

    /**
     * Displays {@code person}, or clears the pane if no student is selected.
     */
    public void showPerson(Person person) {
        if (person == null) {
            name.setText("Select a student with view INDEX");
            phone.setText("");
            email.setText("");
            address.setText("");
            guardianName.setText("");
            guardianPhone.setText("");
            outstandingFee.setText("");
            tags.setText("");
            return;
        }

        name.setText("Name: " + person.getName());
        phone.setText("Phone: " + person.getPhone());
        email.setText("Email: " + person.getEmail());
        address.setText("Address: " + person.getAddress());
        guardianName.setText("Guardian name: " + person.getGuardianName());
        guardianPhone.setText("Guardian phone: " + person.getGuardianPhone());
        outstandingFee.setText("Outstanding fee: " + person.getOutstandingFee()
                .map(Object::toString).orElse("None"));
        String tagNames = person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));
        tags.setText("Tags: " + (tagNames.isEmpty() ? "None" : tagNames));
    }
}
