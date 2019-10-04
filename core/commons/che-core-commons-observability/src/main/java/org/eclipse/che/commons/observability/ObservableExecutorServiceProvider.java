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

import java.util.concurrent.ExecutorService;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ObservableExecutorServiceProvider implements Provider<ExecutorService> {

  private final ExecutorService executorService;

  public ObservableExecutorServiceProvider(
      Provider<ExecutorService> executorServiceProvider,
      ExecutorWrapper wrapper,
      String name,
      String... tags) {
    executorService = wrapper.wrap(executorServiceProvider.get(), name, tags);
  }

  @Override
  public ExecutorService get() {
    return executorService;
  }
}
