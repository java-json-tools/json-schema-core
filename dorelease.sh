#!/bin/bash

#
# This will build everything that is needed and push to Maven central.
#
# The only thing missing is making this script non interactive...
#

mvn clean package repository:bundle-create gpg:sign deploy
