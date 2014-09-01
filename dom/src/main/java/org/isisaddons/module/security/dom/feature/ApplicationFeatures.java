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

import java.util.*;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.core.metamodel.facets.all.hide.HiddenFacet;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjector;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjectorAware;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpiAware;
import org.apache.isis.core.metamodel.spec.feature.Contributed;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.ObjectMember;

@DomainService(repositoryFor = ApplicationFeature.class)
public class ApplicationFeatures implements SpecificationLoaderSpiAware, ServicesInjectorAware {

    public String iconName() {
        return "applicationFeature";
    }

    //region > caches
    SortedMap<ApplicationFeatureId, ApplicationFeature> packageFeatures = Maps.newTreeMap();
    private SortedMap<ApplicationFeatureId, ApplicationFeature> classFeatures = Maps.newTreeMap();
    private SortedMap<ApplicationFeatureId, ApplicationFeature> memberFeatures = Maps.newTreeMap();
    private SortedMap<ApplicationFeatureId, ApplicationFeature> propertyFeatures = Maps.newTreeMap();
    private SortedMap<ApplicationFeatureId, ApplicationFeature> collectionFeatures = Maps.newTreeMap();
    private SortedMap<ApplicationFeatureId, ApplicationFeature> actionFeatures = Maps.newTreeMap();
    //endregion

    //region > init
    @Programmatic
    @PostConstruct
    public void init() {
        final Collection<ObjectSpecification> specifications =primeMetaModel();
        createApplicationFeaturesFor(specifications);
    }

    private Collection<ObjectSpecification> primeMetaModel() {
        final List<Object> services = servicesInjector.getRegisteredServices();
        for (Object service : services) {
            specificationLoader.loadSpecification(service.getClass());
        }
        return specificationLoader.allSpecifications();
    }

    private void createApplicationFeaturesFor(Collection<ObjectSpecification> specifications) {
        // take copy to avoid ConcurrentModificationException
        final List<ObjectSpecification> objectSpecifications = Lists.newArrayList(specifications);
        for (ObjectSpecification spec : objectSpecifications) {
            createApplicationFeaturesFor(spec);
        }
    }

    void createApplicationFeaturesFor(ObjectSpecification spec) {
        if (exclude(spec)) {
            return;
        }

        final List<ObjectAssociation> properties = spec.getAssociations(Contributed.INCLUDED, ObjectAssociation.Filters.PROPERTIES);
        final List<ObjectAssociation> collections = spec.getAssociations(Contributed.INCLUDED, ObjectAssociation.Filters.COLLECTIONS);
        final List<ObjectAction> actions = spec.getObjectActions(Contributed.INCLUDED);

        if(properties.isEmpty() && collections.isEmpty() && actions.isEmpty()) {
            return;
        }

        final String fullIdentifier = spec.getFullIdentifier();
        final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass(fullIdentifier);

        // add class to our map
        // (later on it may get removed if the class turns out to have no features,
        // but we require it in the map for the next bit).
        final ApplicationFeature classFeature = newFeature(classFeatureId);
        classFeatures.put(classFeatureId, classFeature);

        // add members
        boolean addedMembers = false;
        for (ObjectAssociation property : properties) {
            addedMembers = newProperty(classFeatureId, property) || addedMembers;
        }
        for (ObjectAssociation collection : collections) {
            addedMembers = newCollection(classFeatureId, collection) || addedMembers;
        }
        for (ObjectAction act : actions) {
            addedMembers = newAction(classFeatureId, act, act.getSemantics()) || addedMembers;
        }

        if(!addedMembers) {
            // remove this class feature, since it turned out to have no members
            classFeatures.remove(classFeatureId);
            return;
        } else {
            // leave the class as is and (as there were indeed members for this class)
            // and add all of its parent packages
            ApplicationFeatureId classParentPackageId = addClassParent(classFeatureId);
            addParents(classParentPackageId);
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
            parentPackage = newPackage(parentPackageId);
        }
        return parentPackage;
    }

    private ApplicationFeature newPackage(ApplicationFeatureId packageId) {
        ApplicationFeature parentPackage = newFeature(packageId);
        packageFeatures.put(packageId, parentPackage);
        return parentPackage;
    }

    private boolean newProperty(final ApplicationFeatureId classFeatureId, final ObjectMember objectMember) {
        return newMember(classFeatureId, objectMember, ApplicationMemberType.PROPERTY, null);
    }

    private boolean newCollection(final ApplicationFeatureId classFeatureId, final ObjectMember objectMember) {
        return newMember(classFeatureId, objectMember, ApplicationMemberType.COLLECTION, null);
    }

    private boolean newAction(final ApplicationFeatureId classFeatureId, final ObjectMember objectMember, ActionSemantics.Of actionSemantics) {
        return newMember(classFeatureId, objectMember, ApplicationMemberType.ACTION, actionSemantics);
    }

    private boolean newMember(final ApplicationFeatureId classFeatureId, final ObjectMember objectMember, ApplicationMemberType memberType, ActionSemantics.Of actionSemantics) {
        if(objectMember.isAlwaysHidden()) {
            return false;
        }
        newMember(classFeatureId, objectMember.getId(), memberType, actionSemantics);
        return true;
    }

