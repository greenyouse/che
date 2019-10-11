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
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CountedRejectedExecutionHandler implements RejectedExecutionHandler {

  private final RejectedExecutionHandler delegate;
  private final Counter counter;

  public CountedRejectedExecutionHandler(
      RejectedExecutionHandler delegate, MeterRegistry registry, String name) {
    this.delegate = delegate;
    this.counter = registry.counter("executor.rejected", Tags.of("name", name));
  }

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    counter.increment();
    delegate.rejectedExecution(r, executor);
  }

  public static void monitorRejections(
      MeterRegistry registry, ThreadPoolExecutor executor, String name) {
    executor.setRejectedExecutionHandler(
        new CountedRejectedExecutionHandler(
            executor.getRejectedExecutionHandler(), registry, name));
  }
}
