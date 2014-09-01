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
package org.isisaddons.module.security.app.user;

import java.nio.charset.Charset;
import java.util.Iterator;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;
import org.isisaddons.module.security.app.feature.ApplicationFeatureViewModel;
import org.isisaddons.module.security.dom.feature.*;
import org.isisaddons.module.security.dom.permission.*;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUsers;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.util.ObjectContracts;

/**
 * View model identified by {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId} and backed by an {@link org.isisaddons.module.security.dom.feature.ApplicationFeature}.
 */
@MemberGroupLayout(
        columnSpans = {6,0,6,0},
        left = {"Permission"},
        right= {"Cause"}
)
@Bookmarkable
public class UserPermissionViewModel implements ViewModel {

    //region > constructors, factory methods
    public static UserPermissionViewModel newViewModel(
            final ApplicationFeatureId featureId, final ApplicationUser user, final ApplicationPermissionValueSet.Evaluation viewingEvaluation, final ApplicationPermissionValueSet.Evaluation changingEvaluation, final DomainObjectContainer container) {
        return container.newViewModelInstance(UserPermissionViewModel.class, asEncodedString(featureId, user.getUsername(), viewingEvaluation, changingEvaluation));
    }

    public UserPermissionViewModel() {
    }
    //endregion

    //region > identification
    public String title() {
        return getVerb() + " " + getFeatureId().getFullyQualifiedName();
    }

    public String iconName() {
        return "userPermission";
    }
    //endregion

    //region > ViewModel impl
    @Override
    public String viewModelMemento() {
        return asEncodedString();
    }

    @Override
    public void viewModelInit(String encodedMemento) {
        parseEncoded(encodedMemento);
    }

    private static String asEncodedString(ApplicationFeatureId featureId, String username, ApplicationPermissionValueSet.Evaluation viewingEvaluation, ApplicationPermissionValueSet.Evaluation changingEvaluation) {
        return base64UrlEncode(asString(featureId, username, viewingEvaluation, changingEvaluation));
    }

    private static String asString(ApplicationFeatureId featureId, String username, ApplicationPermissionValueSet.Evaluation viewingEvaluation, ApplicationPermissionValueSet.Evaluation changingEvaluation) {

        final boolean viewingEvaluationGranted = viewingEvaluation.isGranted();
        final ApplicationPermissionValue viewingEvaluationCause = viewingEvaluation.getCause();
        final ApplicationFeatureId viewingEvaluationCauseFeatureId = viewingEvaluationCause != null? viewingEvaluationCause.getFeatureId(): null;

        final boolean changingEvaluationGranted = changingEvaluation.isGranted();
        final ApplicationPermissionValue changingEvaluationCause = changingEvaluation.getCause();
        final ApplicationFeatureId changingEvaluationCauseFeatureId = changingEvaluationCause != null? changingEvaluationCause.getFeatureId(): null;
        return Joiner.on(":").join(
                username,

                viewingEvaluationGranted,
                viewingEvaluationCauseFeatureId != null? viewingEvaluationCauseFeatureId.getType(): "",
                viewingEvaluationCauseFeatureId != null? viewingEvaluationCauseFeatureId.getFullyQualifiedName(): "",
                viewingEvaluationCause != null? viewingEvaluationCause.getRule(): "",
                viewingEvaluationCause != null? viewingEvaluationCause.getMode(): "",

                changingEvaluationGranted,
                changingEvaluationCauseFeatureId != null? changingEvaluationCauseFeatureId.getType(): "",
                changingEvaluationCauseFeatureId != null? changingEvaluationCauseFeatureId.getFullyQualifiedName(): "",
                changingEvaluationCause != null? changingEvaluationCause.getRule(): "",
                changingEvaluationCause != null? changingEvaluationCause.getMode(): "",

                featureId.getType(), featureId.getFullyQualifiedName());
    }

    private void parseEncoded(String encodedString) {
        parse(base64UrlDecode(encodedString));
    }

