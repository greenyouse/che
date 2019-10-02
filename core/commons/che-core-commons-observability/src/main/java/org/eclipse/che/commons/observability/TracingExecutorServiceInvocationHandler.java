package org.eclipse.che.commons.observability;

import org.eclipse.che.commons.schedule.executor.CronThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TracingExecutorServiceInvocationHandler implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CronThreadPoolExecutor.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOG.info("{} {} {} ", proxy, method, args);
        return null;
    }
}
