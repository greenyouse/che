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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/** A {@link ThreadFactory} that monitors the number of threads created, running and terminated. */
public class CountedThreadFactory implements ThreadFactory {

  private final ThreadFactory delegate;
  private final Counter created;
  private final AtomicInteger running;
  private final Counter terminated;

  /**
   * Wraps a {@link ThreadFactory} with an explicit name.
   *
   * @param delegate {@link ThreadFactory} to wrap.
   * @param registry {@link MeterRegistry} that will contain the metrics.
   * @param name name for this delegate.
   */
  public CountedThreadFactory(ThreadFactory delegate, MeterRegistry registry, String name) {
    this.delegate = delegate;
    this.created = Counter.builder("executor.thread.created").tag("name", name).register(registry);
    this.terminated =
        Counter.builder("executor.thread.terminated").tag("name", name).register(registry);
    this.running =
        registry.gauge("executor.thread.active", Tags.of("name", name), new AtomicInteger(0));
  }

  /** {@inheritDoc} */
  @Override
  public Thread newThread(Runnable runnable) {

    Thread thread =
        delegate.newThread(
            () -> {
              running.incrementAndGet();
              try {
                runnable.run();
              } finally {
                running.decrementAndGet();
                terminated.increment();
              }
            });
    created.increment();
    return thread;
  }

  public static void monitorThreads(
      MeterRegistry registry, ThreadPoolExecutor executor, String name) {
    executor.setThreadFactory(
        new CountedThreadFactory(executor.getThreadFactory(), registry, name));
  }
}