    private void newMember(ApplicationFeatureId classFeatureId, String memberId, ApplicationMemberType memberType, ActionSemantics.Of actionSemantics) {
        final ApplicationFeatureId featureId = ApplicationFeatureId.newMember(classFeatureId.getFullyQualifiedName(), memberId);

        final ApplicationFeature memberFeature = newFeature(featureId);
        memberFeature.setMemberType(memberType);
        if(actionSemantics != null) {
            memberFeature.setActionSemantics(actionSemantics);
        }
        memberFeatures.put(featureId, memberFeature);

        // also cache per memberType
        featuresMapFor(memberType).put(featureId, memberFeature);

        final ApplicationFeature classFeature = findClass(classFeatureId);
        classFeature.addToMembers(featureId, memberType);
    }

    private SortedMap<ApplicationFeatureId, ApplicationFeature> featuresMapFor(ApplicationMemberType memberType) {
        switch (memberType) {
            case PROPERTY:
                return propertyFeatures;
            case COLLECTION:
                return collectionFeatures;
            default: // case ACTION:
                return actionFeatures;
        }
    }

    private ApplicationFeature newFeature(ApplicationFeatureId featureId) {
        final ApplicationFeature feature = container.newTransientInstance(ApplicationFeature.class);
        feature.setFeatureId(featureId);
        return feature;
    }


    protected boolean exclude(ObjectSpecification spec) {
        return spec.isAbstract() ||
                isBuiltIn(spec) ||
                isHidden(spec) ||
                isFixtureScript(spec) ||
                isSuperClassOfService(spec);
    }

    private boolean isFixtureScript(ObjectSpecification spec) {
        return FixtureScript.class.isAssignableFrom(spec.getCorrespondingClass());
    }

    /**
     * Ignore the (strict) superclasses of any services.
     *
     * <p>
     *     For example, we want to ignore <code>ExceptionRecognizerComposite</code> because there is no service
     *     of that type (only of subtypes of that).
     * </p>
     */
    private boolean isSuperClassOfService(ObjectSpecification spec) {
        final List<Object> registeredServices = servicesInjector.getRegisteredServices();

        final Class<?> specClass = spec.getCorrespondingClass();

        // is this class a supertype or the actual type of one of the services?
        boolean serviceCls = false;
        for (Object registeredService : registeredServices) {
            final Class<?> serviceClass = registeredService.getClass();
            if(specClass.isAssignableFrom(serviceClass)) {
                serviceCls = true;
            }
        }
        if(!serviceCls) {
            return false;
        }

        // yes it is.  In which case, is it the actual concrete class of one of those services?
        for (Object registeredService : registeredServices) {
            final Class<?> serviceClass = registeredService.getClass();
            if(serviceClass.isAssignableFrom(specClass)) {
                return false;
            }
        }
        // couldn't find a service of exactly this type, so ignore the spec.
        return true;
    }

    protected boolean isHidden(ObjectSpecification spec) {
        final HiddenFacet facet = spec.getFacet(HiddenFacet.class);
        return facet != null &&
                !facet.isNoop() &&
                (facet.where() == Where.EVERYWHERE || facet.where() == Where.ANYWHERE) &&
                facet.when() == When.ALWAYS;
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

    //region > allFeatures, allPackages, allClasses, allMembers
    @Programmatic
    public Collection<ApplicationFeature> allFeatures(ApplicationFeatureType featureType) {
        if(featureType == null) {
            return Collections.emptyList();
        }
        switch (featureType) {
            case PACKAGE:
                return allPackages();
            case CLASS:
                return allClasses();
            case MEMBER:
                return allMembers();
        }
        throw new IllegalArgumentException("Unknown feature type " + featureType);
    }

    @Programmatic
    public Collection<ApplicationFeature> allPackages() {
        return packageFeatures.values();
    }

    @Programmatic
    public Collection<ApplicationFeature> allClasses() {
        return classFeatures.values();
    }

    @Programmatic
    public Collection<ApplicationFeature> allMembers() {
        return memberFeatures.values();
    }
    //endregion

    //region > packageNamesContainingClasses, classNamesContainedIn, memberNamesOf
    /**
     *
     * @param memberType - additionally classes containing members of specified type (can be null).
     */
    @Programmatic
    public List<String> packageNamesContainingClasses(ApplicationMemberType memberType) {
        final Collection<ApplicationFeature> packages = allFeatures(ApplicationFeatureType.PACKAGE);
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                packages,
                                ApplicationFeature.Predicates.packageContainingClasses(memberType, this)
                        ),
                        ApplicationFeature.Functions.GET_FQN));
    }

    @Programmatic
    public List<String> classNamesContainedIn(String packageFqn, ApplicationMemberType memberType) {
        final ApplicationFeatureId packageId = ApplicationFeatureId.newPackage(packageFqn);
        final ApplicationFeature pkg = findPackage(packageId);
        if(pkg == null) {
            return Collections.emptyList();
        }
        final SortedSet<ApplicationFeatureId> contents = pkg.getContents();
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                contents,
                                ApplicationFeatureId.Predicates.isClassContaining(memberType, this)),
                        ApplicationFeatureId.Functions.GET_CLASS_NAME));
    }

    @Programmatic
    public List<String> memberNamesOf(String packageFqn, String className, ApplicationMemberType memberType) {
        final ApplicationFeatureId classId = ApplicationFeatureId.newClass(packageFqn + "." + className);
        final ApplicationFeature cls = findClass(classId);
        if(cls == null) {
            return Collections.emptyList();
        }
        final SortedSet<ApplicationFeatureId> featureIds = cls.membersOf(memberType);
        return Lists.newArrayList(
                Iterables.transform(featureIds, ApplicationFeatureId.Functions.GET_MEMBER)
        );
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
