#!/bin/bash

# Release instructions
# 1. Follow the gpg setup instructions: http://central.sonatype.org/pages/working-with-pgp-signatures.html
# 2. In project root create gradle.properties and add:
# sonatypeUsername=metadata-dev
# sonatypePassword=password in vault for oss sonatype<ensure newline>
#
# signing.keyId=generated key
# signing.password=password for gpg key
# signing.secretKeyRingFile=/Users/username/.gnupg/secring.gpg
# 3. ./dorelease.sh
# 4. Go to UI at: https://oss.sonatype.org
# 5. Login as metadata-dev
# 6. Follow the instructions here: http://central.sonatype.org/pages/releasing-the-deployment.html

./gradlew --recompile-scripts clean test uploadArchives

