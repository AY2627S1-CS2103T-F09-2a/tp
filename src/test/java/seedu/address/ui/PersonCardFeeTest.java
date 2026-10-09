package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seedu.address.testutil.PersonBuilder;

/**
 * Loads real student-card FXML and verifies fee labels at a narrow list width.
 */
public class PersonCardFeeTest {

    @Test
    public void render_absentTypicalMaximumFees_labelsAreReadable() throws Exception {
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.startup(() -> {
            try {
                PersonCard absent = new PersonCard(new PersonBuilder().build(), 1);
                PersonCard typical = new PersonCard(new PersonBuilder().withOutstandingFee("180").build(), 2);
                PersonCard maximum = new PersonCard(new PersonBuilder().withOutstandingFee("99999.99").build(), 3);
                PersonCard tagged = new PersonCard(new PersonBuilder().withTags("friends", "sec3").build(), 4);
                VBox cards = new VBox(absent.getRoot(), typical.getRoot(), maximum.getRoot(), tagged.getRoot());
                Scene scene = new Scene(cards, 300, 600);
                scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());
                cards.applyCss();
                cards.layout();
                Label absentFee = (Label) absent.getRoot().lookup("#outstandingFee");
                Label typicalFee = (Label) typical.getRoot().lookup("#outstandingFee");
                Label maximumFee = (Label) maximum.getRoot().lookup("#outstandingFee");
                assertFalse(absentFee.isVisible());
                assertFalse(absentFee.isManaged());
                assertEquals("S$180.00 outstanding", typicalFee.getText());
                assertEquals("S$99999.99 outstanding", maximumFee.getText());
                assertTrue(maximumFee.isVisible());
                assertTrue(maximumFee.isManaged());
                assertTrue(maximumFee.getWidth() >= maximumFee.prefWidth(-1));
                FlowPane taggedTags = (FlowPane) tagged.getRoot().lookup("#tags");
                assertEquals(2, taggedTags.getChildren().size());
                assertEquals("friends", ((Label) taggedTags.getChildren().get(0)).getText());
                assertEquals("sec3", ((Label) taggedTags.getChildren().get(1)).getText());
            } catch (Throwable error) {
                failure.set(error);
            } finally {
                finished.countDown();
            }
        });
        assertTrue(finished.await(20, TimeUnit.SECONDS));
        if (failure.get() != null) {
            throw new AssertionError(failure.get());
        }
    }
}
