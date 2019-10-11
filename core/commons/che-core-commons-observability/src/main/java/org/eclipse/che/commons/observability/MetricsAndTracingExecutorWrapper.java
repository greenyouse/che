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

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.opentracing.Tracer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import org.eclipse.che.commons.schedule.executor.CronExecutorService;

public class MetricsAndTracingExecutorWrapper extends TracingExecutorWrapper {
  private final MeterRegistry meterRegistry;

  @Inject
  public MetricsAndTracingExecutorWrapper(Tracer tracer, MeterRegistry meterRegistry) {
    super(tracer);
    this.meterRegistry = meterRegistry;
  }

  @Override
  public ScheduledExecutorService wrap(
      ScheduledExecutorService executor, String name, String... tags) {
    if (executor instanceof ThreadPoolExecutor) {
      CountedThreadFactory.monitorThreads(meterRegistry, (ThreadPoolExecutor) executor, name);
      CountedRejectedExecutionHandler.monitorRejections(
          meterRegistry, (ThreadPoolExecutor) executor, name);
    }

    ScheduledExecutorService monitoredExecutor =
        ExecutorServiceMetrics.monitor(meterRegistry, executor, name, Tags.of(tags));

    return super.wrap(monitoredExecutor, name, tags);
  }

  @Override
  public ExecutorService wrap(ExecutorService executor, String name, String... tags) {
    if (executor instanceof ThreadPoolExecutor) {
      CountedThreadFactory.monitorThreads(meterRegistry, (ThreadPoolExecutor) executor, name);
      CountedRejectedExecutionHandler.monitorRejections(
          meterRegistry, (ThreadPoolExecutor) executor, name);
    }
    ExecutorService monitoredExecutor =
        ExecutorServiceMetrics.monitor(meterRegistry, executor, name, Tags.of(tags));

    return super.wrap(monitoredExecutor, name, tags);
  }

  @Override
  public CronExecutorService wrap(CronExecutorService executor, String name, String... tags) {
    if (executor instanceof ThreadPoolExecutor) {
      CountedThreadFactory.monitorThreads(meterRegistry, (ThreadPoolExecutor) executor, name);
      CountedRejectedExecutionHandler.monitorRejections(
          meterRegistry, (ThreadPoolExecutor) executor, name);
    }

    new io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics(
            executor, name, Tags.of(tags))
        .bindTo(meterRegistry);

    return super.wrap(
        new TimedCronExecutorService(meterRegistry, executor, name, Tags.of(tags)), name, tags);
  }
}
