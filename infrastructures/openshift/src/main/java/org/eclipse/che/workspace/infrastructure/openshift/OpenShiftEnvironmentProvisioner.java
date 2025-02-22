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
package org.eclipse.che.workspace.infrastructure.openshift;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.eclipse.che.api.core.model.workspace.runtime.RuntimeIdentity;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.commons.annotation.Traced;
import org.eclipse.che.commons.tracing.TracingTags;
import org.eclipse.che.workspace.infrastructure.kubernetes.KubernetesEnvironmentProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.namespace.pvc.WorkspaceVolumesStrategy;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.CertificateProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.GitUserProfileProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.ImagePullSecretProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.LogsVolumeMachineProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.PodTerminationGracePeriodProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.ProxySettingsProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.ServiceAccountProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.UniqueNamesProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.VcsSshKeysProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.env.EnvVarsConverter;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.limits.ram.RamLimitRequestProvisioner;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.restartpolicy.RestartPolicyRewriter;
import org.eclipse.che.workspace.infrastructure.kubernetes.provision.server.ServersConverter;
import org.eclipse.che.workspace.infrastructure.openshift.environment.OpenShiftEnvironment;
import org.eclipse.che.workspace.infrastructure.openshift.provision.OpenShiftUniqueNamesProvisioner;
import org.eclipse.che.workspace.infrastructure.openshift.provision.RouteTlsProvisioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies the set of configurations to the OpenShift environment and environment configuration with
 * the desired order, which corresponds to the needs of the OpenShift infrastructure.
 *
 * @author Anton Korneta
 * @author Alexander Garagatyi
 */
@Singleton
public class OpenShiftEnvironmentProvisioner
    implements KubernetesEnvironmentProvisioner<OpenShiftEnvironment> {

  private static final Logger LOG = LoggerFactory.getLogger(OpenShiftEnvironmentProvisioner.class);

  private final boolean pvcEnabled;
  private final WorkspaceVolumesStrategy volumesStrategy;
  private final UniqueNamesProvisioner<OpenShiftEnvironment> uniqueNamesProvisioner;
  private final RouteTlsProvisioner routeTlsProvisioner;
  private final ServersConverter<OpenShiftEnvironment> serversConverter;
  private final EnvVarsConverter envVarsConverter;
  private final RestartPolicyRewriter restartPolicyRewriter;
  private final RamLimitRequestProvisioner ramLimitProvisioner;
  private final LogsVolumeMachineProvisioner logsVolumeMachineProvisioner;
  private final PodTerminationGracePeriodProvisioner podTerminationGracePeriodProvisioner;
  private final ImagePullSecretProvisioner imagePullSecretProvisioner;
  private final ProxySettingsProvisioner proxySettingsProvisioner;
  private final ServiceAccountProvisioner serviceAccountProvisioner;
  private final CertificateProvisioner certificateProvisioner;
  private final VcsSshKeysProvisioner vcsSshKeysProvisioner;
  private final GitUserProfileProvisioner gitUserProfileProvisioner;

  @Inject
  public OpenShiftEnvironmentProvisioner(
      @Named("che.infra.kubernetes.pvc.enabled") boolean pvcEnabled,
      OpenShiftUniqueNamesProvisioner uniqueNamesProvisioner,
      RouteTlsProvisioner routeTlsProvisioner,
      ServersConverter<OpenShiftEnvironment> serversConverter,
      EnvVarsConverter envVarsConverter,
      RestartPolicyRewriter restartPolicyRewriter,
      WorkspaceVolumesStrategy volumesStrategy,
      RamLimitRequestProvisioner ramLimitProvisioner,
      LogsVolumeMachineProvisioner logsVolumeMachineProvisioner,
      PodTerminationGracePeriodProvisioner podTerminationGracePeriodProvisioner,
      ImagePullSecretProvisioner imagePullSecretProvisioner,
      ProxySettingsProvisioner proxySettingsProvisioner,
      ServiceAccountProvisioner serviceAccountProvisioner,
      CertificateProvisioner certificateProvisioner,
      VcsSshKeysProvisioner vcsSshKeysProvisioner,
      GitUserProfileProvisioner gitUserProfileProvisioner) {
    this.pvcEnabled = pvcEnabled;
    this.volumesStrategy = volumesStrategy;
    this.uniqueNamesProvisioner = uniqueNamesProvisioner;
    this.routeTlsProvisioner = routeTlsProvisioner;
    this.serversConverter = serversConverter;
    this.envVarsConverter = envVarsConverter;
    this.restartPolicyRewriter = restartPolicyRewriter;
    this.ramLimitProvisioner = ramLimitProvisioner;
    this.logsVolumeMachineProvisioner = logsVolumeMachineProvisioner;
    this.podTerminationGracePeriodProvisioner = podTerminationGracePeriodProvisioner;
    this.imagePullSecretProvisioner = imagePullSecretProvisioner;
    this.proxySettingsProvisioner = proxySettingsProvisioner;
    this.serviceAccountProvisioner = serviceAccountProvisioner;
    this.certificateProvisioner = certificateProvisioner;
    this.vcsSshKeysProvisioner = vcsSshKeysProvisioner;
    this.gitUserProfileProvisioner = gitUserProfileProvisioner;
  }

  @Override
  @Traced
  public void provision(OpenShiftEnvironment osEnv, RuntimeIdentity identity)
      throws InfrastructureException {

    TracingTags.WORKSPACE_ID.set(identity::getWorkspaceId);

    LOG.debug(
        "Start provisioning OpenShift environment for workspace '{}'", identity.getWorkspaceId());
    // 1 stage - update environment according Infrastructure specific
    if (pvcEnabled) {
      logsVolumeMachineProvisioner.provision(osEnv, identity);
    }

    // 2 stage - converting Che model env to OpenShift env
    serversConverter.provision(osEnv, identity);
    envVarsConverter.provision(osEnv, identity);
    if (pvcEnabled) {
      volumesStrategy.provision(osEnv, identity);
    }

    // 3 stage - add OpenShift env items
    restartPolicyRewriter.provision(osEnv, identity);
    uniqueNamesProvisioner.provision(osEnv, identity);
    routeTlsProvisioner.provision(osEnv, identity);
    ramLimitProvisioner.provision(osEnv, identity);
    podTerminationGracePeriodProvisioner.provision(osEnv, identity);
    imagePullSecretProvisioner.provision(osEnv, identity);
    proxySettingsProvisioner.provision(osEnv, identity);
    serviceAccountProvisioner.provision(osEnv, identity);
    certificateProvisioner.provision(osEnv, identity);
    vcsSshKeysProvisioner.provision(osEnv, identity);
    gitUserProfileProvisioner.provision(osEnv, identity);
    LOG.debug(
        "Provisioning OpenShift environment done for workspace '{}'", identity.getWorkspaceId());
  }
}
