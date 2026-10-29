package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void constructor_nullGuiSettingsInSource_defaultGuiSettingsUsed() {
        // A preferences file storing "guiSettings": null produces a ReadOnlyUserPrefs whose
        // getGuiSettings() is null. Copying it must fall back to the defaults instead of
        // propagating null into ModelManager, which crashed at startup.
        ReadOnlyUserPrefs corruptedPrefs = () -> null;

        UserPrefs repairedPrefs = new UserPrefs(corruptedPrefs);

        assertEquals(new GuiSettings(), repairedPrefs.getGuiSettings());
        assertEquals(new UserPrefs(), repairedPrefs);
    }

}
