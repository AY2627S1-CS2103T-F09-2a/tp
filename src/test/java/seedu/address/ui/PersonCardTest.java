package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies how the real card component shows a student's tags.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class PersonCardTest {

    @BeforeAll
    public static void initializeToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
    }

    @Test
    public void create_mixedCaseTags_showsTagsAlphabeticallyIgnoringCase() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withTags("biology", "Chemistry", "aMath").build(), 1);
            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            List<String> shownTags = tags.getChildren().stream().map(tag -> ((Label) tag).getText()).toList();
            assertEquals(List.of("aMath", "biology", "Chemistry"), shownTags);
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }
}
