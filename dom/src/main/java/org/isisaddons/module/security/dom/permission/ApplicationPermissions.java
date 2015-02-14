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
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.google.common.collect.Maps;
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

@DomainService(repositoryFor = ApplicationPermission.class)
@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.50"
)
public class ApplicationPermissions {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationPermissions, T> {
        public PropertyDomainEvent(final ApplicationPermissions source, final Identifier identifier) {
            super(source, identifier);
        }

        public PropertyDomainEvent(final ApplicationPermissions source, final Identifier identifier, final T oldValue, final T newValue) {
            super(source, identifier, oldValue, newValue);
        }
    }

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationPermissions, T> {
        public CollectionDomainEvent(final ApplicationPermissions source, final Identifier identifier, final Of of) {
            super(source, identifier, of);
        }

        public CollectionDomainEvent(final ApplicationPermissions source, final Identifier identifier, final Of of, final T value) {
            super(source, identifier, of, value);
        }
    }

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationPermissions> {
        public ActionDomainEvent(final ApplicationPermissions source, final Identifier identifier) {
            super(source, identifier);
        }

        public ActionDomainEvent(final ApplicationPermissions source, final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }

        public ActionDomainEvent(final ApplicationPermissions source, final Identifier identifier, final List<Object> arguments) {
            super(source, identifier, arguments);
        }
    }

    // //////////////////////////////////////

    //region > iconName

    public String iconName() {
        return "applicationPermission";
    }

    //endregion

    //region > init

    @Programmatic
    @PostConstruct
    public void init() {
        if(applicationPermissionFactory == null) {
            applicationPermissionFactory = new ApplicationPermissionFactory.Default(container);
        }
    }

    //endregion

    //region > findByRole (programmatic)
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
     * Uses the {@link org.apache.isis.applib.services.queryresultscache.QueryResultsCache} in order to support
     * multiple lookups from <code>org.isisaddons.module.security.app.user.UserPermissionViewModel</code>.
     */
    @Programmatic
    public ApplicationPermission findByUserAndPermissionValue(final String username, final ApplicationPermissionValue permissionValue) {

        // obtain all permissions for this user, map by its value, and
        // put into query cache (so that this method can be safely called in a tight loop)
        final Map<ApplicationPermissionValue, ApplicationPermission> permissions =
            queryResultsCache.execute(new Callable<Map<ApplicationPermissionValue, ApplicationPermission>>() {
                @Override
                public Map<ApplicationPermissionValue, ApplicationPermission> call() throws Exception {
                    final List<ApplicationPermission> applicationPermissions = findByUser(username);
                    return Maps.uniqueIndex(applicationPermissions, ApplicationPermission.Functions.AS_VALUE);
                }
            }, ApplicationPermissions.class, "findByUserAndPermissionValue", username);

        // now simply return the permission from the required value (if it exists)
        return permissions.get(permissionValue);
    }
    //endregion

    //region > findByRoleAndRuleAndFeatureType (programmatic)
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
    public ApplicationPermission findByRoleAndRuleAndFeature(final ApplicationRole role, final ApplicationPermissionRule rule, final ApplicationFeatureType type, final String featureFqn) {
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
        final ApplicationFeature feature = applicationFeatures.findFeature(featureId);
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
        permission = applicationPermissionFactory.newApplicationPermission();
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
    public static class AllPermissionsDomainEvent extends ActionDomainEvent {
        public AllPermissionsDomainEvent(final ApplicationPermissions source, final Identifier identifier, final Object... args) {
            super(source, identifier, args);
        }
    }

    @Action(
            domainEvent=AllPermissionsDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-list"
    )
    @MemberOrder(sequence = "60.9")
    public List<ApplicationPermission> allPermissions() {
        return container.allInstances(ApplicationPermission.class);
    }
    //endregion

    //region  >  (injected)
    @Inject
    DomainObjectContainer container;
    @Inject
    ApplicationFeatures applicationFeatures;
    @Inject
    QueryResultsCache queryResultsCache;

    /**
     * Will only be injected to if the programmer has supplied an implementation.  Otherwise
     * this class will install a default implementation in {@link #init()}.
     */
    @Inject
    ApplicationPermissionFactory applicationPermissionFactory;
    //endregion
}
