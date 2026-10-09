package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import seedu.address.model.person.Person;
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
                PersonCard sparse = new PersonCard(personWithBlankContactText(), 5);
                VBox cards = new VBox(absent.getRoot(), typical.getRoot(), maximum.getRoot(), tagged.getRoot(),
                        sparse.getRoot());
                cards.getStyleClass().add("list-cell");
                cards.setStyle("-fx-background-color: #3c3e3f;");
                Scene scene = new Scene(cards, 340, 600);
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
                assertEquals(Color.web("#ffd166"), maximumFee.getTextFill());
                assertTrue(maximumFee.getWidth() >= maximumFee.prefWidth(-1));
                FlowPane taggedTags = (FlowPane) tagged.getRoot().lookup("#tags");
                assertEquals(2, taggedTags.getChildren().size());
                assertEquals("friends", ((Label) taggedTags.getChildren().get(0)).getText());
                assertEquals("sec3", ((Label) taggedTags.getChildren().get(1)).getText());
                Label sparsePhone = (Label) sparse.getRoot().lookup("#phone");
                Label sparseAddress = (Label) sparse.getRoot().lookup("#address");
                Label sparseEmail = (Label) sparse.getRoot().lookup("#email");
                assertFalse(sparsePhone.isVisible());
                assertFalse(sparsePhone.isManaged());
                assertFalse(sparseAddress.isVisible());
                assertFalse(sparseAddress.isManaged());
                assertFalse(sparseEmail.isVisible());
                assertFalse(sparseEmail.isManaged());
                WritableImage snapshot = cards.snapshot(null, null);
                BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
                    }
                }
                Path output = Path.of("build", "reports", "ui-fees.png");
                Files.createDirectories(output.getParent());
                ImageIO.write(image, "png", output.toFile());
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

    /**
     * Validation forbids blank phone, address, and email values, so the card's
     * hide-empty-field branches are only reachable with values blanked in tests.
     */
    private static Person personWithBlankContactText() throws ReflectiveOperationException {
        Person person = new PersonBuilder().build();
        for (Object valueHolder : new Object[] {person.getPhone(), person.getAddress(), person.getEmail()}) {
            Field value = valueHolder.getClass().getField("value");
            value.setAccessible(true);
            value.set(valueHolder, "");
        }
        return person;
    }
}
