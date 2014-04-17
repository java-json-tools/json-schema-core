## Read me first

The license of this project is dual licensed LGPLv3 or later/ASL 2.0. See file `LICENSE` for more
details. The full text of both licensed is included in the package.

**Version 1.2.0 is out**. See [here](https://github.com/fge/json-schema-core/wiki/Whatsnew_12) for
the major changes.

## What this is

This package contains the core mechanics of [json-schema-validator
library](https://github.com/fge/json-schema-validator). It also provides a comprehensive
infrastructure to build processing chains for anything you can think of, really. To this effect,
this package can be used, for instance, to perform the following, provided you use the appropriate
software packages:

* generate a JSON Schema from a POJO, and then validate instances against that schema;
* transform different, related schema formats into JSON Schema, or the reverse (for instance Avro);
* conditional patching/deserialization;
* etc etc.

You can see sample usages of this library in a [separate
project](https://github.com/fge/json-schema-processor-examples) which is [demonstrated
online](http://json-schema-validator.herokuapp.com). More details on this library can
be found [here](https://github.com/fge/json-schema-core/wiki/Architecture).


## Versions

The current stable verson is **1.2.1**
([ChangeLog](https://github.com/fge/json-schema-core/wiki/ChangeLog_12x),
[Javadoc](http://fge.github.io/json-schema-core/1.2.x/index.html)).

The old verson is **1.0.3**
([ChangeLog](https://github.com/fge/json-schema-core/wiki/ChangeLog_10x),
[Javadoc](http://fge.github.io/json-schema-core/1.0.x/index.html)).


## Using this project with gradle/maven

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

You can also get the jars from [Bintray](https://bintray.com/fge/maven/json-schema-core).

## Versioning scheme policy

The versioning scheme is defined by the **middle digit** of the version number:

* if this number is **even**, then this is the **stable** version; no new features will be
  added to such versions, and the user API will not change (save for some additions if requested).
* if this number is **odd**, then this is the **development** version; new features will be
  added to those versions only, **and the user API may change**.

