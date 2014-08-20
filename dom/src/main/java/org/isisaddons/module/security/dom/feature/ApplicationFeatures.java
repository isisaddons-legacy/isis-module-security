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
package org.isisaddons.module.security.dom.feature;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.google.common.collect.Maps;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjector;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjectorAware;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpiAware;
import org.apache.isis.core.metamodel.spec.feature.Contributed;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.ObjectMember;

@DomainService
public class ApplicationFeatures implements SpecificationLoaderSpiAware, ServicesInjectorAware {

    //region > caches
    SortedMap<ApplicationFeatureId, ApplicationFeature> packageFeatures = Maps.newTreeMap();
    private SortedMap<ApplicationFeatureId, ApplicationFeature> classFeatures = Maps.newTreeMap();
    private SortedMap<ApplicationFeatureId, ApplicationFeature> memberFeatures = Maps.newTreeMap();
    //endregion

    //region > init
    @Programmatic
    @PostConstruct
    public void init() {
        primeMetaModel();
        createApplicationFeatures();
    }

    private void primeMetaModel() {
        final List<Object> services = servicesInjector.getRegisteredServices();
        for (Object service : services) {
            specificationLoader.loadSpecification(service.getClass());
        }
    }

    void createApplicationFeatures() {

        final Collection<ObjectSpecification> specifications = specificationLoader.allSpecifications();

        for (ObjectSpecification spec : specifications) {
            load(spec);
        }
    }

    void load(ObjectSpecification spec) {
        if (exclude(spec)) {
            return;
        }

        final String fullIdentifier = spec.getFullIdentifier();
        final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass(fullIdentifier);

        final ApplicationFeature classFeature = newFeature(classFeatureId);
        classFeatures.put(classFeatureId, classFeature);

        ApplicationFeatureId classParentPackageId = addClassParent(classFeatureId);

        addParents(classParentPackageId);

        final List<ObjectAssociation> properties = spec.getAssociations(Contributed.INCLUDED, ObjectAssociation.Filters.PROPERTIES);
        for (ObjectAssociation property : properties) {
            newMember(classFeatureId, property);
        }
        final List<ObjectAssociation> associations = spec.getAssociations(Contributed.INCLUDED, ObjectAssociation.Filters.COLLECTIONS);
        for (ObjectAssociation collection : associations) {
            newMember(classFeatureId, collection);
        }
        final List<ObjectAction> actions = spec.getObjectActions(Contributed.INCLUDED);
        for (ObjectAction act : actions) {
            newMember(classFeatureId, act);
        }
    }

    ApplicationFeatureId addClassParent(ApplicationFeatureId classFeatureId) {
        final ApplicationFeatureId parentPackageId = classFeatureId.getParentPackageId();

        ApplicationFeature parentPackage = findPackageElseCreate(parentPackageId);

        parentPackage.addToContents(classFeatureId);
        return parentPackageId;
    }

    void addParents(final ApplicationFeatureId classOrPackageId) {
        ApplicationFeatureId parentPackageId = classOrPackageId.getParentPackageId();
        if(parentPackageId == null) {
            return;
        }

        ApplicationFeature parentPackage = findPackageElseCreate(parentPackageId);

        // add this feature as part of the contents of its parent
        parentPackage.addToContents(classOrPackageId);

        // and recurse up
        addParents(parentPackageId);
    }

    private ApplicationFeature findPackageElseCreate(ApplicationFeatureId parentPackageId) {
        ApplicationFeature parentPackage = findPackage(parentPackageId);
        if (parentPackage == null) {
            // new package, so add it
            parentPackage = newPackage(parentPackageId);
        }
        return parentPackage;
    }

    private ApplicationFeature newPackage(ApplicationFeatureId packageId) {
        ApplicationFeature parentPackage = newFeature(packageId);
        packageFeatures.put(packageId, parentPackage);
        return parentPackage;
    }


    private void newMember(final ApplicationFeatureId classFeatureId, final ObjectMember objectMember) {
        newMember(classFeatureId, objectMember.getName());
    }

    private void newMember(ApplicationFeatureId classFeatureId, String memberName) {
        final ApplicationFeatureId memberId = ApplicationFeatureId.newMember(classFeatureId.getFullyQualifiedName(), memberName);
        final ApplicationFeature memberFeature = newFeature(memberId);
        memberFeatures.put(memberId, memberFeature);

        final ApplicationFeature classFeature = findClass(classFeatureId);
        classFeature.addToMembers(memberId);
    }

    private ApplicationFeature newFeature(ApplicationFeatureId featureId) {
        final ApplicationFeature feature = container.newTransientInstance(ApplicationFeature.class);
        feature.setFeatureId(featureId);
        return feature;
    }


    protected boolean exclude(ObjectSpecification spec) {
        return isBuiltIn(spec) || spec.isAbstract();
    }

    protected boolean isBuiltIn(ObjectSpecification spec) {
        final String className = spec.getFullIdentifier();
        return className.startsWith("java") || className.startsWith("org.joda");
    }
    //endregion

    //region > packageFeatures, classFeatures, memberFeatures
    @Programmatic
    public ApplicationFeature findFeature(ApplicationFeatureId featureId) {
        switch (featureId.getType()) {
            case PACKAGE:
                return findPackage(featureId);
            case CLASS:
                return findClass(featureId);
            case MEMBER:
                return findMember(featureId);
        }
        throw new IllegalArgumentException("Feature has unknown feature type " + featureId.getType());
    }

    @Programmatic
    public ApplicationFeature findPackage(final ApplicationFeatureId featureId) {
        return packageFeatures.get(featureId);
    }

    @Programmatic
    public ApplicationFeature findClass(final ApplicationFeatureId featureId) {
        return classFeatures.get(featureId);
    }

    @Programmatic
    public ApplicationFeature findMember(final ApplicationFeatureId featureId) {
        return memberFeatures.get(featureId);
    }

    //endregion

    //region > allPackages
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public Collection<ApplicationFeature> allPackages() {
        return packageFeatures.values();
    }
    //endregion

    //region > allClasses
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public Collection<ApplicationFeature> allClasses() {
        return classFeatures.values();
    }
    //endregion

    //region > allMembers
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public Collection<ApplicationFeature> allMembers() {
        return memberFeatures.values();
    }
    //endregion

    // //////////////////////////////////////

    //region  > services (injected)
    @Inject
    DomainObjectContainer container;

    private SpecificationLoaderSpi specificationLoader;

    @Programmatic
    @Override
    public void setSpecificationLoaderSpi(SpecificationLoaderSpi specificationLoader) {
        this.specificationLoader = specificationLoader;
    }

    private ServicesInjector servicesInjector;

    @Override
    public void setServicesInjector(ServicesInjector servicesInjector) {
        this.servicesInjector = servicesInjector;
    }
    //endregion

}
