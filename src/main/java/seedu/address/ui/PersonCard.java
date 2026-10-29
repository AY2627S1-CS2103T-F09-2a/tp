package seedu.address.ui;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.logic.Messages;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label guardian;
    @FXML
    private Label outstandingFee;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        setOptionalText(phone, person.getPhone().map(p -> p.value));
        setOptionalText(address, person.getAddress().map(a -> a.value));
        setOptionalText(email, person.getEmail().map(e -> e.value));
        guardian.setText("Guardian: " + person.getGuardianName() + " (" + person.getGuardianPhone() + ")");
        person.getOutstandingFee().ifPresent(fee -> {
            outstandingFee.setText("S$" + fee + " outstanding");
            outstandingFee.setVisible(true);
            outstandingFee.setManaged(true);
        });
        Messages.sortTagNames(person.getTags()).forEach(tagName -> tags.getChildren().add(new Label(tagName)));
    }

    /**
     * Shows {@code text} in {@code label}, or hides the label when the student did not provide that detail.
     */
    private static void setOptionalText(Label label, Optional<String> text) {
        label.setText(text.orElse(""));
        label.setVisible(text.isPresent());
        label.setManaged(text.isPresent());
    }
}
