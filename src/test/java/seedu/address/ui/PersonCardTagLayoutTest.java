package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies that long tags stay inside a student card, with the styles that the app uses.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class PersonCardTagLayoutTest {

    /**
     * The narrowest a card's content can be: 263 pixels with the divider dragged fully left, whatever the window
     * width, less the 2-pixel border on each side of a selected card.
     */
    private static final double NARROWEST_CARD_WIDTH = 259;
    /** The width of a card in a 1280 by 720 window, with the divider in its default position. */
    private static final double WIDE_CARD_WIDTH = 578;
    private static final double MAX_TAG_WIDTH = 230;
    /** The card's bottom padding, in pixels, which is the only space expected below the last line. */
    private static final double CARD_BOTTOM_PADDING = 5;
    private static final String LONG_TAG = "A".repeat(300);
    /**
     * A tag wider than the 130 pixels that the tag row has at the card's preferred width. Together with
     * {@link #SHORT_TAG} and the gap between them, it is narrower than 230 pixels, so both fit on one row.
     */
    private static final String MEDIUM_TAG = "MediumLengthTagAbout25Chr";
    private static final String SHORT_TAG = "math";

    @BeforeAll
    public static void initializeToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
    }

    @Test
    public void create_longTagOnNarrowestCard_cutsOffTagInsideCard() throws Exception {
        assertLongTagContained(NARROWEST_CARD_WIDTH);
    }

    @Test
    public void create_longTagOnWideCard_cutsOffTagInsideCard() throws Exception {
        assertLongTagContained(WIDE_CARD_WIDTH);
    }

    @Test
    public void create_mediumTagOnNarrowestCard_leavesNoBlankRow() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            // The fee adds a line, so the card is taller than its minimum height, which would centre its lines
            PersonCard card = new PersonCard(new PersonBuilder().withTags(MEDIUM_TAG, SHORT_TAG)
                    .withOutstandingFee("120").build(), 1);
            layOut(card.getRoot(), NARROWEST_CARD_WIDTH);
            Label guardian = (Label) card.getRoot().lookup("#guardian");

            double spaceBelowLastLine = bottomEdge(card.getRoot()) - bottomEdge(guardian);
            assertTrue(spaceBelowLastLine <= CARD_BOTTOM_PADDING + 1,
                    "card has " + spaceBelowLastLine + " pixels of space below its last line");
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }

    /**
     * Asserts that, on a card {@code cardWidth} pixels wide, the long tag is at most {@value #MAX_TAG_WIDTH} pixels
     * wide and cut off with an ellipsis, every tag and the guardian line stay inside the card, and the short tag is
     * shown in full.
     */
    private static void assertLongTagContained(double cardWidth) throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withTags(LONG_TAG, SHORT_TAG).build(), 1);
            layOut(card.getRoot(), cardWidth);
            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            Label longTag = findTag(tags, LONG_TAG);
            Label shortTag = findTag(tags, SHORT_TAG);
            Label guardian = (Label) card.getRoot().lookup("#guardian");

            assertTrue(rightEdge(tags) <= rightEdge(card.getRoot()), "tag row is " + tags.getWidth() + " pixels wide");
            for (Node tag : tags.getChildren()) {
                assertTrue(rightEdge(tag) <= rightEdge(tags), "a tag ends past the tag row");
            }
            assertTrue(rightEdge(guardian) <= rightEdge(card.getRoot()), "guardian line ends past the card");
            assertTrue(longTag.getWidth() <= MAX_TAG_WIDTH, "long tag is " + longTag.getWidth() + " pixels wide");
            assertTrue(shownText(longTag).endsWith(longTag.getEllipsisString()), "long tag is not cut off");
            assertEquals(SHORT_TAG, shownText(shortTag));
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }

    /**
     * Lays out {@code cardRoot} at {@code width} pixels wide and at its preferred height for that width, as the student
     * list does, with the app's styles.
     */
    private static void layOut(Region cardRoot, double width) {
        VBox container = new VBox(cardRoot);
        Scene scene = new Scene(container);
        scene.getStylesheets().add(PersonCardTagLayoutTest.class.getResource("/view/DarkTheme.css").toExternalForm());
        container.applyCss();
        container.resize(width, container.prefHeight(width));
        container.layout();
    }

    private static Label findTag(FlowPane tags, String tagName) {
        return tags.getChildren().stream()
                .map(tag -> (Label) tag)
                .filter(tag -> tag.getText().equals(tagName))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Returns the text that {@code label} actually shows, which ends with its ellipsis string if it is cut off.
     * JavaFX 17 has no public way to ask whether a label's text is cut off, so this reads the label's text node.
     */
    private static String shownText(Label label) {
        return ((Text) label.lookup(".text")).getText();
    }

    private static double rightEdge(Node node) {
        return node.localToScene(node.getLayoutBounds()).getMaxX();
    }

    private static double bottomEdge(Node node) {
        return node.localToScene(node.getLayoutBounds()).getMaxY();
    }
}
