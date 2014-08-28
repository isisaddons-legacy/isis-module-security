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

    ApplicationFeatures applicationFeatures;

    @Before
    public void setUp() throws Exception {
        applicationFeatures = new ApplicationFeatures();
        applicationFeatures.container = mockContainer;

        mockActThatIsHidden = context.mock(ObjectAction.class, "mockActThatIsHidden");
    }

    public static class Load extends ApplicationFeaturesTest {

        @Test
        public void singleClassNoMembers() throws Exception {

            final List<ObjectAssociation> properties = Lists.<ObjectAssociation>newArrayList(mockProp);
            final List<ObjectAssociation> collections = Lists.<ObjectAssociation>newArrayList(mockColl);
            final List<ObjectAction> actions = Lists.newArrayList(mockAct, mockActThatIsHidden);

            context.checking(new Expectations() {{
                allowing(mockSpec).isAbstract();
                will(returnValue(false));

                allowing(mockSpec).getFullIdentifier();
                will(returnValue("com.mycompany.Bar"));

                allowing(mockSpec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.PROPERTIES));
                will(returnValue(properties));

                allowing(mockSpec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.COLLECTIONS));
                will(returnValue(collections));

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

                allowing(mockActThatIsHidden).getId();
                will(returnValue("someActionThatIsHidden"));

                allowing(mockActThatIsHidden).isAlwaysHidden();
                will(returnValue(true));

            }});

            // then
            final Sequence sequence = context.sequence("loadSequence");
            context.checking(new Expectations() {{
                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newClass("com.mycompany.Bar"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember("com.mycompany.Bar", "someProperty"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember("com.mycompany.Bar", "someCollection"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember("com.mycompany.Bar", "someAction"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("com.mycompany"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("com"))));
            }});

            // when
            applicationFeatures.createApplicationFeaturesFor(mockSpec);

            // then
            final ApplicationFeature comPkg = applicationFeatures.findPackage(ApplicationFeatureId.newPackage("com"));
            assertThat(comPkg, is(notNullValue()));
            final ApplicationFeature comMycompanyPkg = applicationFeatures.findPackage(ApplicationFeatureId.newPackage("com.mycompany"));
            assertThat(comPkg, is(notNullValue()));
            assertThat(comPkg.getContents(), contains(comMycompanyPkg.getFeatureId()));
            assertThat(comMycompanyPkg.getContents(), contains(ApplicationFeatureId.newClass("com.mycompany.Bar")));

            // then
            final ApplicationFeature barClass = applicationFeatures.findClass(ApplicationFeatureId.newClass("com.mycompany.Bar"));
            assertThat(barClass, is(Matchers.notNullValue()));

            // then the mockActThatIsHidden is not listed.
            assertThat(barClass.getMembers().size(), is(3));
            assertThat(barClass.getMembers(),
                    containsInAnyOrder(
                            ApplicationFeatureId.newMember("com.mycompany.Bar", "someProperty"),
                            ApplicationFeatureId.newMember("com.mycompany.Bar", "someCollection"),
                            ApplicationFeatureId.newMember("com.mycompany.Bar", "someAction")
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