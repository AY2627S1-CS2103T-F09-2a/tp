package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

public class ApplicationPathsTest {

    @TempDir
    private Path directory;

    @Test
    public void homeFolderFor_jarLaunch_ignoresWorkingFolder() throws Exception {
        Path home = Files.createDirectory(directory.resolve("jar home"));
        Path jar = Files.createFile(home.resolve("poco.jar"));
        Path workingFolder = Files.createDirectory(directory.resolve("different folder"));
        assertEquals(home.toRealPath(), ApplicationPaths.homeFolderFor(jar, workingFolder));
    }

    @Test
    public void homeFolderFor_developmentRun_usesWorkingFolder() throws Exception {
        Path classes = Files.createDirectory(directory.resolve("classes"));
        assertEquals(directory.toRealPath(), ApplicationPaths.homeFolderFor(classes, directory));
    }

    @Test
    public void resolveWithin_nestedFile_staysInsideHome() {
        assertEquals(directory.resolve("data/addressbook.json"),
                ApplicationPaths.resolveWithin(directory, Path.of("data", "addressbook.json")));
    }

    @Test
    public void resolveWithin_parentTraversal_rejects() {
        assertThrows(IllegalArgumentException.class, () ->
                ApplicationPaths.resolveWithin(directory, Path.of("..", "outside.json")));
    }

    @Test
    public void resolveWithin_absoluteOutsidePath_rejects() {
        assertThrows(IllegalArgumentException.class, () ->
                ApplicationPaths.resolveWithin(directory, directory.getParent().resolve("outside.json")));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void resolveWithin_symbolicLinkOutsideHome_rejects() throws Exception {
        Path home = Files.createDirectory(directory.resolve("home"));
        Path outside = Files.createDirectory(directory.resolve("outside"));
        Files.createSymbolicLink(home.resolve("data"), outside);
        assertThrows(IllegalArgumentException.class, () ->
                ApplicationPaths.resolveWithin(home, Path.of("data", "addressbook.json")));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void resolveWithin_danglingSymbolicLinkOutsideHome_rejects() throws Exception {
        Path home = Files.createDirectory(directory.resolve("home"));
        Path missingOutside = directory.resolve("outside").resolve("missing");
        Files.createSymbolicLink(home.resolve("data"), missingOutside);
        assertThrows(IllegalArgumentException.class, () ->
                ApplicationPaths.resolveWithin(home, Path.of("data", "addressbook.json")));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void resolveWithin_danglingSymbolicLinkInsideHome_staysInsideHome() throws Exception {
        Path home = Files.createDirectory(directory.resolve("home"));
        Files.createSymbolicLink(home.resolve("data"), Path.of("storage"));
        assertEquals(home.resolve("data/addressbook.json"),
                ApplicationPaths.resolveWithin(home, Path.of("data", "addressbook.json")));
    }
}
