## Preamble

All instructions in this file use the Linux (or other Unix) conventions for
build. If you happen to use Windows, replace `./gradlew` with `gradlew.bat`.

## Building instructions

### Gradle usage

You may be fortunate enough that your IDE has Gradle support. Should it not
be the case, first report a bug to your vendor; then refer to the cheat sheet
below:

```
# List the list of tasks
./gradlew tasks
# Build, test the package
./gradlew clean test
# Build a full jar -- will be generated in build/libs/*-full.jar
./gradlew clean fatjar
# Build a standard jar
./gradlew clean jar
# Install the jar/sources/javadoc into your local maven repository
./gradlew clean install
```

If you try and play around with Gradle configuration files, in order to be
_really sure_ that your modifications are accounted for, add the
`--recompile-scripts` option before the task name; for instance:

```
./gradlew --recompile-scripts test
```

## Important note to Maven users

While there exists a possiblity to generate a `pom.xml` (using `./gradlew pom`),
such a pom.xml will not work reliably because of the dependency of this package
on the correct generation of META-INF/services/ files. See legacy/pom.xml for
details.

While you can adapt this file to work for you, please beware that it is _not_
supported by the author anymore.

## Usage in JetBrains IDEA

This package relies on service files to be generated. As such, IDEA's builtin
compiler does not do the job properly.

The build files add a task to support preparing the classes/resource files
(production as well as test) where IDEA expects them by default:

* production files in out/production/{projectName};
* test files in out/test/{projectName}.

As such, before any run/test configurations, you should configure IDEA so that
it run this command:

```
./gradlew prepareIdea
```

It will correctly generate all service files/compile everything/etc.

