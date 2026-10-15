package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GuiSettingsTest {
    @Test
    public void constructor_noArgs_returnsDefaultDimensions() {
        GuiSettings guiSettings = new GuiSettings();
        assertEquals(750, guiSettings.getWindowWidth());
        assertEquals(600, guiSettings.getWindowHeight());
    }

    @Test
    public void toStringMethod() {
        GuiSettings guiSettings = new GuiSettings();
        String expected = GuiSettings.class.getCanonicalName() + "{windowWidth=" + guiSettings.getWindowWidth()
                + ", windowHeight=" + guiSettings.getWindowHeight() + ", windowCoordinates="
                + guiSettings.getWindowCoordinates() + "}";
        assertEquals(expected, guiSettings.toString());
    }
}
