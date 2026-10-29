package seedu.address.commons.util;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final String TEMPORARY_FILE_SUFFIX = ".tmp";
    private static final Logger logger = LogsCenter.getLogger(FileUtil.class);

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet, but not its parent folder.
     * The string is first written to a temporary file in the same folder, which then replaces the file in one step.
     * If any step fails, the file keeps its previous content, and the temporary file is deleted where possible.
     *
     * @throws IOException if the string cannot be written, if the file cannot be opened for writing, or if the
     *     temporary file cannot be created in or moved within the folder.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        writeToFile(file, content, FileUtil::writeAndSync);
    }

    /**
     * Similar to {@link #writeToFile(Path, String)}, but writes the temporary file with {@code contentWriter}.
     */
    static void writeToFile(Path file, String content, ContentWriter contentWriter) throws IOException {
        requireAllNonNull(file, content, contentWriter);
        byte[] bytes = content.getBytes(CHARSET);
        checkWritable(file);
        Path target = resolveTarget(file);

        try {
            writeThenReplace(target, bytes, contentWriter);
        } catch (AccessDeniedException e) {
            // The user sees this message, so it names the file being saved rather than the temporary file
            AccessDeniedException deniedForFile = new AccessDeniedException(file.toString());
            deniedForFile.initCause(e);
            throw deniedForFile;
        }
    }

    /**
     * Opens {@code file} for writing, if it exists, and closes it without changing it. A file that could not be
     * written to in place, such as a read-only file or a folder, therefore still fails to save with the same error.
     * On macOS and Linux, moving a file onto a read-only file would otherwise succeed.
     */
    private static void checkWritable(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        FileChannel.open(file, StandardOpenOption.WRITE).close();
    }

    /**
     * Returns the file to replace when saving to {@code file}. If {@code file} is a symbolic link (a shortcut to
     * another file) and that file exists, this is the file that the link points to, so that saving updates that file
     * and keeps the link, as writing to the link did. Otherwise, it is {@code file} itself.
     */
    private static Path resolveTarget(Path file) throws IOException {
        return Files.exists(file) ? file.toRealPath() : file;
    }

    /**
     * Writes {@code content} to a new temporary file next to {@code target}, then replaces {@code target} with it.
     */
    private static void writeThenReplace(Path target, byte[] content, ContentWriter contentWriter)
            throws IOException {
        Path temporaryFile = Files.createTempFile(target.toAbsolutePath().getParent(),
                target.getFileName() + ".", TEMPORARY_FILE_SUFFIX);
        try {
            contentWriter.write(temporaryFile, content);
            copyPermissions(target, temporaryFile);
            replace(temporaryFile, target);
        } finally {
            deleteIfPresent(temporaryFile);
        }
    }

    /**
     * Writes {@code content} to {@code file} and waits until it reaches the storage device, so that the file is
     * complete before it replaces the previous one.
     */
    private static void writeAndSync(Path file, byte[] content) throws IOException {
        try (FileOutputStream output = new FileOutputStream(file.toFile())) {
            output.write(content);
            output.getFD().sync();
        }
    }

    /**
     * Gives {@code temporaryFile} the permissions of {@code target}, if it exists and the file system has POSIX
     * permissions, so that replacing {@code target} does not change who can read it.
     */
    private static void copyPermissions(Path target, Path temporaryFile) throws IOException {
        if (!Files.exists(target) || Files.getFileAttributeView(target, PosixFileAttributeView.class) == null) {
            return;
        }
        Files.setPosixFilePermissions(temporaryFile, Files.getPosixFilePermissions(target));
    }

    /**
     * Replaces {@code target} with {@code source} in one step, so that {@code target} always holds either its old or
     * its new content. There is no fallback to an ordinary move, which deletes {@code target} before moving
     * {@code source}, so a failure would lose both.
     */
    private static void replace(Path source, Path target) throws IOException {
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Deletes {@code temporaryFile} if it still exists, logging instead of throwing if that fails, so that the
     * original error, if any, is the one reported.
     */
    private static void deleteIfPresent(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException e) {
            logger.warning("Could not delete temporary file " + temporaryFile + ": " + e);
        }
    }

    /**
     * Writes the content of a file. Tests replace it to simulate a write that fails, or a failure after it.
     */
    @FunctionalInterface
    interface ContentWriter {
        void write(Path file, byte[] content) throws IOException;
    }

}
