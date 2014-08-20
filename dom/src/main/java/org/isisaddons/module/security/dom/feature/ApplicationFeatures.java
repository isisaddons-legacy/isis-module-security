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
import java.util.SortedSet;
import javax.annotation.PostConstruct;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.memento.MementoService;
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

    
    //region > packageFeatures, classFeatures, memberFeatures

    SortedSet<ApplicationFeature> packageFeatures = Sets.newTreeSet();
    private SortedSet<ApplicationFeature> classFeatures = Sets.newTreeSet();
    private SortedSet<ApplicationFeature> memberFeatures = Sets.newTreeSet();
    private ServicesInjector servicesInjector;

    public ApplicationFeature findPackage(ApplicationFeature feature) {
        return findFeature(packageFeatures, feature);
    }

    ApplicationFeature findClass(ApplicationFeature feature) {
        return findFeature(classFeatures, feature);
    }

    ApplicationFeature findMember(ApplicationFeature feature) {
        return findFeature(memberFeatures, feature);
    }

    private static ApplicationFeature findFeature(SortedSet<ApplicationFeature> set, ApplicationFeature feature) {
        if(set.contains(feature)) {
            return set.tailSet(feature).first();
        }
        return null;
    }
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
        final ApplicationFeature classFeature = ApplicationFeature.newClass(fullIdentifier);
        classFeatures.add(classFeature);

        ApplicationFeature classParentPackage = addClassParent(classFeature);

        addParents(classParentPackage);

        final List<ObjectAssociation> properties = spec.getAssociations(Contributed.INCLUDED, ObjectAssociation.Filters.PROPERTIES);
        for (ObjectAssociation property : properties) {
            addMember(classFeature, property);
        }
        final List<ObjectAssociation> associations = spec.getAssociations(Contributed.INCLUDED, ObjectAssociation.Filters.COLLECTIONS);
        for (ObjectAssociation collection : associations) {
            addMember(classFeature, collection);
        }
        final List<ObjectAction> actions = spec.getObjectActions(Contributed.INCLUDED);
        for (ObjectAction act : actions) {
            addMember(classFeature, act);
        }
    }

    private void addMember(ApplicationFeature classFeature, ObjectMember objectMember) {
        final ApplicationFeature memberFeature = ApplicationFeature.newMember(classFeature.getFullyQualifiedName(), objectMember.getName());
        classFeature.addToMembers(memberFeature);
        memberFeatures.add(memberFeature);
    }

    ApplicationFeature addClassParent(ApplicationFeature classFeature) {
        ApplicationFeature parentPackage = classFeature.getParentPackage();
        final ApplicationFeature existingPackage = findPackage(parentPackage);
        if(existingPackage != null) {
            // encountered this package already
            parentPackage = existingPackage;
        } else {
            // new package, so add it...
            packageFeatures.add(parentPackage);
        }
        parentPackage.addToContents(classFeature);
        return parentPackage;
    }

    void addParents(final ApplicationFeature pkg) {
        ApplicationFeature parentPackage = pkg.getParentPackage();
        if(parentPackage == null) {
            return;
        }

        final ApplicationFeature existingPackage = findPackage(parentPackage);
        if(existingPackage != null) {
            // encountered this package already
            parentPackage = existingPackage;
        } else {
            // new package, so add it
            packageFeatures.add(parentPackage);
        }

        // add this pkg as part of the contents of its parent
        parentPackage.addToContents(pkg);

        // and recurse up
        addParents(parentPackage);
    }

    protected boolean exclude(ObjectSpecification spec) {
        return isBuiltIn(spec) || spec.isAbstract();
    }

    protected boolean isBuiltIn(ObjectSpecification spec) {
        final String className = spec.getFullIdentifier();
        return className.startsWith("java") || className.startsWith("org.joda");
    }
    //endregion

    //region > allPackages
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public SortedSet<ApplicationFeature> allPackages() {
        return packageFeatures;
    }
    //endregion

    //region > allClasses
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public SortedSet<ApplicationFeature> allClasses() {
        return classFeatures;
    }
    //endregion

    //region > allMembers
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public SortedSet<ApplicationFeature> allMembers() {
        return memberFeatures;
    }
    //endregion

    // //////////////////////////////////////

    private SpecificationLoaderSpi specificationLoader;

    @Programmatic
    @Override
    public void setSpecificationLoaderSpi(SpecificationLoaderSpi specificationLoader) {
        this.specificationLoader = specificationLoader;
    }

    @javax.inject.Inject
    MementoService mementoService;

    @Override
    public void setServicesInjector(ServicesInjector servicesInjector) {
        this.servicesInjector = servicesInjector;
    }

}
