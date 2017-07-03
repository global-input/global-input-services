source deploy/util.sh

git add .
git commit -m "releasing"
mvn jgitflow:release-start
mvn jgitflow:release-finish
git checkout  master
getProjectVersionFromPom
setPackageVersion
package
git checkout develop
deploy/create_deploy_scripts.sh
 