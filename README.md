[![License LGPLv3][LGPLv3 badge]][LGPLv3]
[![License ASL 2.0][ASL 2.0 badge]][ASL 2.0]
[![Build Status][Travis badge]][Travis]
![Maven Central](https://img.shields.io/maven-central/v/com.github.java-json-tools/json-schema-core.svg)


## Read me first

The license of this project is dual licensed [LGPLv3] or later/[ASL 2.0]. See file `LICENSE` for more
details. The full text of both licensed is included in the package.

## What this is

This package contains the core mechanics of [json-schema-validator
library](https://github.com/java-json-tools/json-schema-validator). It also provides a comprehensive
infrastructure to build processing chains for anything you can think of, really. To this effect,
this package can be used, for instance, to perform the following, provided you use the appropriate
software packages:

* generate a JSON Schema from a POJO, and then validate instances against that schema;
* transform different, related schema formats into JSON Schema, or the reverse (for instance Avro);
* conditional patching/deserialization;
* etc etc.

You can see sample usages of this library in a [separate
project](https://github.com/java-json-tools/json-schema-processor-examples) which is [demonstrated
online](http://json-schema-validator.herokuapp.com). More details on this library can
be found [here](https://github.com/java-json-tools/json-schema-core/wiki/Architecture).


## Versions

The current stable verson is **1.2.9**
([ChangeLog](https://github.com/java-json-tools/json-schema-core/wiki/ChangeLog_12x),
[Javadoc](http://java-json-tools.github.io/json-schema-core/1.2.x/index.html)).

The old verson is **1.0.4**
([ChangeLog](https://github.com/java-json-tools/json-schema-core/wiki/ChangeLog_10x),
[Javadoc](http://java-json-tools.github.io/json-schema-core/1.0.x/index.html)).

See [here](https://github.com/java-json-tools/json-schema-core/wiki/Whatsnew_12) for
the major changes between 1.0.x and 1.2.x.

## Using this project with gradle/maven

For gradle, use:

```
dependencies {
    compile(group: "com.github.java-json-tools", name: "json-shema-core", version: "1.2.8");
}
```

For maven:

```xml
<dependency>
    <groupId>com.github.java-json-tools</groupId>
    <artifactId>json-schema-core</artifactId>
    <version>1.2.8</version>
</dependency>
```

You can also get the jars from [Bintray](https://bintray.com/java-json-tools/maven/json-schema-core).

## Versioning scheme policy

The versioning scheme is defined by the **middle digit** of the version number:

* if this number is **even**, then this is the **stable** version; no new features will be
  added to such versions, and the user API will not change (save for some additions if requested).
* if this number is **odd**, then this is the **development** version; new features will be
  added to those versions only, **and the user API may change**.

[LGPLv3 badge]: https://img.shields.io/:license-LGPLv3-blue.svg
[LGPLv3]: http://www.gnu.org/licenses/lgpl-3.0.html
[ASL 2.0 badge]: https://img.shields.io/:license-Apache%202.0-blue.svg
[ASL 2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
[Travis Badge]: https://api.travis-ci.org/java-json-tools/json-schema-core.svg?branch=master
[Travis]: https://travis-ci.org/java-json-tools/json-schema-core
