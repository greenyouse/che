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
package org.eclipse.che.commons.lang.execution;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Interface for {@link ExecutorService} classes which provide the ability to wrap original class
 * and add extra functionality, with intent to maintain original behaviour unaffected.
 * Typical use-case is adding monitoring or tracing capabilities.
 */
public interface ExecutorServiceWrapper {

    /**
     * Returns a wrapper of <code>executor</code> that implements the same class as input parameter <code>executor</code> .
     *
     * @param executor service that has to be wrapped
     * @param id       a unique identifier of  <code>executor</code>
     * @param tags     A bunch of additional information in the form if the key-value that might be useful to know.
     * @return an object implements the same class as input parameter. May be a proxy for the actual implementing object.
     */
    <E extends ExecutorService> E wrap(E executor,  Class<E> executorType, String id, Map<String, String> tags);

}
