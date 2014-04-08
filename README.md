## Read me first

The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.

**NOTE**: this package uses [Gradle](http://www.gradle.org) as a build system. See file `BUILD.md`
for details.

## What this is

This package was first extracted from the [json-schema-validator
library](https://github.com/fge/json-schema-validator). Since then, however, it has become its own
beast.

The goal of this package is to provide a sufficiently comprehensive infrastructure to build
processing chains for anything JSON related, and JSON Schema related in particular. To this effect,
this package can be used, for instance, to perform the following:

* check the syntax of a JSON Schema;
* walk a JSON Schema and plug a listener into the walking process (BETA).

More details on this library can be found
[here](https://github.com/fge/json-schema-core/wiki/Architecture). Future plans can be viewed
[here](https://github.com/fge/json-schema-core/wiki/Roadmap). As always, ideas/contributions are
welcome!

You can see sample usages of this library in a [separate
project](https://github.com/fge/json-schema-processor-examples).

## Versions

The current stable verson is **1.0.3**
([ChangeLog](https://github.com/fge/json-schema-core/wiki/ChangeLog),
[Javadoc](http://fge.github.io/json-schema-core/stable/index.html)).

The current development verson is **1.1.10**
([ChangeLog](https://github.com/fge/json-schema-core/wiki/ChangeLog.devel),
[Javadoc](http://fge.github.io/json-schema-core/devel/index.html)).

Since 1.1.x, you can also download the jars on
[Bintray](https://bintray.com/fge/maven/json-schema-core).

## Using this project for gradle/maven

For gradle, use:

```
dependencies {
    compile(group: "com.github.fge", name: "json-shema-core", version: "your.version");
}
```

For maven:

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-core</artifactId>
    <version>your-version</version>
</dependency>
```

## Versioning scheme policy

The versioning scheme is defined by the **middle digit** of the version number:

* if this number is **even**, then this is the **stable** version; no new features will be
  added to such versions, and the user API will not change (save for some additions if requested).
* if this number is **odd**, then this is the **development** version; new features will be
  added to those versions only, **and the user API may change**.

