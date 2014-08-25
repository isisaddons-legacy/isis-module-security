VERSION=$1

if [ ! "$VERSION" ]; then
    echo "usage: $(basename $0) [version]"
    exit 1
fi

# edit parent pom.xml's reference
echo "editing parent's pom.xml (reference to dom module)"
cat pom.xml | sed "s/<isis-module-command.version>.*</<isis-module-command.version>$VERSION</" > pom.xml.$$.sed
mv pom.xml.$$.sed pom.xml

# edit dom's pom.xml
echo "editing dom's pom.xml"
pushd dom >/dev/null
mvn versions:set -DnewVersion=$VERSION > /dev/null
popd >/dev/null

echo "Committing changes"
git commit -am "bumping to $VERSION"

# tag if not a snapshot
echo $VERSION | grep -v SNAPSHOT > /dev/null
if [ $? = 0 ]; then
    echo "tagging (not a snapshot version)"
    git tag $VERSION
fi
