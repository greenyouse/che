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

import com.google.common.annotations.Beta;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import org.eclipse.che.commons.schedule.executor.CronExecutorService;

@Beta
public interface ExecutorWrapper {

  ExecutorService wrap(ExecutorService executor, String name, String... tags);

  ScheduledExecutorService wrap(ScheduledExecutorService executor, String name, String... tags);

  CronExecutorService wrap(CronExecutorService executor, String name, String... tags);

  // Executor wrap(Executor executor, String name, String... tags);
}
