package org.eclipse.che.commons.observability;

import io.opentracing.noop.NoopTracerFactory;
import org.eclipse.che.commons.lang.execution.Executor;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.testng.Assert.*;

public class TracingExecutorWrapperTest {
    @Test
    public void testName() {
        TracingExecutorWrapper wr = new TracingExecutorWrapper(NoopTracerFactory.create());
        ExecutorService executor = wr.wrap(Executors.newSingleThreadExecutor(), ExecutorService.class, "id", Collections.emptyMap());
        executor.execute(() -> {

        });
    }
}