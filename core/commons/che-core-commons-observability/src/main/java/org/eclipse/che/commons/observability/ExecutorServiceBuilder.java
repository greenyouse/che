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
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorServiceBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(ExecutorServiceBuilder.class);

  private final String nameFormat;
  private int corePoolSize;
  private int maximumPoolSize;
  private boolean allowCoreThreadTimeOut;
  private Duration keepAliveTime;
  private BlockingQueue<Runnable> workQueue;
  private ThreadFactory threadFactory;
  private RejectedExecutionHandler handler;
  private MeterRegistry registry;

  public ExecutorServiceBuilder(String nameFormat, ThreadFactory factory) {
    this.nameFormat = nameFormat;
    this.corePoolSize = 0;
    this.maximumPoolSize = 1;
    this.allowCoreThreadTimeOut = false;
    this.keepAliveTime = Duration.ofSeconds(60);
    this.workQueue = new LinkedBlockingQueue<>();
    this.threadFactory = factory;
    this.handler = new ThreadPoolExecutor.AbortPolicy();
  }

  public ExecutorServiceBuilder minThreads(int threads) {
    this.corePoolSize = threads;
    return this;
  }

  public ExecutorServiceBuilder maxThreads(int threads) {
    this.maximumPoolSize = threads;
    return this;
  }

  public ExecutorServiceBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
    this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    return this;
  }

  public ExecutorServiceBuilder keepAliveTime(Duration time) {
    this.keepAliveTime = time;
    return this;
  }

  public ExecutorServiceBuilder workQueue(BlockingQueue<Runnable> workQueue) {
    this.workQueue = workQueue;
    return this;
  }

  public ExecutorServiceBuilder rejectedExecutionHandler(RejectedExecutionHandler handler) {
    this.handler = handler;
    return this;
  }

  public ExecutorServiceBuilder threadFactory(ThreadFactory threadFactory) {
    this.threadFactory = threadFactory;
    return this;
  }

  public ExecutorService build() {
    if (corePoolSize != maximumPoolSize && maximumPoolSize > 1 && !isBoundedQueue()) {
      LOG.warn("Parameter 'maximumPoolSize' is conflicting with unbounded work queues");
    }

    CountedThreadFactory countedThreadFactory =
        new CountedThreadFactory(threadFactory, null, nameFormat);
    final ThreadPoolExecutor executor =
        new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime.toMillis(),
            TimeUnit.MILLISECONDS,
            workQueue,
            countedThreadFactory,
            handler);
    executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);

    return executor;
  }

  private boolean isBoundedQueue() {
    return workQueue.remainingCapacity() != Integer.MAX_VALUE;
  }
}
