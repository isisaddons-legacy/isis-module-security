/*
 *  Copyright 2014 Dan Haywood
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
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeature;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureId;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureRepositoryDefault;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureType;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ApplicationPermission.class
)
public class ApplicationPermissionRepository {


    //region > findByRole (programmatic)
    @Programmatic
    public List<ApplicationPermission> findByRoleCached(final ApplicationRole role) {
        return queryResultsCache.execute(new Callable<List<ApplicationPermission>>() {
            @Override
            public List<ApplicationPermission> call() throws Exception {
                return findByRole(role);
            }
        }, ApplicationPermissionRepository.class, "findByRoleCached", role);
    }

    @Programmatic
    public List<ApplicationPermission> findByRole(final ApplicationRole role) {
        return container.allMatches(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByRole",
                        "role", role));
    }
    //endregion

    //region > findByUser (programmatic)
    @Programmatic
    public List<ApplicationPermission> findByUserCached(final ApplicationUser user) {
        return queryResultsCache.execute(new Callable<List<ApplicationPermission>>() {
            @Override public List<ApplicationPermission> call() throws Exception {
                return findByUser(user);
            }
        }, ApplicationPermissionRepository.class, "findByUserCached", user);
    }

    @Programmatic
    public List<ApplicationPermission> findByUser(final ApplicationUser user) {
        final String username = user.getUsername();
        return findByUser(username);
    }

    private List<ApplicationPermission> findByUser(final String username) {
        return container.allMatches(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByUser",
                        "username", username));
    }
    //endregion

    //region > findByUserAndPermissionValue (programmatic)
    /**
     * Uses the {@link QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     */
    @Programmatic
    public ApplicationPermission findByUserAndPermissionValue(final String username, final ApplicationPermissionValue permissionValue) {



        // obtain all permissions for this user, map by its value, and
        // put into query cache (so that this method can be safely called in a tight loop)
        final Map<ApplicationPermissionValue, List<ApplicationPermission>> permissions =
            queryResultsCache.execute(new Callable<Map<ApplicationPermissionValue, List<ApplicationPermission>>>() {
                @Override
                public Map<ApplicationPermissionValue, List<ApplicationPermission>> call() throws Exception {

                    final List<ApplicationPermission> applicationPermissions = findByUser(username);
                    final ImmutableListMultimap<ApplicationPermissionValue, ApplicationPermission> index = Multimaps
                            .index(applicationPermissions, ApplicationPermission.Functions.AS_VALUE);

                    return Multimaps.asMap(index);
                }
                // note: it is correct that only username (and not permissionValue) is the key
                // (we are obtaining all the perms for this user)
            }, ApplicationPermissionRepository.class, "findByUserAndPermissionValue", username);

        // now simply return the permission from the required value (if it exists)
        final List<ApplicationPermission> applicationPermissions = permissions.get(permissionValue);
        return applicationPermissions != null && !applicationPermissions.isEmpty()
                    ? applicationPermissions.get(0)
                    : null;
    }
    //endregion

    //region > findByRoleAndRuleAndFeatureType (programmatic)
    @Programmatic
    public List<ApplicationPermission> findByRoleAndRuleAndFeatureTypeCached(
            final ApplicationRole role, final ApplicationPermissionRule rule,
            final ApplicationFeatureType type) {
        return queryResultsCache.execute(new Callable<List<ApplicationPermission>>() {
            @Override public List<ApplicationPermission> call() throws Exception {
                return findByRoleAndRuleAndFeatureType(role, rule, type);
            }
        }, ApplicationPermissionRepository.class, "findByRoleAndRuleAndFeatureTypeCached", role, rule, type);
    }

    @Programmatic
    public List<ApplicationPermission> findByRoleAndRuleAndFeatureType(
            final ApplicationRole role, final ApplicationPermissionRule rule,
            final ApplicationFeatureType type) {
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
    public ApplicationPermission findByRoleAndRuleAndFeatureCached(
            final ApplicationRole role,
            final ApplicationPermissionRule rule,
            final ApplicationFeatureType type,
            final String featureFqn) {
        return queryResultsCache.execute(new Callable<ApplicationPermission>() {
            @Override public ApplicationPermission call() throws Exception {
                return findByRoleAndRuleAndFeature(role, rule, type, featureFqn);
            }
        }, ApplicationPermissionRepository.class, "findByRoleAndRuleAndFeatureCached", role, rule, type, featureFqn);
    }

    @Programmatic
    public ApplicationPermission findByRoleAndRuleAndFeature(
            final ApplicationRole role,
            final ApplicationPermissionRule rule, final ApplicationFeatureType type, final String featureFqn) {
        return container.firstMatch(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByRoleAndRuleAndFeature",
                        "role", role,
                        "rule", rule,
                        "featureType", type,
                        "featureFqn", featureFqn ));
    }
    //endregion

    //region > findByFeature (programmatic)
    @Programmatic
    public List<ApplicationPermission> findByFeatureCached(final ApplicationFeatureId featureId) {
        return queryResultsCache.execute(new Callable<List<ApplicationPermission>>() {
            @Override public List<ApplicationPermission> call() throws Exception {
                return findByFeature(featureId);
            }
        }, ApplicationPermissionRepository.class, "findByFeatureCached", featureId);
    }

    @Programmatic
    public List<ApplicationPermission> findByFeature(final ApplicationFeatureId featureId) {
        return container.allMatches(
                new QueryDefault<>(
                        ApplicationPermission.class, "findByFeature",
                        "featureType", featureId.getType(),
                        "featureFqn", featureId.getFullyQualifiedName()));
    }
    //endregion

    //region > newPermission (programmatic)

    @Programmatic
    public ApplicationPermission newPermission(
            final ApplicationRole role,
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final ApplicationFeatureType featureType,
            final String featureFqn) {
        final ApplicationFeatureId featureId = ApplicationFeatureId.newFeature(featureType, featureFqn);
        final ApplicationFeature feature = applicationFeatureRepository.findFeature(featureId);
        if(feature == null) {
            container.warnUser("No such " + featureType.name().toLowerCase() + ": " + featureFqn);
            return null;
        }
        return newPermissionNoCheck(role, rule, mode, featureType, featureFqn);
    }

    @Programmatic
    public ApplicationPermission newPermissionNoCheck(
            final ApplicationRole role,
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final ApplicationFeatureType featureType,
            final String featureFqn) {
        ApplicationPermission permission = findByRoleAndRuleAndFeature(role, rule, featureType, featureFqn);
        if (permission != null) {
            return permission;
        }
        permission = getApplicationPermissionFactory().newApplicationPermission();
        permission.setRole(role);
        permission.setRule(rule);
        permission.setMode(mode);
        permission.setFeatureType(featureType);
        permission.setFeatureFqn(featureFqn);
        container.persistIfNotAlready(permission);
        return permission;
    }

    @Programmatic
    public ApplicationPermission newPermission(
            final ApplicationRole role,
            final ApplicationPermissionRule rule,
            final ApplicationPermissionMode mode,
            final String featurePackage,
            final String featureClassName,
            final String featureMemberName) {
        final ApplicationFeatureId featureId = ApplicationFeatureId.newFeature(featurePackage, featureClassName, featureMemberName);
        final ApplicationFeatureType featureType = featureId.getType();
        final String featureFqn = featureId.getFullyQualifiedName();

        final ApplicationFeature feature = applicationFeatureRepository.findFeature(featureId);
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
    @Programmatic
    public List<ApplicationPermission> allPermissions() {
        return container.allInstances(ApplicationPermission.class);
    }
    //endregion

    //region  >  (injected)
    @Inject
    DomainObjectContainer container;
    @Inject
    ApplicationFeatureRepositoryDefault applicationFeatureRepository;

    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in the {@link #getApplicationPermissionFactory() accessor}.
     */
    @Inject
    ApplicationPermissionFactory applicationPermissionFactory;

    private ApplicationPermissionFactory getApplicationPermissionFactory() {
        return applicationPermissionFactory != null
                ? applicationPermissionFactory
                : (applicationPermissionFactory = new ApplicationPermissionFactory.Default(container));
    }

    @Inject
    QueryResultsCache queryResultsCache;
    //endregion

}
