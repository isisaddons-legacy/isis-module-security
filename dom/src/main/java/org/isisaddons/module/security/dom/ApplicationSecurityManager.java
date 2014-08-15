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
package org.isisaddons.module.security.dom;

import java.util.List;

import javax.inject.Inject;

import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatures;
import org.isisaddons.module.security.service.ApplicationSecurityService;

import org.apache.isis.applib.AbstractViewModel;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.services.memento.MementoService;
import org.apache.isis.applib.services.memento.MementoService.Memento;

@Immutable
@Bookmarkable
public class ApplicationSecurityManager extends AbstractViewModel {

    @Override
    public String viewModelMemento() {
        final Memento memento = mementoService.create();
        // final Bookmark propertyBookmark =
        // getBookmarkService().bookmarkFor(getPackageName());
        memento.set("packageName", getPackageName());
        return memento.asString();
    }

    @Override
    public void viewModelInit(String mementoStr) {
        if (mementoStr != null) {
            final Memento memento = mementoService.parse(mementoStr);
            // final Bookmark propertyBookmark = memento.get("property",
            // Bookmark.class);
            setPackageName(memento.get("packageName", String.class));
        }
    }

    // //////////////////////////////////////

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Named("Select")
    @MemberOrder(name = "property", sequence = "1")
    public ApplicationSecurityManager selectPackageName(
            final String packageName) {
        setPackageName(packageName);
        return applicationSecurityService.manage(this);
    }

    public List<String> choices0SelectPackageName(String packageName) {
        return applicationFeatures.allPackageNames();
    }

    // //////////////////////////////////////

    @Render(Type.EAGERLY)
    public List<ApplicationFeature> getFeatures() {
        return applicationFeatures.findByPackageName(getPackageName());
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ApplicationSecurityService applicationSecurityService;

    @Inject
    private MementoService mementoService;

    @Inject
    private ApplicationFeatures applicationFeatures;

}
