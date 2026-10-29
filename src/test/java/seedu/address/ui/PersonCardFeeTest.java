package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies fee labels and displayed indexes in the real card component.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class PersonCardFeeTest {

    @BeforeAll
    public static void initializeToolkit() {
        JavaFxTestToolkit.startOrReuse();
    }

    @Test
    public void create_feePresent_displaysExactAmountAndIndex() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withOutstandingFee("15.5").build(), 1);
            Label fee = (Label) card.getRoot().lookup("#outstandingFee");
            assertEquals("S$15.50 outstanding", fee.getText());
            assertTrue(fee.isVisible());
            assertTrue(fee.isManaged());
            assertEquals("1. ", ((Label) card.getRoot().lookup("#id")).getText());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void create_feeAbsent_hidesFeeLabel() throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().build(), 1);
            Label fee = (Label) card.getRoot().lookup("#outstandingFee");
            assertFalse(fee.isVisible());
            assertFalse(fee.isManaged());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void create_studentWithoutOptionalContactDetails_displaysGuardianAndHidesOptionalLabels()
            throws Exception {
        FutureTask<Void> assertion = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(new PersonBuilder()
                    .withoutContactDetails()
                    .withGuardianName("Mei Tan")
                    .withGuardianPhone("92345678")
                    .build(), 1);
            Label guardian = (Label) card.getRoot().lookup("#guardian");
            assertEquals("Guardian: Mei Tan (92345678)", guardian.getText());
            assertFalse(((Label) card.getRoot().lookup("#phone")).isVisible());
            assertFalse(((Label) card.getRoot().lookup("#address")).isVisible());
            assertFalse(((Label) card.getRoot().lookup("#email")).isVisible());
            return null;
        });
        Platform.runLater(assertion);
        assertion.get(10, TimeUnit.SECONDS);
    }
}
