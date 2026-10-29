package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    private static final String OLD_CONTENT = "{ \"persons\" : [ \"Alex\" ] }";
    private static final String NEW_CONTENT = "{ \"persons\" : [ \"Alex\", \"Zoë\" ] }";
    private static final String DISK_FULL_MESSAGE = "No space left on device";

    @TempDir
    public Path testFolder;

    @Test
    public void writeToFile_nullFile_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(null, NEW_CONTENT));
    }

    @Test
    public void writeToFile_newFile_writesContent() throws IOException {
        Path file = testFolder.resolve("addressbook.json");

        FileUtil.writeToFile(file, NEW_CONTENT);

        assertEquals(NEW_CONTENT, FileUtil.readFromFile(file));
        assertTestFolderContainsOnly(file);
    }

    @Test
    public void writeToFile_existingFile_replacesContent() throws IOException {
        Path file = createFileWithOldContent();

        FileUtil.writeToFile(file, NEW_CONTENT);

        assertEquals(NEW_CONTENT, FileUtil.readFromFile(file));
        assertTestFolderContainsOnly(file);
    }

    @Test
    public void writeToFile_writeFailsPartway_keepsPreviousContentAndDeletesTemporaryFile() throws IOException {
        Path file = createFileWithOldContent();
        List<Path> temporaryFiles = new ArrayList<>();

        assertThrows(IOException.class, DISK_FULL_MESSAGE, () -> FileUtil.writeToFile(file, NEW_CONTENT,
                recordingFailingWriter(temporaryFiles)));

        assertEquals(OLD_CONTENT, FileUtil.readFromFile(file));
        // The temporary file must be in the same folder, so that it can replace the file in one step
        assertEquals(testFolder.toRealPath(), temporaryFiles.get(0).getParent().toRealPath());
        assertTestFolderContainsOnly(file);
    }

    @Test
    public void writeToFile_writeFailsPartwayForNewFile_createsNoFile() throws IOException {
        Path file = testFolder.resolve("addressbook.json");

        assertThrows(IOException.class, DISK_FULL_MESSAGE, () -> FileUtil.writeToFile(file, NEW_CONTENT,
                recordingFailingWriter(new ArrayList<>())));

        assertTestFolderContainsOnly();
    }

    @Test
    public void writeToFile_readOnlyFile_throwsAccessDeniedExceptionAndKeepsContent() throws IOException {
        Path file = createFileWithOldContent();
        assumeTrue(file.toFile().setWritable(false), "Cannot make the file read-only");
        try {
            assumeFalse(Files.isWritable(file), "This user can write to read-only files");

            assertThrows(AccessDeniedException.class, file.toString(), () -> FileUtil.writeToFile(file, NEW_CONTENT));

            assertEquals(OLD_CONTENT, FileUtil.readFromFile(file));
            assertTestFolderContainsOnly(file);
        } finally {
            file.toFile().setWritable(true);
        }
    }

    @Test
    public void writeToFile_readOnlyFolder_throwsAccessDeniedExceptionNamingFile() throws IOException {
        Path file = createFileWithOldContent();
        assumeFolderCanBeMadeReadOnly();
        testFolder.toFile().setWritable(false);
        try {
            assertThrows(AccessDeniedException.class, file.toString(), () -> FileUtil.writeToFile(file, NEW_CONTENT));

            assertEquals(OLD_CONTENT, FileUtil.readFromFile(file));
            assertTestFolderContainsOnly(file);
        } finally {
            testFolder.toFile().setWritable(true);
        }
    }

    @Test
    public void writeToFile_replaceFails_keepsPreviousContent() throws IOException {
        Path file = createFileWithOldContent();
        assumeFolderCanBeMadeReadOnly();
        // Writes the whole temporary file, then stops the folder from changing, so that only the replacing fails
        FileUtil.ContentWriter writeThenMakeFolderReadOnly = (temporaryFile, content) -> {
            Files.write(temporaryFile, content);
            testFolder.toFile().setWritable(false);
        };
        try {
            assertThrows(AccessDeniedException.class, file.toString(), () ->
                    FileUtil.writeToFile(file, NEW_CONTENT, writeThenMakeFolderReadOnly));

            assertEquals(OLD_CONTENT, FileUtil.readFromFile(file));
        } finally {
            testFolder.toFile().setWritable(true);
        }
    }

    @Test
    public void writeToFile_emptyFolder_throwsFileSystemExceptionAndKeepsFolder() throws IOException {
        Path folder = Files.createDirectory(testFolder.resolve("addressbook.json"));

        // The message differs between systems, such as "Is a directory" on macOS and Linux
        assertThrows(FileSystemException.class, () -> FileUtil.writeToFile(folder, NEW_CONTENT));

        assertTrue(Files.isDirectory(folder));
        assertTestFolderContainsOnly(folder);
    }

    @Test
    public void writeToFile_symbolicLink_replacesLinkedFile() throws IOException {
        Path linkedFile = createFileWithOldContent();
        Path link = testFolder.resolve("link.json");
        try {
            Files.createSymbolicLink(link, linkedFile.getFileName());
        } catch (UnsupportedOperationException | FileSystemException e) {
            abort("Cannot create symbolic links here: " + e);
        }

        FileUtil.writeToFile(link, NEW_CONTENT);

        assertTrue(Files.isSymbolicLink(link));
        assertEquals(NEW_CONTENT, FileUtil.readFromFile(linkedFile));
        assertTestFolderContainsOnly(linkedFile, link);
    }

    @Test
    public void writeToFile_existingFile_keepsPosixPermissions() throws IOException {
        Path file = createFileWithOldContent();
        assumeTrue(Files.getFileAttributeView(file, PosixFileAttributeView.class) != null,
                "The file system has no POSIX permissions");
        // Differs from the usual permissions of both new files and new temporary files
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-r-----");
        Files.setPosixFilePermissions(file, permissions);

        FileUtil.writeToFile(file, NEW_CONTENT);

        assertEquals(permissions, Files.getPosixFilePermissions(file));
    }

    private Path createFileWithOldContent() throws IOException {
        return Files.writeString(testFolder.resolve("addressbook.json"), OLD_CONTENT);
    }

    /**
     * Skips the test unless making the test folder read-only stops this user from creating files in it.
     * It does not on Windows, or for the root user.
     */
    private void assumeFolderCanBeMadeReadOnly() {
        testFolder.toFile().setWritable(false);
        boolean isStillWritable = Files.isWritable(testFolder);
        testFolder.toFile().setWritable(true);
        assumeFalse(isStillWritable, "This user can create files in read-only folders");
    }

    /**
     * Returns a writer that records the file it is given, writes half of the content, then fails as a full disk would.
     */
    private static FileUtil.ContentWriter recordingFailingWriter(List<Path> writtenFiles) {
        return (file, content) -> {
            writtenFiles.add(file);
            Files.write(file, Arrays.copyOf(content, content.length / 2));
            throw new IOException(DISK_FULL_MESSAGE);
        };
    }

    /**
     * Asserts that the test folder contains exactly {@code expectedFiles}, so that no temporary file is left behind.
     */
    private void assertTestFolderContainsOnly(Path... expectedFiles) throws IOException {
        try (Stream<Path> files = Files.list(testFolder)) {
            assertEquals(Set.of(expectedFiles), files.collect(Collectors.toSet()));
        }
    }
}
