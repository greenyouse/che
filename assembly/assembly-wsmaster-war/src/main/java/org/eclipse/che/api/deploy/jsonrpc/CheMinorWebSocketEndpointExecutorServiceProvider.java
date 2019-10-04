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
package org.eclipse.che.api.deploy.jsonrpc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.eclipse.che.commons.lang.concurrent.LoggingUncaughtExceptionHandler;
import org.eclipse.che.commons.lang.execution.ExecutorServiceProvider;
import org.eclipse.che.commons.observability.ExecutorWrapper;
import org.eclipse.che.commons.observability.ObservableExecutorServiceProvider;

/** {@link ExecutorService} provider used in {@link CheMinorWebSocketEndpoint}. */
@Singleton
public class CheMinorWebSocketEndpointExecutorServiceProvider
    extends ObservableExecutorServiceProvider {

  public static final String JSON_RPC_MINOR_CORE_POOL_SIZE_PARAMETER_NAME =
      "che.core.jsonrpc.minor_processor_core_pool_size";
  public static final String JSON_RPC_MINOR_MAX_POOL_SIZE_PARAMETER_NAME =
      "che.core.jsonrpc.minor_processor_max_pool_size";
  public static final String JSON_RPC_MINOR_QUEUE_CAPACITY_PARAMETER_NAME =
      "che.core.jsonrpc.minor_processor_queue_capacity";

  @Inject
  public CheMinorWebSocketEndpointExecutorServiceProvider(
      @Named(JSON_RPC_MINOR_CORE_POOL_SIZE_PARAMETER_NAME) int corePoolSize,
      @Named(JSON_RPC_MINOR_MAX_POOL_SIZE_PARAMETER_NAME) int maxPoolSize,
      @Named(JSON_RPC_MINOR_QUEUE_CAPACITY_PARAMETER_NAME) int queueCapacity,
      ExecutorWrapper wrapper) {
    super(
        new ExecutorServiceProvider(
            corePoolSize,
            maxPoolSize,
            queueCapacity,
            new ThreadFactoryBuilder()
                .setUncaughtExceptionHandler(LoggingUncaughtExceptionHandler.getInstance())
                .setNameFormat(
                    CheMajorWebSocketEndpointExecutorServiceProvider.class.getSimpleName() + "-%d")
                .setDaemon(true)
                .build()),
        wrapper,
        "che.core.jsonrpc.Minor");
  }
}
