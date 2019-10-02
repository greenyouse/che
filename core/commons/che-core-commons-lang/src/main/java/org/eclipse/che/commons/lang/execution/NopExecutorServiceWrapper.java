package org.eclipse.che.commons.lang.execution;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Implementation of {@link ExecutorServiceWrapper} that do nothing.
 */
public class NopExecutorServiceWrapper implements ExecutorServiceWrapper {

    @Override
    public <E extends ExecutorService> E wrap(E executor, Class<E> executorType, String id, Map<String, String> tags) {
        return executor;
    }
}
