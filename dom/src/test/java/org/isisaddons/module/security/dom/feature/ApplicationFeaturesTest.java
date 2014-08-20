package org.isisaddons.module.security.dom.feature;

import java.util.List;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.*;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ApplicationFeaturesTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ObjectSpecification spec;
    @Mock
    OneToOneAssociation prop;
    @Mock
    OneToManyAssociation coll;
    @Mock
    ObjectAction act;

    ApplicationFeatures applicationFeatures;

    @Before
    public void setUp() throws Exception {
        applicationFeatures = new ApplicationFeatures();
    }

    public static class Load extends ApplicationFeaturesTest {

        @Test
        public void singleClassNoMembers() throws Exception {

            final List<ObjectAssociation> properties = Lists.<ObjectAssociation>newArrayList(prop);
            final List<ObjectAssociation> collections = Lists.<ObjectAssociation>newArrayList(coll);
            final List<ObjectAction> actions = Lists.newArrayList(act);

            context.checking(new Expectations() {{
                allowing(spec).isAbstract();
                will(returnValue(false));

                allowing(spec).getFullIdentifier();
                will(returnValue("com.mycompany.Bar"));

                allowing(spec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.PROPERTIES));
                will(returnValue(properties));

                allowing(spec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.COLLECTIONS));
                will(returnValue(collections));

                allowing(spec).getObjectActions(with(Contributed.INCLUDED));
                will(returnValue(actions));

                allowing(prop).getName();
                will(returnValue("someProperty"));

                allowing(coll).getName();
                will(returnValue("someCollection"));

                allowing(act).getName();
                will(returnValue("someAction"));
            }});

            // when
            applicationFeatures.load(spec);

            // then
            final ApplicationFeature comPkg = applicationFeatures.findPackage(ApplicationFeature.newPackage("com"));
            assertThat(comPkg, is(notNullValue()));
            final ApplicationFeature comMycompanyPkg = applicationFeatures.findPackage(ApplicationFeature.newPackage("com.mycompany"));
            assertThat(comPkg, is(notNullValue()));
            assertThat(comPkg.getContents(), contains(comMycompanyPkg));
            assertThat(comMycompanyPkg.getContents(), contains(ApplicationFeature.newClass("com.mycompany.Bar")));

            // then
            final ApplicationFeature barClass = applicationFeatures.findClass(ApplicationFeature.newClass("com.mycompany.Bar"));
            assertThat(barClass, is(Matchers.notNullValue()));

            Assert.assertThat(barClass.getMembers(),
                    containsInAnyOrder(
                            ApplicationFeature.newMember("com.mycompany.Bar", "someProperty"),
                            ApplicationFeature.newMember("com.mycompany.Bar", "someCollection"),
                            ApplicationFeature.newMember("com.mycompany.Bar", "someAction")
                    ));
        }

    }

    public static class AddClassParent extends ApplicationFeaturesTest {

        @Test
        public void parentNotYetEncountered() throws Exception {

            // given
            final ApplicationFeature classFeature = ApplicationFeature.newClass("com.mycompany.Bar");

            // when
            final ApplicationFeature classParent = applicationFeatures.addClassParent(classFeature);

            // then
            Assert.assertThat(classParent, is(equalTo(classFeature.getParentPackage())));
            final ApplicationFeature classPackage = applicationFeatures.findPackage(classParent);
            assertThat(classPackage, is(Matchers.notNullValue()));

        }

        @Test
        public void parentAlreadyEncountered() throws Exception {

            // given
            final ApplicationFeature pkg = ApplicationFeature.newPackage("com.mycompany");
            applicationFeatures.packageFeatures.add(pkg);

            final ApplicationFeature classFeature = ApplicationFeature.newClass("com.mycompany.Bar");

            // when
            final ApplicationFeature applicationFeature = applicationFeatures.addClassParent(classFeature);

            // then
            Assert.assertThat(applicationFeature, is(equalTo(pkg)));
        }

    }
}