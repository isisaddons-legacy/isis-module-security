/*
 *  Copyright 2014 Jeroen van der Wal
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.security.dom.permission;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.security.dom.actor.ApplicationRole;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.query.QueryDefault;

@DomainService(repositoryFor = ApplicationPermission.class)
public class ApplicationPermissions {

    public String iconName() {
        return "applicationPermission";
    }


    //region > findByRole (programmatic)
    @Programmatic
    public List<ApplicationPermission> findByRole(final ApplicationRole role) {
        return container.allMatches(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByRole",
                        "role", role));
    }
    //endregion

    //region > findByRoleAndRuleAndFeatureType (programmatic)
    @Programmatic
    public List<ApplicationPermission> findByRoleAndRuleAndFeatureType(
            final ApplicationPermissionRule rule,
            final ApplicationFeatureType type,
            final ApplicationRole role) {
        return container.allMatches(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByRoleAndRuleAndFeatureType",
                        "role", role,
                        "rule", rule,
                        "featureType", type));
    }
    //endregion

    //region > findByRoleAndRuleAndFeature (programmatic)
    @Programmatic
    public ApplicationPermission findByRoleAndRuleAndFeature(ApplicationPermissionRule rule, ApplicationFeatureType type, String featureFqn) {
        return container.firstMatch(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByRoleAndRuleAndFeature",
                        "role", this,
                        "rule", rule,
                        "featureType", type,
                        "featureFqn", featureFqn ));
    }
    //endregion

    //region > findByFeature (programmatic)
    @Programmatic
    public List<ApplicationPermission> findByFeature(ApplicationFeatureId featureId) {
        return container.allMatches(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByFeature",
                        "featureType", featureId.getType(),
                        "featureFqn", featureId.getFullyQualifiedName()));
    }
    //endregion

    //region > newPermission (programmatic)

    @Programmatic
    public void newPermission(
            final ApplicationRole role,
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final ApplicationFeatureType featureType,
            final String featureFqn) {
        final ApplicationFeatureId featureId = ApplicationFeatureId.newFeature(featureType, featureFqn);
        final ApplicationFeature feature = applicationFeatures.findFeature(featureId);
        if(feature == null) {
            container.warnUser("No such " + featureType.name().toLowerCase() + ": " + featureFqn);
            return;
        }
        final ApplicationPermission permission = container.newTransientInstance(ApplicationPermission.class);
        permission.setRole(role);
        permission.setRule(rule);
        permission.setMode(mode);
        permission.setFeatureType(featureType);
        permission.setFeatureFqn(featureFqn);
        container.persistIfNotAlready(permission);
    }

    @Programmatic
    public ApplicationPermission newPermission(
            final ApplicationRole role,
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String featurePackage,
            final @Optional String featureClassName,
            final @Optional String featureMemberName) {
        ApplicationFeatureId featureId = ApplicationFeatureId.newFeature(featurePackage, featureClassName, featureMemberName);
        final ApplicationFeatureType featureType = featureId.getType();
        final String featureFqn = featureId.getFullyQualifiedName();

        final ApplicationFeature feature = applicationFeatures.findFeature(featureId);
        if(feature == null) {
            container.warnUser("No such " + featureType.name().toLowerCase() + ": " + featureFqn);
            return null;
        }

        final ApplicationPermission permission = container.newTransientInstance(ApplicationPermission.class);
        permission.setRole(role);
        permission.setRule(rule);
        permission.setMode(mode);
        permission.setFeatureType(featureType);
        permission.setFeatureFqn(featureFqn);
        container.persistIfNotAlready(permission);

        return permission;
    }
    //endregion

    //region > allPermission (action)
    @Prototype
    @ActionSemantics(ActionSemantics.Of.SAFE)
    @MemberOrder(name = "Security", sequence = "60.9")
    public List<ApplicationPermission> allPermissions() {
        return container.allInstances(ApplicationPermission.class);
    }
    //endregion

    //region  >  (injected)
    @Inject
    DomainObjectContainer container;
    @Inject
    ApplicationFeatures applicationFeatures;

    //endregion
}
