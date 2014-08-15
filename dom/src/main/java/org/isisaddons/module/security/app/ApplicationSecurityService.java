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
package org.isisaddons.module.security.app;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpiAware;

@Named("Security")
@DomainService
public class ApplicationSecurityService extends AbstractFactoryAndRepository implements SpecificationLoaderSpiAware {

    public ApplicationSecurityManager manage() {
        return manage(null);
    }

    @Programmatic
    public ApplicationSecurityManager manage(ApplicationSecurityManager manager) {
        final String viewModelMemento = manager == null ? null : manager.viewModelMemento();
        return getContainer().newViewModelInstance(ApplicationSecurityManager.class, viewModelMemento);
    }


    // //////////////////////////////////////

    private SpecificationLoaderSpi specificationLoader;

    @Programmatic
    @Override
    public void setSpecificationLoaderSpi(SpecificationLoaderSpi specificationLoader) {
        this.specificationLoader = specificationLoader;
    }

    @Inject
    public ApplicationFeatures features;

}
