package seedu.address.commons.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * Resolves runtime files inside the folder containing the executable JAR.
 */
public final class ApplicationPaths {

    private static final Path HOME_FOLDER = locateHomeFolder();

    private ApplicationPaths() {}

    /**
     * Returns a runtime path inside the application home folder.
     * Existing symbolic links must also resolve inside that folder.
     */
    public static Path resolve(String first, String... more) {
        return resolveWithin(HOME_FOLDER, Path.of(first, more));
    }

    private static Path locateHomeFolder() {
        try {
            Path location = Path.of(ApplicationPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return homeFolderFor(location, Path.of(""));
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("Cannot locate the application home folder.", e);
        }
    }

    /**
     * Uses the JAR's folder for packaged launches and the working folder for development runs.
     */
    static Path homeFolderFor(Path location, Path workingFolder) throws IOException {
        Path home = Files.isRegularFile(location) ? location.toRealPath().getParent() : workingFolder.toRealPath();
        return home;
    }

    /**
     * Rejects traversal and existing symbolic links that would escape the home folder.
     */
    static Path resolveWithin(Path home, Path relativePath) {
        Path absoluteHome = home.toAbsolutePath().normalize();
        Path target = absoluteHome.resolve(relativePath).normalize();
        if (!target.startsWith(absoluteHome)) {
            throw new IllegalArgumentException("Runtime files must stay inside the application home folder.");
        }
        try {
            Path realHome = absoluteHome.toRealPath();
            Path current = target;
            while (current != null && current.startsWith(absoluteHome)) {
                if (Files.exists(current, LinkOption.NOFOLLOW_LINKS) && !current.toRealPath().startsWith(realHome)) {
                    throw new IllegalArgumentException("Runtime files must stay inside the application home folder.");
                }
                current = current.getParent();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot validate the runtime file path.", e);
        }
        return target;
    }
}
