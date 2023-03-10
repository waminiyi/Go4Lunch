package com.waminiyi.go4lunch;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class TaskUtil {

    private static final long BEST_EFFORT_DURATION = 10;
    private static final long WAIT_DURATION = 40;
    private static final TimeUnit WAIT_UNIT = TimeUnit.SECONDS;

    public TaskUtil() {
    }

    /**
     * Waits for the task to complete.
     *
     * <p>The primary use case for this method is to perform test clean-up in a {@code finally} block.
     * Clean-up is inherently a best-effort task, because it might not succeed when the test is
     * broken.
     *
     * <p>This method will block the current thread for a short period of time. Unlike the other
     * methods in this class, this method is not biased towards success or failure. This method does
     * not throw any exceptions.
     */
    public static void waitBestEffort(Task<?> task) {
        try {
            Tasks.await(task, BEST_EFFORT_DURATION, WAIT_UNIT);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException ex) {
            // Ignore.
        }
    }

    /**
     * Waits for the task to complete with a failure.
     *
     * <p>This method will block the current thread and return the resulting exception. An assertion
     * failure will be thrown if the task does not fail.
     */
    public static Throwable waitForFailure(Task<?> task) throws Exception {
        try {
            Tasks.await(task, WAIT_DURATION, WAIT_UNIT);
        } catch (ExecutionException ex) {
            return ex.getCause();
        }

        throw new AssertionError("Task did not fail");
    }

    /**
     * Waits for the task to complete successfully.
     *
     * <p>This method will block the current thread and return the result of the task. It will rethrow
     * any expection thrown by the task without wrapping.
     */
    public static <T> T waitForSuccess(Task<T> task) throws Exception {
        try {
            return Tasks.await(task, WAIT_DURATION, WAIT_UNIT);
        } catch (ExecutionException ex) {
            Throwable t = ex.getCause();
            if (t instanceof Exception) {
                throw (Exception) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            }

            throw new IllegalStateException("Task threw unexpected Throwable", t);
        }
    }
}
