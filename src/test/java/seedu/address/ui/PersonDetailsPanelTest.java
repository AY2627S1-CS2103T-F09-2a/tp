package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
    public void showPerson_maximumContactFields_scrollToBottomRevealsFinalFields() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonDetailsPanel panel = new PersonDetailsPanel();
            ScrollPane scrollPane = (ScrollPane) panel.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(scrollPane, 340, 260));
            try {
                panel.showPerson(new PersonBuilder().withName("W".repeat(70))
                        .withGuardianName("W".repeat(70))
                        .withAddress("W".repeat(150))
                        .withEmail("a".repeat(62) + "@" + "b".repeat(62) + "."
                                + "c".repeat(62) + "." + "d".repeat(61) + ".com")
                        .withOutstandingFee("99999.99").withTags("Math").build());
                stage.show();
                scrollPane.applyCss();
                scrollPane.layout();
                assertTrue(scrollPane.getContent().getBoundsInLocal().getHeight()
                        > scrollPane.getViewportBounds().getHeight(), "Fixture must require scrolling");

                scrollPane.setVvalue(scrollPane.getVmax());
                scrollPane.layout();
                Label lastField = (Label) scrollPane.getContent().lookup("#tags");
                Bounds viewport = scrollPane.localToScene(scrollPane.getBoundsInLocal());
                Bounds lastFieldOnScreen = lastField.localToScene(lastField.getBoundsInLocal());
                assertEquals("Tags: Math", lastField.getText());
                assertTrue(lastFieldOnScreen.getMaxY() <= viewport.getMaxY(),
                        "The final details field must be visible after scrolling to the bottom");
            } finally {
                stage.close();
            }
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
