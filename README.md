provolone
=========

run the get-caciocavallo script to get caciocavallo sources and apply patch automatically to resolve compile errors.

Build Prerequisites
===================

- git
- mercurial
- maven
- openjdk-7-jdk

Build Process
=============

- Get the source code by executing "git clone https://github.com/iaavo/provolone.git"
- Execute the get-caciocavallo.sh
- Install caciocavallo into your local maven repository by executing "mvn install" in the newly created caciocavallo directory
- Execute the get-testinprogress.sh
- Navigate to testInProgress-plugin/plugin and package the testInProgress jenkins plugin by executing "mvn package".
- Execute the get-swingset2.sh
- Install swingset2 into your local maven repository by executing "mvn install" in the newly created swingset2 directory
- Create a new Jenkins Job and build it once to let jenkins create the directory structure
- Copy libcacioweb.so from caciocavallo/cacio-web/target into provolone/demo
- Copy the provolone/demo directory into your jenkins job workspace directory.
- Install the testInProgress Jenkins Plugin by going to the advanced settings of the plugin manager in jenkins ("http://localhost:8080/pluginManager/advanced") and manually uploading a plugin. Select the newly generated testInProgress-plugin.hpi located in testInProgess-plugin/plugin/target.
- Configure the Job, check the "Show tests in progress" Checkbox and add a shell execution: mvn -Dmaven.repo.local="path/to/your/local/maven/repository" test
- Build the job and watch testinprogess.

