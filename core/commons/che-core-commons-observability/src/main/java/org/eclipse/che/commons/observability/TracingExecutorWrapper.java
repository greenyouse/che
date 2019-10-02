/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.commons.observability;

import io.opentracing.Tracer;
import org.eclipse.che.commons.lang.execution.ExecutorServiceWrapper;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class TracingExecutorWrapper implements ExecutorServiceWrapper {


    private final Tracer tracer;

    @Inject
    public TracingExecutorWrapper(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public <E extends ExecutorService> E wrap(E executor, Class<E> executorType, String id, Map<String, String> tags) {
        return (E) Proxy.newProxyInstance(
                executor.getClass().getClassLoader(),
                new Class[]{executorType},
                new TracingExecutorServiceInvocationHandler());
    }
}
