package org.isisaddons.module.security.dom.feature;

import java.util.List;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.core.metamodel.facets.all.hide.HiddenFacet;
import org.apache.isis.core.metamodel.facets.members.hidden.HiddenFacetNever;
import org.apache.isis.core.metamodel.runtimecontext.ServicesInjector;
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
    ObjectSpecification mockSpec;
    @Mock
    OneToOneAssociation mockProp;
    @Mock
    OneToManyAssociation mockColl;
    @Mock
    ObjectAction mockAct;
    ObjectAction mockActThatIsHidden;

    @Mock
    DomainObjectContainer mockContainer;

    @Mock
    ServicesInjector mockServicesInjector;

    ApplicationFeatures applicationFeatures;

    @Before
    public void setUp() throws Exception {
        applicationFeatures = new ApplicationFeatures();
        applicationFeatures.container = mockContainer;
        applicationFeatures.setServicesInjector(mockServicesInjector);

        mockActThatIsHidden = context.mock(ObjectAction.class, "mockActThatIsHidden");
    }

    public static class Load extends ApplicationFeaturesTest {

        public static class Bar {}

        @Test
        public void happyCase() throws Exception {

            final List<ObjectAssociation> properties = Lists.<ObjectAssociation>newArrayList(mockProp);
            final List<ObjectAssociation> collections = Lists.<ObjectAssociation>newArrayList(mockColl);
            final List<ObjectAction> actions = Lists.newArrayList(mockAct, mockActThatIsHidden);

            context.checking(new Expectations() {{
                allowing(mockSpec).isAbstract();
                will(returnValue(false));

                allowing(mockSpec).getFullIdentifier();
                will(returnValue(Bar.class.getName()));

                allowing(mockSpec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.PROPERTIES));
                will(returnValue(properties));

                allowing(mockSpec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.COLLECTIONS));
                will(returnValue(collections));

                allowing(mockSpec).getFacet(HiddenFacet.class);
                will(returnValue(new HiddenFacetNever(mockSpec)));

                allowing(mockSpec).getCorrespondingClass();
                will(returnValue(Bar.class));

                allowing(mockSpec).getObjectActions(with(Contributed.INCLUDED));
                will(returnValue(actions));

                allowing(mockProp).getId();
                will(returnValue("someProperty"));

                allowing(mockProp).isAlwaysHidden();
                will(returnValue(false));

                allowing(mockColl).getId();
                will(returnValue("someCollection"));

                allowing(mockColl).isAlwaysHidden();
                will(returnValue(false));

                allowing(mockAct).getId();
                will(returnValue("someAction"));

                allowing(mockAct).isAlwaysHidden();
                will(returnValue(false));

                allowing(mockAct).getSemantics();
                will(returnValue(ActionSemantics.Of.SAFE));

                allowing(mockActThatIsHidden).getId();
                will(returnValue("someActionThatIsHidden"));

                allowing(mockActThatIsHidden).isAlwaysHidden();
                will(returnValue(true));

                allowing(mockActThatIsHidden).getSemantics();
                will(returnValue(ActionSemantics.Of.SAFE));

                allowing(mockServicesInjector).getRegisteredServices();
                will(returnValue(Lists.newArrayList()));
            }});

            // then
            final Sequence sequence = context.sequence("loadSequence");
            context.checking(new Expectations() {{
                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newClass(Bar.class.getName()))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember(Bar.class.getName(), "someProperty"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember(Bar.class.getName(), "someCollection"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember(Bar.class.getName(), "someAction"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.feature"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module.security"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org"))));
            }});

            // when
            applicationFeatures.createApplicationFeaturesFor(mockSpec);

            // then
            final ApplicationFeature orgPkg = applicationFeatures.findPackage(ApplicationFeatureId.newPackage("org"));
            assertThat(orgPkg, is(notNullValue()));
            final ApplicationFeature orgIsisaddonsPkg = applicationFeatures.findPackage(ApplicationFeatureId.newPackage("org.isisaddons"));
            assertThat(orgPkg, is(notNullValue()));
            final ApplicationFeature featurePkg = applicationFeatures.findPackage(ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.feature"));
            assertThat(orgPkg, is(notNullValue()));
            assertThat(orgPkg.getContents(), contains(orgIsisaddonsPkg.getFeatureId()));
            assertThat(featurePkg.getContents(), contains(ApplicationFeatureId.newClass(Bar.class.getName())));

            // then
            final ApplicationFeature barClass = applicationFeatures.findClass(ApplicationFeatureId.newClass(Bar.class.getName()));
            assertThat(barClass, is(Matchers.notNullValue()));

            // then the mockActThatIsHidden is not listed.
            assertThat(barClass.getProperties().size(), is(1));
            assertThat(barClass.getCollections().size(), is(1));
            assertThat(barClass.getActions().size(), is(1));
            assertThat(barClass.getProperties(),
                    containsInAnyOrder(
                            ApplicationFeatureId.newMember(Bar.class.getName(), "someProperty")
                    ));
            assertThat(barClass.getCollections(),
                    containsInAnyOrder(
                            ApplicationFeatureId.newMember(Bar.class.getName(), "someCollection")
                    ));
            assertThat(barClass.getActions(),
                    containsInAnyOrder(
                            ApplicationFeatureId.newMember(Bar.class.getName(), "someAction")
                    ));
        }

    }

    public static class AddClassParent extends ApplicationFeaturesTest {

        @Test
        public void parentNotYetEncountered() throws Exception {

            // given
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // then
            final ApplicationFeature newlyCreatedParent = new ApplicationFeature();
            context.checking(new Expectations() {{
                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                will(returnValue(newlyCreatedParent));
            }});

            // when
            final ApplicationFeatureId classParentId = applicationFeatures.addClassParent(classFeatureId);

            // then
            Assert.assertThat(classParentId, is(equalTo(classFeatureId.getParentPackageId())));
            final ApplicationFeature classPackage = applicationFeatures.findPackage(classParentId);
            assertThat(classPackage, is(newlyCreatedParent));
        }

        @Test
        public void parentAlreadyEncountered() throws Exception {

            // given
            final ApplicationFeatureId packageId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationFeature pkg = new ApplicationFeature();
            pkg.setFeatureId(packageId);
            applicationFeatures.packageFeatures.put(packageId, pkg);

            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // when
            final ApplicationFeatureId applicationFeatureId = applicationFeatures.addClassParent(classFeatureId);

            // then
            Assert.assertThat(applicationFeatureId, is(equalTo(packageId)));
        }

    }
}