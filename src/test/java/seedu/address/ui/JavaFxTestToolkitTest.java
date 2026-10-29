package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@code JavaFxTestToolkit} with stand-ins for {@code Platform.startup} and {@code Platform.runLater},
 * so that no test starts or shuts down the JavaFX toolkit that the UI tests share.
 */
public class JavaFxTestToolkitTest {

    private static final Duration SHORT_TIMEOUT = Duration.ofMillis(50);
    private static final String MESSAGE_CANNOT_REUSE_SHORT_TIMEOUT =
            String.format(JavaFxTestToolkit.MESSAGE_CANNOT_REUSE, SHORT_TIMEOUT.toMillis());
    private static final Consumer<Runnable> RUNS_TASK = Runnable::run;
    private static final Consumer<Runnable> DROPS_TASK = task -> {};

    private final CountDownLatch blockedRunLaterRelease = new CountDownLatch(1);

    @AfterEach
    public void tearDown() {
        blockedRunLaterRelease.countDown();
        // An interrupt left on the JUnit thread would break later tests
        Thread.interrupted();
    }

    @Test
    public void startOrReuse_startSucceeds_doesNotCheckTasks() {
        assertDoesNotThrow(() -> JavaFxTestToolkit.startOrReuse(() -> {},
                task -> fail("A new toolkit needs no check"), SHORT_TIMEOUT));
    }

    @Test
    public void startOrReuse_alreadyStartedAndRunsTasks_reusesToolkit() {
        IllegalStateException startupError = new IllegalStateException("Toolkit already initialized");

        assertDoesNotThrow(() ->
                JavaFxTestToolkit.startOrReuse(throwing(startupError), RUNS_TASK, SHORT_TIMEOUT));
    }

    @Test
    public void startOrReuse_alreadyStartedButDropsTasks_throwsWithStartupErrorAsCause() {
        IllegalStateException startupError = new IllegalStateException("Platform.exit has been called");

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                JavaFxTestToolkit.startOrReuse(throwing(startupError), DROPS_TASK, SHORT_TIMEOUT));

        assertEquals(MESSAGE_CANNOT_REUSE_SHORT_TIMEOUT, thrown.getMessage());
        assertSame(startupError, thrown.getCause());
    }

    @Test
    public void startOrReuse_earlierStartFailedSoRunLaterBlocks_throwsWithinTimeout() {
        IllegalStateException startupError = new IllegalStateException("Toolkit already initialized");
        Consumer<Runnable> blocksForever = task -> awaitRelease();

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                JavaFxTestToolkit.startOrReuse(throwing(startupError), blocksForever, SHORT_TIMEOUT));

        assertEquals(MESSAGE_CANNOT_REUSE_SHORT_TIMEOUT, thrown.getMessage());
        assertSame(startupError, thrown.getCause());
    }

    @Test
    public void startOrReuse_runLaterRefusesTask_throwsWithBothErrors() {
        IllegalStateException startupError = new IllegalStateException("Toolkit already initialized");
        IllegalStateException runLaterError = new IllegalStateException("Toolkit not initialized");

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                JavaFxTestToolkit.startOrReuse(throwing(startupError), refusing(runLaterError),
                        SHORT_TIMEOUT));

        assertEquals(MESSAGE_CANNOT_REUSE_SHORT_TIMEOUT, thrown.getMessage());
        assertSame(startupError, thrown.getCause());
        assertArrayEquals(new Throwable[] {runLaterError}, thrown.getSuppressed());
    }

    @Test
    public void startOrReuse_noDisplay_throwsWithStartupErrorAsCause() {
        RuntimeException startupError = new UnsupportedOperationException("Unable to open DISPLAY");

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                JavaFxTestToolkit.startOrReuse(throwing(startupError), RUNS_TASK, SHORT_TIMEOUT));

        assertEquals(JavaFxTestToolkit.MESSAGE_CANNOT_START, thrown.getMessage());
        assertSame(startupError, thrown.getCause());
    }

    @Test
    public void startOrReuse_interruptedWhileWaiting_throwsAndKeepsInterrupt() {
        IllegalStateException startupError = new IllegalStateException("Toolkit already initialized");
        Thread.currentThread().interrupt();

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                JavaFxTestToolkit.startOrReuse(throwing(startupError), DROPS_TASK, SHORT_TIMEOUT));

        assertEquals(JavaFxTestToolkit.MESSAGE_INTERRUPTED, thrown.getMessage());
        assertInstanceOf(InterruptedException.class, thrown.getCause());
        assertArrayEquals(new Throwable[] {startupError}, thrown.getSuppressed());
        assertTrue(Thread.currentThread().isInterrupted());
    }

    private void awaitRelease() {
        try {
            blockedRunLaterRelease.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Runnable throwing(RuntimeException error) {
        return () -> {
            throw error;
        };
    }

    private static Consumer<Runnable> refusing(RuntimeException error) {
        return task -> {
            throw error;
        };
    }
}
