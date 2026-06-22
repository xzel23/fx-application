package com.dua3.fx.application;

import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Base class for JavaFX tests.
 * <p>
 * This class handles the initialization and shutdown of the JavaFX platform.
 * It ensures that the platform is initialized before any tests run and is not
 * shut down between test classes, allowing multiple JavaFX test classes to run
 * in sequence.
 */
public abstract class FxTestBase {

    /**
     * Initialize the JavaFX platform if it's not already initialized.
     * This method is synchronized to prevent multiple concurrent initializations.
     */
    @BeforeAll
    public static void initializePlatform() {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException ex) {
            // if the Platform is already running, start right now
            latch.countDown();
        }
        await(latch);
    }

    /**
     * Waits for the specified {@code CountDownLatch} to reach the count of zero within a timeout period.
     * Throws an {@code IllegalStateException} if the timeout occurs or the thread is interrupted
     * while waiting.
     *
     * @param latch the {@code CountDownLatch} to wait on. It must not be null.
     * @throws IllegalStateException if the waiting thread is interrupted or the latch times out
     *         before reaching the count of zero.
     */
    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(30, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timed out while starting JavaFX toolkit");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while starting JavaFX toolkit", e);
        }
    }

    /**
     * This method intentionally does not call Platform.exit().
     * The platform will be shut down when the JVM exits.
     * This allows multiple JavaFX test classes to run in sequence.
     */
    @AfterAll
    public static void cleanupPlatform() {
        // Intentionally empty - we don't want to shut down the platform between test classes
        System.out.println("JavaFX test completed, keeping platform running for subsequent tests");
    }
}
