<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<h2>What this is</h2>

<p>This package contains the core processing architecture used by the <a
href="https://github.com/fge/json-schema-validator">json-schema-validator library</a>. While it is
not complete yet, it already contains some fundamental pieces:</p>


* all the processing core interfaces: processor, processor chains, selectors;
* all the logging and exception infrastructure;
* JSON Pointer.

<p>In the future, more processors will be written, the main goal of which is to plug this package
into a JSON Schema validator -- either the syntax validator (which will eventually be part of this
package) or the full chain.</p>

<h2>Version</h2>

<p>The current verson is <b>0.99.1</b>.</p>

<h2>Maven artifact</h2>

<p>Replace `your-version-here` with the appropriate version:</p>

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-core</artifactId>
    <version>your-version-here</version>
</dependency>
```

