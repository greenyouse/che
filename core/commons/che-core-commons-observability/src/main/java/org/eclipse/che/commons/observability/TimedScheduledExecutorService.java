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
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.internal.TimedExecutorService;
import java.lang.reflect.Field;
import java.util.concurrent.*;

public class TimedScheduledExecutorService extends TimedExecutorService
    implements ScheduledExecutorService {
  private final MeterRegistry registry;
  private final ScheduledExecutorService delegate;
  private final String executorServiceName;
  private final Iterable<Tag> tags;
  private final Timer timer;

  public TimedScheduledExecutorService(
      MeterRegistry registry,
      ScheduledExecutorService delegate,
      String executorServiceName,
      Iterable<Tag> tags) {
    super(registry, delegate, executorServiceName, tags);
    this.registry = registry;
    this.delegate = delegate;
    this.executorServiceName = executorServiceName;
    this.tags = tags;
    this.timer = getOrCreateTimer();
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return delegate.schedule(timer.wrap(command), delay, unit);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return delegate.schedule(timer.wrap(callable), delay, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(
      Runnable command, long initialDelay, long period, TimeUnit unit) {
    return delegate.scheduleAtFixedRate(timer.wrap(command), initialDelay, period, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(
      Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return delegate.scheduleWithFixedDelay(timer.wrap(command), initialDelay, delay, unit);
  }

  protected Timer getOrCreateTimer() {
    try {
      Field e = TimedExecutorService.class.getDeclaredField("timer");
      e.setAccessible(true);
      return (Timer) e.get(this);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Do nothing. We simply can't get to the underlying ThreadPoolExecutor.
    }
    return registry.timer("executor_timer", Tags.concat(tags, "name", executorServiceName));
  }
}
