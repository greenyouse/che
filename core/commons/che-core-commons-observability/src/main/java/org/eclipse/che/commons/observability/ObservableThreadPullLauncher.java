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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.micrometer.core.instrument.Tags;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.che.commons.lang.concurrent.LoggingUncaughtExceptionHandler;
import org.eclipse.che.commons.lang.execution.ExecutorServiceWrapper;
import org.eclipse.che.commons.schedule.executor.CronThreadPoolExecutor;
import org.eclipse.che.commons.schedule.executor.ThreadPullLauncher;

public class ObservableThreadPullLauncher extends ThreadPullLauncher {
  /**
   * @param corePoolSize           the number of threads to keep in the pool, even if they are idle, unless
   *                               {@code allowCoreThreadTimeOut} is set
   * @param executorServiceWrapper
   */
  public ObservableThreadPullLauncher(Integer corePoolSize, ExecutorServiceWrapper executorServiceWrapper) {
    super(corePoolSize, executorServiceWrapper);
  }

//  @Inject
//  public ObservableThreadPullLauncher(
//      ExecutorWrapper wrapper, @Named("schedule.core_pool_size") Integer corePoolSize) {
//    super(
//        wrapper.wrap(
//            new CronThreadPoolExecutor(
//                corePoolSize,
//                new ThreadFactoryBuilder()
//                    .setNameFormat("Annotated-scheduler-%d")
//                    .setUncaughtExceptionHandler(LoggingUncaughtExceptionHandler.getInstance())
//                    .setDaemon(false)
//                    .build()),
//            "Annotated-scheduler",
//            Tags.empty()));
//  }
}
