package seedu.address.ui;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javafx.application.Platform;

/**
 * Starts the JavaFX toolkit for UI tests.
 * The UI tests share one toolkit, because JavaFX can start it only once per JVM.
 */
final class JavaFxTestToolkit {

    static final Duration REUSE_TIMEOUT = Duration.ofSeconds(10);

    static final String MESSAGE_CANNOT_START = "Could not start the JavaFX toolkit. This usually means that"
            + " there is no display: run the UI tests (POCO_UI_TESTS=true) only in a desktop session.";
    static final String MESSAGE_CANNOT_REUSE = "Could not start the JavaFX toolkit, and the toolkit that an"
            + " earlier test started did not run a task within %d ms. An earlier attempt to start it may have"
            + " failed, or it may have been shut down, for example by Platform.exit() or by closing its last"
            + " window while implicit exit was on. The cause says why starting failed this time.";
    static final String MESSAGE_INTERRUPTED =
            "Interrupted while checking that the JavaFX toolkit runs tasks.";

    private JavaFxTestToolkit() {}

    /**
     * Starts the JavaFX toolkit, or reuses the toolkit that an earlier UI test started if it still runs
     * tasks. Also keeps the toolkit running after a test closes its last window, so that later UI tests can
     * reuse it.
     *
     * @throws IllegalStateException if the toolkit can be neither started nor reused.
     *     Its cause is the original error. If the calling thread is interrupted before or while waiting for the check,
     *     its cause is the {@code InterruptedException} instead, and the original error is suppressed.
     */
    static void startOrReuse() {
        startOrReuse(() -> Platform.startup(() -> {}), Platform::runLater, REUSE_TIMEOUT);
        Platform.setImplicitExit(false);
    }

    /**
     * Runs {@code startup}. If it reports that the toolkit was started before, checks that a task given to
     * {@code runLater} runs within {@code timeout}.
     *
     * @throws IllegalStateException if the toolkit can be neither started nor reused.
     *     Its cause is the original error. If the calling thread is interrupted before or while waiting for the check,
     *     its cause is the {@code InterruptedException} instead, and the original error is suppressed.
     */
    static void startOrReuse(Runnable startup, Consumer<Runnable> runLater, Duration timeout) {
        try {
            startup.run();
        } catch (IllegalStateException e) {
            // JavaFX throws this once any earlier start has begun, even if that start failed or the toolkit
            // has since been shut down
            checkRunsTasks(runLater, timeout, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException(MESSAGE_CANNOT_START, e);
        }
    }

    /**
     * Throws an {@code IllegalStateException} caused by {@code startupError} if a task given to
     * {@code runLater} does not run within {@code timeout}.
     * A toolkit that has been shut down drops such tasks without an error, and after a failed start,
     * {@code runLater} blocks its caller forever, so it is called on a separate thread that can be abandoned.
     */
    private static void checkRunsTasks(Consumer<Runnable> runLater, Duration timeout,
            IllegalStateException startupError) {
        CountDownLatch isSettled = new CountDownLatch(1);
        AtomicBoolean hasRun = new AtomicBoolean();
        AtomicReference<RuntimeException> runLaterError = new AtomicReference<>();
        Thread submitter = new Thread(() -> {
            try {
                runLater.accept(() -> {
                    hasRun.set(true);
                    isSettled.countDown();
                });
            } catch (RuntimeException e) {
                runLaterError.set(e);
                isSettled.countDown();
            }
        }, "JavaFX toolkit reuse check");
        submitter.setDaemon(true);
        submitter.start();

        try {
            isSettled.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            IllegalStateException interrupted = new IllegalStateException(MESSAGE_INTERRUPTED, e);
            interrupted.addSuppressed(startupError);
            throw interrupted;
        }
        if (hasRun.get()) {
            return;
        }

        String cannotReuseMessage = String.format(MESSAGE_CANNOT_REUSE, timeout.toMillis());
        IllegalStateException cannotReuse = new IllegalStateException(cannotReuseMessage, startupError);
        if (runLaterError.get() != null) {
            cannotReuse.addSuppressed(runLaterError.get());
        }
        throw cannotReuse;
    }
}
