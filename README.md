# isis-module-security #

[![Build Status](https://travis-ci.org/isisaddons/isis-module-security.png?branch=master)](https://travis-ci.org/isisaddons/isis-module-security)

This module, intended for use within [Apache Isis](http://isis.apache.org), provides the ability to manage *user*s, *role*s,
and *permission*s.  Users have roles, roles have permissions, and permissions are associated with *application feature*s. 
These features are derived from the Isis metamodel and can be scoped at either a _package_, _class_ or individual _class member_.
Permissions themselves can either _allow_ or _veto_ the ability to _view_ or _change_ any application feature.

A key design objective of this module has been to limit the amount of permissioning data required.  To support this objective:

* permissions are hierarchical: a class-level permission applies to all class members, while a package-level permission 
  applies to all classes of all subpackages
  
* permissions can _allow_ or _veto_ access; thus a role can be granted access to most features, but excluded from selective others

* permissions are scoped: a member-level permission overrides a class-level permission, a class-level permission 
  overrides a package-level permission; the lower-level package permission overrides a higher-level one 
  (eg `com.mycompany.invoicing` overrides `com.mycompany`).

* if there are conflicting permissions at the same scope, then the _allow_ takes precedence over the __veto__.
  
The module also provides an implementation of [Apache Shiro](http://shiro.apache.org)'s 
[AuthorizingRealm](https://shiro.apache.org/static/1.2.2/apidocs/org/apache/shiro/realm/AuthorizingRealm.html).  This 
allows the users/permissions to be used for Isis' authentication and/or authorization.

Authentication is optional; each user is either _local_ or _delegated_:

* users with a _delegated_ account type are authenticated through a (configured) _delegated authentication realm_ (for 
  example LDAP).  Any other implementation of Shiro's `AuthenticatingRealm` can be used.
 
* users with a _local_ account type are authenticated through a `PasswordEncryptionService`.

The module provides a default implementation based on [jBCrypt](http://www.mindrot.org/projects/jBCrypt/), but other 
implementations can be plugged-in if required.
  
## Domain Model ##

![](https://raw.github.com/isisaddons/isis-module-security/master/images/domain-model.png)

The above diagram was generated by [yuml.me](http://yuml.me); see appendix at end of page for the DSL.

## Screenshots ##

The following screenshots show an example app's usage of the module, which includes all the services and entities 
([users, roles, permissions](https://github.com/isisaddons/isis-module-security/tree/master/dom/src/main/java/org/isisaddons/module/security/dom) etc) 
provided by the module itself.  This example app's [domain](https://github.com/isisaddons/isis-module-security/tree/master/fixture/src/main/java/org/isisaddons/module/security/fixture/dom) 
also has its own very simple `ExampleEntity` entity and corresponding repository.

For further screenshots, see the [screenshot tutorial](https://github.com/isisaddons/isis-module-security/wiki/Screenshot-Tutorial) on the wiki.

>
> Note: these screenshots show the security module running on Apache Isis 1.7.0.   In 1.8.0-SNAPSHOT, the UI provided by Apache Isis has been substantially enhanced.
>

#### Automatically Seeds Roles ####

When the security module starts up, it will automatically (idempotently) seed a number of roles, corresponding permissions and a 
default `isis-module-security-admin` user.  The corresponding (similarly named) `isis-module-security-admin` role 
grants all permissions to all classes in the security module itself:

![](https://raw.github.com/isisaddons/isis-module-security/master/images/030-role.png)

The `isis-module-security-regular-user` role grants selected permissions to viewing/changing members of the 
`ApplicationUser` class (so that a user with this role can view/update their own record):

![](https://raw.github.com/isisaddons/isis-module-security/master/images/035-role-regular-user.png)

#### Add permission for all features in a package ####

Permissions created at the package level apply to all classes in all packages and subpackages (that is, recursively).

![](https://raw.github.com/isisaddons/isis-module-security/master/images/040-role-add-permission-package.png)

#### Permissions can ALLOW or VETO access ####

Permissions can either grant (allow) access or prevent (veto) access.  If a user has permissions that contradict each 
other (for example, they are a member of "roleA" that allows the permission, but also of "roleB" that vetoes the
permission) then by default the allow wins.  However, this is strategy is pluggable, and the security module can be 
configured such that a veto would override an allow if required.

![](https://raw.github.com/isisaddons/isis-module-security/master/images/050-permission-rule.png)

#### Permissions can apply to VIEWING or CHANGING the feature ####

For a property, "changing" means being able to edit it.  For a collection, "changing" means being able to add or remove
from it.  For an action, "changing" means being able to invoke it.

![](https://raw.github.com/isisaddons/isis-module-security/master/images/060-permission-mode.png)

Note that Isis' Wicket viewer currently does not support the concept of "changing" collections; the work-around is 
instead create a pair of actions to add/remove instead.  This level of control is usually needed anyway.

An _allow/changing_ permission naturally enough implies _allow/viewing_, while conversely and symmetrically
 _veto/viewing_ permission implies _veto/changing_.

#### Specify package ####

The list of packages is derived from Isis' own metamodel.

![](https://raw.github.com/isisaddons/isis-module-security/master/images/070-permission-package-from-isis-metamodel.png)

#### Add permission for all members in a class ####

Permissions defined at the class level take precedence to those defined at the package level.  For example, a user
might have _allow/viewing_ at a parent level, but have this escalated to _allow/changing_ for a particular
class.  Conversely, the class-level permission might veto access.

![](https://raw.github.com/isisaddons/isis-module-security/master/images/090-role-add-permission-class.png)

#### Add permission to an individual action of a member ####

Permissions can also be defined at the member level: action, property or collection.  These override permissions 
defined at either the class- or package-level.

For example, to add a permission for an individual action:

![](https://raw.github.com/isisaddons/isis-module-security/master/images/110-role-add-permission-action.png)

#### Application feature for a class member ####

Class members (action, property or collection) lists the permissions defined against that member:

![](https://raw.github.com/isisaddons/isis-module-security/master/images/280-feature.png)

It provides access in turn to the parent (class) feature... 

#### Application feature for a class ####

The class feature lists associated permissions (if any), also the child properties, collections and actions:

![](https://raw.github.com/isisaddons/isis-module-security/master/images/283-class-feature.png)

It also provides access to its parent (package) feature ...

#### Application feature for a package ####

The package feature lists its associated permissions (if any), its contents (class or package features) and also 
provides access to its parent (package) feature.

![](https://raw.github.com/isisaddons/isis-module-security/master/images/286-package-feature.png)

#### Application users ####

Application users can have either a _local_ or a _delegated_ account type.  Local users are authenticated and authorized
through the module's Shiro realm implementation.  Optionally a delegate authentication realm can be configured; if so 
then delegated users can be created and their credentials will be authenticated by the delegate authentication realm.

If configured _without_ a delegate realm, then the users must be created by the administrator.    If configured _with_
a delegate realm, then the user will be created automatically if that user attempts to log on.  However, for safety 
their `ApplicationUser` accounts are created in a disabled state and with no roles, so the administrator is still required
to update them.

Once the user is created, then additional information about that user can be captured, including their name and
contact details.  This information is not otherwise used by the security module, but may be of use to other parts
of the application.  The users' roles and effective permissions are also shown.
 
![](https://raw.github.com/isisaddons/isis-module-security/master/images/289-user-details.png)

A user can maintain their own details, but may not alter other users' details.  An administrator can alter all details,
as well as reset a users' password.

If a user is disabled, then they may not log in.  This is useful for temporarily barring access to users without 
having to change all their roles, for example if they leave the company or go on maternity leave.


#### Application Tenancy (1.8.0-SNAPSHOT) ####

Both application users and domain objects can be associated with an `ApplicationTenancy`.  For application user's this
is a property of the object, for domain object's this is performed by implementing the `WithApplicationTenancy` interface:

    public interface WithApplicationTenancy {
        ApplicationTenancy getApplicationTenancy();
    }

The application can then be configured so that access to domain objects can be restricted based on the respective
tenancies of the user accessing the object and of the object itself.  The table below summarizes the rules:

<table>
    <tr>
        <th>object's tenancy</th><th>user's tenancy</th><th>access</th>
    </tr>
    <tr>
        <td>null</td><td>null</td><td>editable</td>
    </tr>
    <tr>
        <td>null</td><td>non-null</td><td>editable</td>
    </tr>
    <tr>
        <td>/</td><td>/</td><td>editable</td>
    </tr>
    <tr>
        <td>/</td><td>/it</td><td>visible</td>
    </tr>
    <tr>
        <td>/</td><td>/it/car</td><td>visible</td>
    </tr>
    <tr>
        <td>/</td><td>/it/igl</td><td>visible</td>
    </tr>
    <tr>
        <td>/</td><td>/fr</td><td>visible</td>
    </tr>
    <tr>
        <td>/</td><td>null</td><td>not visible</td>
    </tr>
    <tr>
        <td>/it</td><td>/</td><td>editable</td>
    </tr>
    <tr>
        <td>/it</td><td>/it</td><td>editable</td>
    </tr>
    <tr>
        <td>/it</td><td>/it/car</td><td>visible</td>
    </tr>
    <tr>
        <td>/it</td><td>/it/igl</td><td>visible</td>
    </tr>
    <tr>
        <td>/it</td><td>/fr</td><td>not visible</td>
    </tr>
    <tr>
        <td>/it</td><td>null</td><td>not visible</td>
    </tr>
    <tr>
        <td>/it/car</td><td>/</td><td>editable</td>
    </tr>
    <tr>
        <td>/it/car</td><td>/it</td><td>editable</td>
    </tr>
    <tr>
        <td>/it/car</td><td>/it/car</td><td>editable</td>
    </tr>
    <tr>
        <td>/it/car</td><td>/it/igl</td><td>not visible</td>
    </tr>
    <tr>
        <td>/it/car</td><td>/fr</td><td>not visible</td>
    </tr>
    <tr>
        <td>/it/car</td><td>null</td><td>not visible</td>
    </tr>
</table>

To enable this requires a single configuration property to be set, see below.


## How to run the Demo App ##

The prerequisite software is:

* Java JDK 7 (nb: Isis currently does not support JDK 8)
* [maven 3](http://maven.apache.org) (3.2.x is recommended).

To build the demo app:

    git clone https://github.com/isisaddons/isis-module-security.git
    mvn clean install

To run the demo app:

    mvn antrun:run -P self-host
    
Then log on using user: `isis-module-security-admin`, password: `pass`


## How to configure/use ##

You can either use this module "out-of-the-box", or you can fork this repo and extend to your own requirements. 

### Out-of-the-box ###

#### Shiro configuration (shiro.ini) ####

The module includes `org.isisaddons.module.security.shiro.IsisModuleSecurityRealm`, an implementation of Apache Shiro's
`org.apache.shiro.realm.AuthorizingRealm` class.  This realm is intended to be configured as the single realm for Shiro,
but it can optionally have a delegateAuthenticationRealm injected into it.

* if configured without a delegate realm then `IsisModuleSecurityRealm` deals only with _local_ users and performs
  both authentication and authorization for them.  Authentication is performed against encrypted password.  Users with
  _delegate_ account type will be unable to log in.

* if configured with a delegate realm then `IsisModuleSecurityRealm` deals with both _delegated_ and _local_ users.
  Authentication of _delegated_ users is performed by the delegate authentication realm, while _local_ users continue
  to be authenticated in the same way as before, against their encrypted password.  Authorization is performed the
  same way for either account type, by reference to their user roles and those roles' permissions.

For both _local_ and _delegated_ users the realm will prevent a disabled user from logging in. 

To configure, update your `WEB-INF/shiro.ini`'s `[main]` section:

<pre>
[main]

isisModuleSecurityRealm=org.isisaddons.module.security.shiro.IsisModuleSecurityRealm

authenticationStrategy=org.isisaddons.module.security.shiro.AuthenticationStrategyForIsisModuleSecurityRealm
securityManager.authenticator.authenticationStrategy = $authenticationStrategy

securityManager.realms = $isisModuleSecurityRealm
</pre>

If a delegate authentication realm is used, then define it and inject (again, in the `[main]` section):

<pre>
someOtherRealm=...

isisModuleSecurityRealm.delegateAuthenticationRealm=$someOtherRealm
</pre>

where `$someOtherRealm` defines some other realm to perform authentication.


#### Isis domain services (isis.properties) ####

Update the `WEB-INF/isis.properties`:

<pre>
    isis.services-installer=configuration-and-annotation
    isis.services.ServicesInstallerFromAnnotation.packagePrefix=
            ...,\
            org.isisaddons.module.security,\
            ...

    isis.services = ...,\
            org.isisaddons.module.security.dom.password.PasswordEncryptionServiceUsingJBcrypt,\
            org.isisaddons.module.security.dom.permission.PermissionsEvaluationServiceAllowBeatsVeto,\
            ...
</pre>

where:

* the `PasswordEncryptionServiceUsingJBcrypt` is an implementation of the `PasswordEncryptionService`.  This is 
  mandatory; local users (including the default `isis-module-security-admin` administrator user) must be authenticated
  using the password service.  If required, any other implementation can be supplied.

* The `PermissionsEvaluationServiceAllowBeatsVeto` is an implementation of the `PermissionsEvaluationService` that
  determines how to resolve conflicting permissions at the same scope.  This service is optional; if not present
  then the module will default to an allow-beats-veto strategy.  An alternative implementation of
  `PermissionsEvaluationServiceVetoBeatsAllow` is also available for use if required; or any other implementation
   of this interface can be supplied.

There is further discussion of the `PasswordEncryptionService` and `PermissionsEvaluationService` below.

#### Tenancy checking (isis.properties, 1.8.0-SNAPSHOT) ####

To enable tenancy checking (as described above, to restrict a user's access to tenanted objects), add the following
in `WEB-INF/isis.properties`:

<pre>
    isis.reflector.facets.include=org.isisaddons.module.security.facets.TenantedAuthorizationFacetFactory
</pre>


#### Classpath ####

Finally, update your classpath by adding this dependency in your dom project's `pom.xml`:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;org.isisaddons.module.security&lt;/groupId&gt;
        &lt;artifactId&gt;isis-module-security-dom&lt;/artifactId&gt;
        &lt;version&gt;1.7.0&lt;/version&gt;
    &lt;/dependency&gt;
</pre>

If using the `PasswordEncryptionServiceUsingJBcrypt` service, also add a dependency on the underlying library:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;org.mindrot&lt;/groupId&gt;
        &lt;artifactId&gt;jbcrypt&lt;/artifactId&gt;
        &lt;version&gt;0.3m&lt;/version&gt;
    &lt;/dependency&gt;
</pre>

Check for later releases by searching [Maven Central Repo](http://search.maven.org/#search|ga|1|isis-module-security-dom).




### "Out-of-the-box" (-SNAPSHOT) ###

If you want to use the current `-SNAPSHOT`, then the steps are the same as above, except:

* when updating the classpath, specify the appropriate -SNAPSHOT version:

<pre>
    &lt;version&gt;1.8.0-SNAPSHOT&lt;/version&gt;
</pre>

* add the repository definition to pick up the most recent snapshot (we use the Cloudbees continuous integration service).  We suggest defining the repository in a `<profile>`:

<pre>
    &lt;profile&gt;
        &lt;id&gt;cloudbees-snapshots&lt;/id&gt;
        &lt;activation&gt;
            &lt;activeByDefault&gt;true&lt;/activeByDefault&gt;
        &lt;/activation&gt;
        &lt;repositories&gt;
            &lt;repository&gt;
                &lt;id&gt;snapshots-repo&lt;/id&gt;
                &lt;url&gt;http://repository-estatio.forge.cloudbees.com/snapshot/&lt;/url&gt;
                &lt;releases&gt;
                    &lt;enabled&gt;false&lt;/enabled&gt;
                &lt;/releases&gt;
                &lt;snapshots&gt;
                    &lt;enabled&gt;true&lt;/enabled&gt;
                &lt;/snapshots&gt;
            &lt;/repository&gt;
        &lt;/repositories&gt;
    &lt;/profile&gt;
</pre>


### Forking the repo ###

If instead you want to extend this module's functionality, then we recommend that you fork this repo.  The repo is 
structured as follows:

* `pom.xml   ` - parent pom
* `dom       ` - the module implementation, depends on Isis applib
* `fixture   ` - fixtures, holding a sample domain objects and fixture scripts; depends on `dom`
* `integtests` - integration tests for the module; depends on `fixture`
* `webapp    ` - demo webapp (see above screenshots); depends on `dom` and `fixture`

Only the `dom` project is released to Maven Central Repo.  The versions of the other modules are purposely left at 
`0.0.1-SNAPSHOT` because they are not intended to be released.


## API and Implementation ##

The module defines a number of services and default implementations.  The behaviour of the module can be adjusted
by implementing and registerng alternative implementations.

### PasswordEncryptionService ###

The `PasswordEncryptionService` (responsible for authenticating _local_ user accounts) is responsible for 
performing a one-way encryption of password to encrypted form.  This encrypted version is then stored in the 
`ApplicationUser` entity's `encryptedPassword` property.

The service defines the following API:

<pre>
public interface PasswordEncryptionService {
    public String encrypt(final String password);
    public boolean matches(final String candidate, final String encrypted);
}
</pre>

The `PasswordEncryptionServiceUsingJbcrypt` provides an implementation of this service based on Blowfish algorithm.  It
depends in turn on `org.mindrot:jbcrypt` library; see above for details of updating the classpath to reference this
library.


### PermissionsEvaluationService ###

The `PermissionsEvaluationService` is responsible for determining which of a number of possibly conflicting permissions
apply to a target member.  It defines the following API:
 
<pre>
public interface PermissionsEvaluationService {
    public ApplicationPermissionValueSet.Evaluation evaluate(
                final ApplicationFeatureId targetMemberId,
                final ApplicationPermissionMode mode,
                final Collection<ApplicationPermissionValue> permissionValues);
</pre>

It is _not_ necessary to register any implementation of this service in `isis.properties`; by default a strategy of
allow-beats-veto is applied.  However this strategy can be explicitly specified by registering the (provided)
`PermissionsEvaluationServiceAllowBeatsVeto` implementation, or alternatively it can be reversed by registering 
`PermissionsEvaluationServiceVetoBeatsAllow`.  Of course some other implementation with a different algorithm may 
instead be registered.


## Default Roles, Permissions and Users ###

Whenever the application starts the security module checks for (and creates if missing) the following roles, permissions
and users: 

* `isis-module-security-admin` role
    * _allow_ _changing_ of all classes (recursively) under the `org.isisaddons.module.security.app` package 
    * _allow_ _changing_ of all classes (recursively) under the `org.isisaddons.module.security.dom` package 
* `isis-module-security-regular-user` role
    * _allow_ _changing_ (ie invocation) of the `org.isisaddons.module.security.app.user.MeService#me` action
    * _allow_ _viewing_ of the `org.isisaddons.module.security.app.dom.ApplicationUser` class
    * _allow_ _changing_ of the selected "self-service" actions of the `org.isisaddons.module.security.app.dom.ApplicationUser` class
* `isis-module-security-fixture` role
    * _allow_ _changing_ of `org.isisaddons.module.security.fixture` package (run example fixtures if prototyping) 
* `admin` user
    * granted `isis-module-security-admin` role
* `isis-applib-fixtureresults` role
    * _allow_ _changing_ of `org.apache.isis.applib.fixturescripts.FixtureResult` class

This work is performed by the `SeedSecurityModuleService`.


## Future Directions/Possible Improvements ##

Limitations in current implementation:
- It is not possible to set permissions on the root package.  The workaround is to specify for `org` or `com` top-level package instead.

Ideas for future features:
- users could possibly be extended to include user settings, refactored out from [isis-module-settings](https://github.com/isisaddons/isis-module-settings)
- features could possibly be refactored out/merged with [isis-module-devutils](https://github.com/isisaddons/isis-module-devutils). 
- hierarchical roles

## Change Log ##

* `1.8.0` (snapshot) - released against Isis 1.8.0.  ApplicationTenancy extended to support hierarchical tenancies, with path as primary key (nb: breaking change), support to make easier to extend (pluggable factories and events for all actions). MeService on TERTIARY menuBar.
* `1.7.0` - released against Isis 1.7.0
* `1.6.2` - made more resilient so can be called by an application's own 'security seed' service
* `1.6.1` - support for account types and delegated authentication realm
* `1.6.0` - first release


## Legal Stuff ##
 
#### License ####

    Copyright 2014 Dan Haywood

    Licensed under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.


#### Dependencies ####

In addition to Apache Isis, this module depends on:

* `org.mindrot:jbcrypt` (Apache-like license); only required if the `PasswordEncryptionServiceUsingJBcrypt` service is configured.


##  Maven deploy notes ##

Only the `dom` module is deployed, and is done so using Sonatype's OSS support (see 
[user guide](http://central.sonatype.org/pages/apache-maven.html)).

#### Release to Sonatype's Snapshot Repo ####

To deploy a snapshot, use:

    pushd dom
    mvn clean deploy
    popd

The artifacts should be available in Sonatype's 
[Snapshot Repo](https://oss.sonatype.org/content/repositories/snapshots).

#### Release to Maven Central ####

The `release.sh` script automates the release process.  It performs the following:

* performs a sanity check (`mvn clean install -o`) that everything builds ok
* bumps the `pom.xml` to a specified release version, and tag
* performs a double check (`mvn clean install -o`) that everything still builds ok
* releases the code using `mvn clean deploy`
* bumps the `pom.xml` to a specified release version

For example:

    sh release.sh 1.8.0 \
                  1.9.0-SNAPSHOT \
                  dan@haywood-associates.co.uk \
                  "this is not really my passphrase"
    
where
* `$1` is the release version
* `$2` is the snapshot version
* `$3` is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* `$4` is the corresponding passphrase for that secret key.

Other ways of specifying the key and passphrase are available, see the `pgp-maven-plugin`'s 
[documentation](http://kohsuke.org/pgp-maven-plugin/secretkey.html)).

If the script completes successfully, then push changes:

    git push

If the script fails to complete, then identify the cause, perform a `git reset --hard` to start over and fix the issue
before trying again.  Note that in the `dom`'s `pom.xml` the `nexus-staging-maven-plugin` has the 
`autoReleaseAfterClose` setting set to `true` (to automatically stage, close and the release the repo).  You may want
to set this to `false` if debugging an issue.
 
According to Sonatype's guide, it takes about 10 minutes to sync, but up to 2 hours to update [search](http://search.maven.org).


## Appendix: yuml.me DSL

<pre>
[ApplicationTenancy|name;path{bg:blue}]<0..*children-parent0..1>[[ApplicationTenancy]
[ApplicationUser|username{bg:green}]0..*->0..1[ApplicationTenancy]
[ApplicationUser]1-0..*>[ApplicationRole|name{bg:yellow}]
[ApplicationRole]1-0..*>[ApplicationPermission]
[ApplicationUser]->[AccountType|LOCAL;DELEGATED]
[ApplicationFeature|fullyQualifiedName{bg:green}]-memberType>0..1[ApplicationMemberType|PROPERTY;COLLECTION;ACTION]
[ApplicationFeature]->type[ApplicationFeatureType|PACKAGE;CLASS;MEMBER]
[ApplicationPermission{bg:pink}]++->[ApplicationFeature]
[ApplicationPermission]->[ApplicationPermissionMode|VIEWING;CHANGING]
[ApplicationPermission]->[ApplicationPermissionRule|ALLOW;VETO]
</pre>

