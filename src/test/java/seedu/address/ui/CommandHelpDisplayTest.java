package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Checks command help in the actual minimum-size application window.
 */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class CommandHelpDisplayTest {
    @TempDir
    public Path directory;

    @BeforeAll
    public static void startToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test has already started the toolkit.
        }
        Platform.setImplicitExit(false);
    }

    @Test
    public void helpAdd_minimumWindow_wrapsAndScrollsWithoutExecutingAdd() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            ModelManager model = new ModelManager();
            StorageManager storage = new StorageManager(
                    new JsonAddressBookStorage(directory.resolve("students.json")),
                    new JsonUserPrefsStorage(directory.resolve("prefs.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(model, storage), directory);
            try {
                window.fillInnerParts();
                window.show();
                stage.setWidth(750);
                stage.setHeight(450);
                TextField command = (TextField) stage.getScene().lookup("#commandTextField");
                command.setText("help add");
                command.fireEvent(new ActionEvent());
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                TextArea result = (TextArea) stage.getScene().lookup("#resultDisplay");
                assertTrue(result.isWrapText());
                assertTrue(result.getText().contains("g/GUARDIAN_NAME gp/GUARDIAN_PHONE"));
                assertTrue(result.getText().contains("[f/AMOUNT]"));
                assertTrue(result.getText().contains("99999.99"));
                assertEquals(0, model.getAddressBook().getPersonList().size());
                boolean verticalScrollAvailable = false;
                for (Node node : result.lookupAll(".scroll-bar")) {
                    ScrollBar bar = (ScrollBar) node;
                    if (bar.getOrientation() == Orientation.HORIZONTAL) {
                        assertFalse(bar.isVisible());
                    } else {
                        verticalScrollAvailable = bar.isVisible() && bar.getMax() > bar.getMin();
                    }
                }
                assertTrue(verticalScrollAvailable);
                result.setScrollTop(Double.MAX_VALUE);
                assertTrue(result.getScrollTop() > 0);
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }
}
