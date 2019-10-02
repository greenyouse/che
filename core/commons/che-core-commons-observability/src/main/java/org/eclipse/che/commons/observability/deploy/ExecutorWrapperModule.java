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
package org.eclipse.che.commons.observability.deploy;

import com.google.inject.AbstractModule;
import org.eclipse.che.commons.lang.execution.ExecutorServiceWrapper;
import org.eclipse.che.commons.lang.execution.NopExecutorServiceWrapper;
import org.eclipse.che.commons.observability.*;

public class ExecutorWrapperModule extends AbstractModule {

  @Override
  protected void configure() {

    if (!Boolean.valueOf(System.getenv("CHE_METRICS_ENABLED"))
        && !Boolean.valueOf(System.getenv("CHE_TRACING_ENABLED"))) {
      bind(ExecutorServiceWrapper.class).to(NopExecutorServiceWrapper.class);
    } else if (Boolean.valueOf(System.getenv("CHE_METRICS_ENABLED"))) {
      // the situation with disabled tracing handled over NopTracer
      //bind(ExecutorWrapper.class).to(MetricsAndTracingExecutorWrapper.class);
    } else {
      //bind(ExecutorWrapper.class).to(TracingExecutorWrapper.class);
    }
  }
}
