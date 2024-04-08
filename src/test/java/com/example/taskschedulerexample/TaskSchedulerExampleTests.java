package com.example.taskschedulerexample;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskSchedulerExampleTests {
    @Test
    void tracksSchedulingOfTask() throws Exception {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        SimpleAsyncTaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
        ScheduledFuture<?> schedule = taskScheduler.schedule(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            atomicBoolean.set(true);
        }, Instant.now().plusSeconds(1));

        schedule.get();

        assertTrue(atomicBoolean.get());
    }

    @Test
    void tracksExecutionTask() throws Exception {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        ScheduledFuture<?> schedule = taskScheduler.schedule(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            atomicBoolean.set(true);
        }, Instant.now().plusSeconds(1));

        schedule.get();

        assertTrue(atomicBoolean.get());
    }

    @Test
    void doesNotThrowTaskException() throws Exception {
        SimpleAsyncTaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
        ScheduledFuture<?> schedule = taskScheduler.schedule(() -> {
            throw new RuntimeException();
        }, Instant.now().plusSeconds(1));

        assertThrows(ExecutionException.class, schedule::get);
    }

    @Test
    void throwsTaskException() throws Exception {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        ScheduledFuture<?> schedule = taskScheduler.schedule(() -> {
            throw new RuntimeException();
        }, Instant.now().plusSeconds(1));

        assertThrows(ExecutionException.class, schedule::get);
    }
}
