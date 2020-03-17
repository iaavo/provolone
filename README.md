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

1.) Get the source code by executing "git clone https://github.com/iaavo/provolone.git"
2.) Execute the get-caciocavallo.sh
3.) Install caciocavallo into your local maven repository by executing "mvn install" in the newly created caciocavallo directory
4.) Execute the get-testinprogress.sh
5.) Execute the get-swingset2.sh
6.) Install swingset2 into your local maven repository by executing "mvn install" in the newly created swingset2 directory
7.) Goto /provolone/demo and execute "mvn package"