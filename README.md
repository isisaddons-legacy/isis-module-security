# isis-module-security #

[![Build Status](https://travis-ci.org/isisaddons/isis-module-security.png?branch=master)](https://travis-ci.org/isisaddons/isis-module-security)

This module, intended for use within [Apache Isis](http://isis.apache.org), provides the ability to manage users, roles,
and permissions.

    
The module consists of ...

## Screenshots ##

The following screenshots show an example app's usage of the module.  This example app has its own very simple 
`ExampleEntity` entity (and corresponding`ExampleEntities` repository), along with all the services and entities 
provided by the module itself.

#### Application Menus ####

The security module provides a number of menus: _Users_, _Roles_, _Permissions_ and _User Tenancies_:

![]https://raw.github.com/isisaddons/isis-module-security/master/images/010-menus.png)

#### Install example fixture data ####

![]https://raw.github.com/isisaddons/isis-module-security/master/images/020-install-fixtures.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/030-role.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/040-role-add-permission-package.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/050-permission-rule.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/060-permission-mode.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/070-permission-package-from-isis-metamodel.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/080-permission-added.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/090-role-add-permission-class.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/100-role-add-permission-class-in-package.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/110-role-add-permission-property.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/120-role-add-property-filtered-packages.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/130-role-add-permission-property.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/140-role-property-permission-added.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/150-role-add-collection-permission.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/160-role-add-collection-filtered-packages.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/170-role-add-permission-action.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/180-using-rules-and-modes-to-finetune.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/190-role-add-permission-filtered-packages.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/200-role-add-permission-action.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/210-role-action-permission-added.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/220-remove-permission.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/230-remove-permission-available.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/240-role-add-user.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/250-role-add-user-autoComplete.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/260-role-permission.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/270-permission.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/280-feature.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/290-user-update-name.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/300-user-name.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/310-user-name-updated.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/320-user-permission.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/320-user-update-tenancy.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/330-user-add-remove-roles.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/340-user-remove-role.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/350-user-effective-permissions-updated.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/360-user-effective-permissions.png)

todo: write about...

![]https://raw.github.com/isisaddons/isis-module-security/master/images/370-user-permission.png)


#### yada ####


#### yada ####


## Relationship to Apache Isis Core ##

Isis Core 1.6.0 included the `org.apache.isis.core:isis-module-xxx:1.6.0` Maven artifact.  This module is a
direct copy of that code, with the following changes:

* package names have been altered from `org.apache.isis` to `org.isisaddons.module.command`
* the `persistent-unit` (in the JDO manifest) has changed from `isis-module-xxx` to 
  `org-isisaddons-module-xxx-dom`

Otherwise the functionality is identical; warts and all!

At the time of writing the plan is to remove this module from Isis Core (so it won't be in Isis 1.7.0), and instead 
continue to develop it solely as one of the [Isis Addons](http://www.isisaddons.org) modules.


## How to configure/use ##

You can either use this module "out-of-the-box", or you can fork this repo and extend to your own requirements. 

To use "out-of-the-box":

* update your classpath by adding this dependency in your dom project's `pom.xml`:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;org.isisaddons.module.xxx&lt;/groupId&gt;
        &lt;artifactId&gt;isis-module-xxx-dom&lt;/artifactId&gt;
        &lt;version&gt;1.6.0&lt;/version&gt;
    &lt;/dependency&gt;
</pre>

* update your `WEB-INF/isis.properties`:

<pre>
    isis.services-installer=configuration-and-annotation
    isis.services.ServicesInstallerFromAnnotation.packagePrefix=
                    ...,\
                    org.isisaddons.module.xxx.xxx,\
                    ...

    isis.services = ...,\
                    org.isisaddons.module.xxx.XxxContributions,\
                    ...
</pre>

Notes:
* Check for later releases by searching [Maven Central Repo](http://search.maven.org/#search|ga|1|isis-module-security-dom).
* The `XxxContributions` service is optional but recommended; see below for more information.

If instead you want to extend this module's functionality, then we recommend that you fork this repo.  The repo is 
structured as follows:

* `pom.xml   ` - parent pom
* `dom       ` - the module implementation, depends on Isis applib
* `fixture   ` - fixtures, holding a sample domain objects and fixture scripts; depends on `dom`
* `integtests` - integration tests for the module; depends on `fixture`
* `webapp    ` - demo webapp (see above screenshots); depends on `dom` and `fixture`

Only the `dom` project is released to Maven Central Repo.  The versions of the other modules are purposely left at 
`0.0.1-SNAPSHOT` because they are not intended to be released.

## API ##

### XxxService ###

The `XxxService` defines the following API:

<pre>
public interface XxxService {
}
</pre>


## Implementation ##

## Supporting Services ##

## Related Modules/Services ##

... referenced by the [Isis Add-ons](http://www.isisaddons.org) website.



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

There are no third-party dependencies.


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

#### Release to Maven Central (scripted process) ####

The `release.sh` script automates the release process.  It performs the following:

* perform sanity check (`mvn clean install -o`) that everything builds ok
* bump the `pom.xml` to a specified release version, and tag
* perform a double check (`mvn clean install -o`) that everything still builds ok
* release the code using `mvn clean deploy`
* bump the `pom.xml` to a specified release version

For example:

    sh release.sh 1.6.1 \
                  1.6.2-SNAPSHOT \
                  dan@haywood-associates.co.uk \
                  "this is not really my passphrase"
    
where
* `$1` is the release version
* `$2` is the snapshot version
* `$3` is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* `$4` is the corresponding passphrase for that secret key.

If the script completes successfully, then push changes:

    git push
    
If the script fails to complete, then identify the cause, perform a `git reset --hard` to start over and fix the issue
before trying again.

#### Release to Maven Central (manual process) ####

If you don't want to use `release.sh`, then the steps can be performed manually.

To start, call `bumpver.sh` to bump up to the release version, eg:

     `sh bumpver.sh 1.6.1`

which:
* edit the parent `pom.xml`, to change `${isis-module-command.version}` to version
* edit the `dom` module's pom.xml version
* commit the changes
* if a SNAPSHOT, then tag

Next, do a quick sanity check:

    mvn clean install -o
    
All being well, then release from the `dom` module:

    pushd dom
    mvn clean deploy -P release \
        -Dpgp.secretkey=keyring:id=dan@haywood-associates.co.uk \
        -Dpgp.passphrase="literal:this is not really my passphrase"
    popd

where (for example):
* "dan@haywood-associates.co.uk" is the email of the secret key (`~/.gnupg/secring.gpg`) to use for signing
* the pass phrase is as specified as a literal

Other ways of specifying the key and passphrase are available, see the `pgp-maven-plugin`'s 
[documentation](http://kohsuke.org/pgp-maven-plugin/secretkey.html)).

If (in the `dom`'s `pom.xml` the `nexus-staging-maven-plugin` has the `autoReleaseAfterClose` setting set to `true`,
then the above command will automatically stage, close and the release the repo.  Sync'ing to Maven Central should 
happen automatically.  According to Sonatype's guide, it takes about 10 minutes to sync, but up to 2 hours to update 
[search](http://search.maven.org).

If instead the `autoReleaseAfterClose` setting is set to `false`, then the repo will require manually closing and 
releasing either by logging onto the [Sonatype's OSS staging repo](https://oss.sonatype.org) or alternatively by 
releasing from the command line using `mvn nexus-staging:release`.

Finally, don't forget to update the release to next snapshot, eg:

    sh bumpver.sh 1.6.2-SNAPSHOT

and then push changes.
