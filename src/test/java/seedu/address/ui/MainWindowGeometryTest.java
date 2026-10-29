package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Checks that saved window geometry can be recovered on an actual JavaFX Stage. */
@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")
public class MainWindowGeometryTest {

    @TempDir
    public Path testFolder;

    @BeforeAll
    public static void initializeToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Another UI test may already have initialized JavaFX.
        }
        Platform.setImplicitExit(false);
    }

    @Test
    public void show_invalidDimensions_fallsBackToDefaults() throws Exception {
        for (double invalid : new double[]{-10, 0, Double.NaN, Double.POSITIVE_INFINITY}) {
            FutureTask<Void> check = new FutureTask<>(() -> {
                Stage stage = createWindow(new GuiSettings(invalid, invalid, 100, 100));
                try {
                    assertEquals(750, stage.getWidth());
                    assertEquals(600, stage.getHeight());
                } finally {
                    stage.close();
                }
                return null;
            });
            Platform.runLater(check);
            check.get(10, TimeUnit.SECONDS);
        }
    }

    @Test
    public void show_offscreenPosition_recoversOnVisibleScreen() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            Stage stage = createWindow(new GuiSettings(750, 600, Integer.MAX_VALUE, Integer.MAX_VALUE));
            try {
                assertTrue(Screen.getScreens().stream().map(Screen::getVisualBounds)
                        .anyMatch(bounds -> bounds.contains(stage.getX(), stage.getY())));
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void show_visibleSavedPosition_preservesCoordinates() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            int x = (int) screen.getMinX() + 50;
            int y = (int) screen.getMinY() + 50;
            Stage stage = createWindow(new GuiSettings(750, 600, x, y));
            try {
                assertEquals(x, stage.getX());
                assertEquals(y, stage.getY());
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    private Stage createWindow(GuiSettings settings) {
        UserPrefs prefs = new UserPrefs();
        prefs.setGuiSettings(settings);
        Path data = testFolder.resolve("addressbook.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));
        Stage stage = new Stage();
        new MainWindow(stage, new LogicManager(new ModelManager(new AddressBook(), prefs), storage), data).show();
        return stage;
    }
}