    private void parse(String asString) {
        final Iterator<String> iterator = Splitter.on(":").split(asString).iterator();

        this.username = iterator.next();

        this.viewingGranted = Boolean.valueOf(iterator.next());
        final String viewingEvaluationCauseFeatureIdType = iterator.next();
        ApplicationFeatureType viewingEvaluationFeatureIdType =  !viewingEvaluationCauseFeatureIdType.isEmpty() ? ApplicationFeatureType.valueOf(viewingEvaluationCauseFeatureIdType) : null;
        String viewingEvaluationFeatureFqn = iterator.next();
        this.viewingFeatureId = viewingEvaluationFeatureIdType != null? new ApplicationFeatureId(viewingEvaluationFeatureIdType,viewingEvaluationFeatureFqn) : null;

        final String viewingEvaluationCauseRule = iterator.next();
        this.viewingRule = !viewingEvaluationCauseRule.isEmpty()? ApplicationPermissionRule.valueOf(viewingEvaluationCauseRule): null;
        final String viewingEvaluationCauseMode = iterator.next();
        this.viewingMode = !viewingEvaluationCauseMode.isEmpty()? ApplicationPermissionMode.valueOf(viewingEvaluationCauseMode): null;


        this.changingGranted = Boolean.valueOf(iterator.next());
        final String changingEvaluationCauseFeatureIdType = iterator.next();
        ApplicationFeatureType changingEvaluationFeatureIdType =  !changingEvaluationCauseFeatureIdType.isEmpty() ? ApplicationFeatureType.valueOf(changingEvaluationCauseFeatureIdType) : null;
        String changingEvaluationFeatureFqn = iterator.next();
        this.changingFeatureId = changingEvaluationFeatureIdType != null? new ApplicationFeatureId(changingEvaluationFeatureIdType,changingEvaluationFeatureFqn) : null;

        final String changingEvaluationCauseRule = iterator.next();
        this.changingRule = !changingEvaluationCauseRule.isEmpty()? ApplicationPermissionRule.valueOf(changingEvaluationCauseRule): null;
        final String changingEvaluationCauseMode = iterator.next();
        this.changingMode = !changingEvaluationCauseMode.isEmpty()? ApplicationPermissionMode.valueOf(changingEvaluationCauseMode): null;

        final ApplicationFeatureType type = ApplicationFeatureType.valueOf(iterator.next());
        this.featureId = new ApplicationFeatureId(type, iterator.next());
    }

    @Programmatic
    public String asEncodedString() {
        return asEncodedString(getFeatureId(), getUsername(), newEvaluation(viewingGranted, viewingFeatureId, viewingRule, viewingMode), newEvaluation(changingGranted, changingFeatureId, changingRule, changingMode));
    }

    private static ApplicationPermissionValueSet.Evaluation newEvaluation(boolean granted, ApplicationFeatureId featureId, ApplicationPermissionRule rule, ApplicationPermissionMode mode) {
        return new ApplicationPermissionValueSet.Evaluation(newPermissionValue(featureId, rule, mode), granted);
    }

    private static ApplicationPermissionValue newPermissionValue(ApplicationFeatureId featureId, ApplicationPermissionRule rule, ApplicationPermissionMode mode) {
        if(featureId == null || mode == null || rule == null) {
            return null;
        } else {
            return new ApplicationPermissionValue(featureId, rule, mode);
        }
    }

