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
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.Future;
import org.eclipse.che.commons.schedule.executor.CronExecutorService;
import org.eclipse.che.commons.schedule.executor.CronExpression;

public class TimedCronExecutorService extends TimedScheduledExecutorService
    implements CronExecutorService {
  private final CronExecutorService delegate;

  private final Timer timer;

  public TimedCronExecutorService(
      MeterRegistry registry,
      CronExecutorService delegate,
      String executorServiceName,
      Iterable<Tag> tags) {
    super(registry, delegate, executorServiceName, tags);
    this.delegate = delegate;
    this.timer = getOrCreateTimer();
  }

  @Override
  public Future<?> schedule(Runnable task, CronExpression expression) {
    return delegate.schedule(timer.wrap(task), expression);
  }
}
