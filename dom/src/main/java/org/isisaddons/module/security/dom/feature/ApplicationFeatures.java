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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.memento.MementoService;
import org.apache.isis.core.metamodel.services.devutils.MetaModelRow;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpiAware;
import org.apache.isis.core.metamodel.spec.feature.*;

@DomainService
public class ApplicationFeatures implements SpecificationLoaderSpiAware  {

    private SortedSet<ApplicationFeature> packageFeatures = Sets.newTreeSet();
    private SortedSet<ApplicationFeature> classFeatures = Sets.newTreeSet();
    private SortedSet<ApplicationFeature> memberFeatures = Sets.newTreeSet();

    @ActionSemantics(ActionSemantics.Of.IDEMPOTENT)
    public void loadMetaModel() {

        final Collection<ObjectSpecification> specifications = specificationLoader.allSpecifications();

        for (ObjectSpecification spec : specifications) {
            if (exclude(spec)) {
                continue;
            }

            final String fullIdentifier = spec.getFullIdentifier();
            final ApplicationFeature classFeature = ApplicationFeature.newClass(fullIdentifier);

            ApplicationFeature classParentPackage = addClassParent(classFeature);

            addParents(classParentPackage);

            final List<ObjectAssociation> properties = spec.getAssociations(Contributed.INCLUDED, ObjectAssociation.Filters.PROPERTIES);
            for (ObjectAssociation property : properties) {
                final OneToOneAssociation otoa = (OneToOneAssociation) property;
                final ApplicationFeature memberFeature = ApplicationFeature.newMember(fullIdentifier, otoa.getName());
                memberFeatures.add(memberFeature);
            }
            final List<ObjectAssociation> associations = spec.getAssociations(Contributed.EXCLUDED, ObjectAssociation.Filters.COLLECTIONS);
            for (ObjectAssociation collection : associations) {
                final OneToManyAssociation otma = (OneToManyAssociation) collection;
                final ApplicationFeature memberFeature = ApplicationFeature.newMember(fullIdentifier, otma.getName());
                memberFeatures.add(memberFeature);
            }
            final List<ObjectAction> actions = spec.getObjectActions(Contributed.INCLUDED);
            for (ObjectAction act : actions) {
                final ApplicationFeature memberFeature = ApplicationFeature.newMember(fullIdentifier, act.getName());
                memberFeatures.add(memberFeature);
            }
        }
    }

    public ApplicationFeature addClassParent(ApplicationFeature classFeature) {
        ApplicationFeature parentPackage = classFeature.getParentPackage();
        if(packageFeatures.contains(parentPackage)) {
            // encountered this package already
            parentPackage = packageFeatures.tailSet(parentPackage).first();
        } else {
            // new package, so add it...
            packageFeatures.add(parentPackage);
        }
        parentPackage.addToContents(classFeature);
        return parentPackage;
    }

    private void addParents(final ApplicationFeature pkg) {
        ApplicationFeature parentPackage = pkg.getParentPackage();
        if(parentPackage == null) {
            return;
        }

        if(packageFeatures.contains(parentPackage)) {
            // encountered this package already
            parentPackage = packageFeatures.tailSet(pkg).first();
        } else {
            // new package, so add it
            packageFeatures.add(pkg);
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


    // //////////////////////////////////////

    private SpecificationLoaderSpi specificationLoader;

    @Programmatic
    @Override
    public void setSpecificationLoaderSpi(SpecificationLoaderSpi specificationLoader) {
        this.specificationLoader = specificationLoader;
    }

    @javax.inject.Inject
    MementoService mementoService;

}