    private static String base64UrlDecode(String str) {
        final byte[] bytes = BaseEncoding.base64Url().decode(str);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    private static String base64UrlEncode(final String str) {
        byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
        return BaseEncoding.base64Url().encode(bytes);
    }

    //endregion

    //region > user (derived property, hidden in parented tables)

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(name = "Permission", sequence = "1")
    public ApplicationUser getUser() {
        return applicationUsers.findUserByUsername(getUsername());
    }

    private String username;
    @Programmatic
    public String getUsername() {
        return username;
    }
    //endregion

    //region > verb (derived property)

    private boolean viewingGranted;
    private boolean changingGranted;

    @MemberOrder(name="Permission", sequence = "2")
    public String getVerb() {
        return changingGranted
                ? "Can change"
                : viewingGranted
                ? "Can view"
                : "No access to";
    }
    //endregion

    //region > feature (derived property)

    @javax.jdo.annotations.NotPersistent
    @Disabled
    @Hidden(where=Where.REFERENCES_PARENT)
    @MemberOrder(name = "Permission",sequence = "4")
    public ApplicationFeatureViewModel getFeature() {
        if(getFeatureId() == null) {
            return null;
        }
        return ApplicationFeatureViewModel.newViewModel(getFeatureId(), container);
    }

    private ApplicationFeatureId featureId;

    @Programmatic
    public ApplicationFeatureId getFeatureId() {
        return featureId;
    }

    public void setFeatureId(ApplicationFeatureId applicationFeatureId) {
        this.featureId = applicationFeatureId;
    }

    //endregion

    //region > viewingPermission (derived property)
    private ApplicationFeatureId viewingFeatureId;
    private ApplicationPermissionMode viewingMode;
    private ApplicationPermissionRule viewingRule;

    @javax.jdo.annotations.NotPersistent
    @Disabled
    @Hidden(where=Where.REFERENCES_PARENT)
    @MemberOrder(name="Cause", sequence = "2.1")
    public ApplicationPermission getViewingPermission() {
        if(getViewingPermissionValue() == null) {
            return null;
        }
        return applicationPermissions.findByUserAndPermissionValue(username, getViewingPermissionValue());
    }

    private ApplicationPermissionValue getViewingPermissionValue() {
        if(viewingFeatureId == null) {
            return null;
        }
        return new ApplicationPermissionValue(viewingFeatureId, viewingRule, viewingMode);
    }

    //endregion

    //region > changingPermission (derived property)

    private ApplicationFeatureId changingFeatureId;
    private ApplicationPermissionMode changingMode;
    private ApplicationPermissionRule changingRule;


    @javax.jdo.annotations.NotPersistent
    @Disabled
    @Hidden(where=Where.REFERENCES_PARENT)
    @MemberOrder(name="Cause", sequence = "2.2")
    public ApplicationPermission getChangingPermission() {
        if(getChangingPermissionValue() == null) {
            return null;
        }
        return applicationPermissions.findByUserAndPermissionValue(username, getChangingPermissionValue());
    }

    private ApplicationPermissionValue getChangingPermissionValue() {
        if(changingFeatureId == null) {
            return null;
        }
        return new ApplicationPermissionValue(changingFeatureId, changingRule, changingMode);
    }

    //endregion

    //region > toString

    private final static String propertyNames = "user, featureId";

    @Override
    public String toString() {
        return ObjectContracts.toString(this, propertyNames);
    }
    //endregion

    //region > Functions

    public static class Functions {
        private Functions(){}
        public static Function<ApplicationFeature, UserPermissionViewModel> asViewModel(final ApplicationUser user, final DomainObjectContainer container) {
            return new Function<ApplicationFeature, UserPermissionViewModel>(){
                @Override
                public UserPermissionViewModel apply(ApplicationFeature input) {
                    final ApplicationPermissionValueSet permissionSet = user.getPermissionSet();
                    final ApplicationPermissionValueSet.Evaluation changingEvaluation = permissionSet.evaluate(input.getFeatureId(), ApplicationPermissionMode.CHANGING);
                    final ApplicationPermissionValueSet.Evaluation viewingEvaluation = permissionSet.evaluate(input.getFeatureId(), ApplicationPermissionMode.VIEWING);
                    return UserPermissionViewModel.newViewModel(input.getFeatureId(), user, viewingEvaluation, changingEvaluation, container);
                }
            };
        }
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatures applicationFeatures;

    @javax.inject.Inject
    ApplicationPermissions applicationPermissions;

    @javax.inject.Inject
    ApplicationUsers applicationUsers;
    //endregion

}
