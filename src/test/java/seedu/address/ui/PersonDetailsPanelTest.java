package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies how the details pane displays a student's fields, such as optional contact fields, the fee and tags.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class PersonDetailsPanelTest {

    @BeforeAll
    public static void initializeToolkit() {
        JavaFxTestToolkit.startOrReuse();
    }

    @Test
    public void showPerson_contactFieldsPresentAndAbsent_displaysValuesOrNotProvided() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonDetailsPanel panel = new PersonDetailsPanel();
            VBox content = (VBox) ((ScrollPane) panel.getRoot()).getContent();
            panel.showPerson(new PersonBuilder().withOutstandingFee("15.5").build());
            assertEquals("Phone: 85355255", ((Label) content.lookup("#phone")).getText());
            assertEquals("Email: amy@gmail.com", ((Label) content.lookup("#email")).getText());
            assertEquals("Address: 123, Jurong West Ave 6, #08-111", ((Label) content.lookup("#address")).getText());
            assertEquals("Outstanding fee: S$15.50", ((Label) content.lookup("#outstandingFee")).getText());

            panel.showPerson(new PersonBuilder().withoutContactDetails().build());
            assertEquals("Phone: Not provided", ((Label) content.lookup("#phone")).getText());
            assertEquals("Email: Not provided", ((Label) content.lookup("#email")).getText());
            assertEquals("Address: Not provided", ((Label) content.lookup("#address")).getText());
            assertEquals("Outstanding fee: None", ((Label) content.lookup("#outstandingFee")).getText());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void showPerson_mixedCaseTags_listsTagsAlphabeticallyIgnoringCase() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonDetailsPanel panel = new PersonDetailsPanel();
            VBox content = (VBox) ((ScrollPane) panel.getRoot()).getContent();
            panel.showPerson(new PersonBuilder().withTags("biology", "Chemistry", "aMath").build());
            assertEquals("Tags: aMath, biology, Chemistry", ((Label) content.lookup("#tags")).getText());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }
}
