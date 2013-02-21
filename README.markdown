<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<h2>What this is</h2>

<p>This package contains the core processing architecture used by the <a
href="https://github.com/fge/json-schema-validator">json-schema-validator library</a>, and which can
be used by other packages as well:</p>


* all the processing core interfaces: processor, processor chains, selectors;
* all the logging and exception infrastructure;
* JSON Reference and JSON Pointer;
* base classes for a JSON Schema and instance;
* other various interfaces.

<p>For more details on these components, see <a
href="https://github.com/fge/json-schema-core/wiki/ChangeLog">here</a>.

<p>A new package will be created showing this package in action.</p>

<h2>Version</h2>

<p>The current verson is <b>0.99.6</b> (<a
href="https://github.com/fge/json-schema-core/wiki/ChangeLog">ChangeLog</a>, <a
href="http://fge.github.com/json-schema-core/devel/index.html">Javadoc</a>).</p>

<h2>Maven artifact</h2>

<p>Replace <tt>your-version-here</tt> with the appropriate version:</p>

```xml
<dependency>
    <groupId>com.github.fge</groupId>
    <artifactId>json-schema-core</artifactId>
    <version>your-version-here</version>
</dependency>
```

