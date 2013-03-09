<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<h2>What this is</h2>

<p>This package contains the core architecture use by the <a
href="https://github.com/fge/json-schema-validator">json-schema-validator library</a>.</p>

<p>For more details on this library, see <a
href="https://github.com/fge/json-schema-core/wiki/Architecture">this page</a>. The roadmap for this
library can be seen <a
href="https://github.com/fge/json-schema-core/wiki/Roadmap">hee</a>.

<p>You can see sample usages of this library in a <a
href="https://github.com/fge/json-schema-processor-examples">separate project</a>.</p>

<h2>Versions</h2>

<p>The current stable verson is <b>1.0.2</b> (<a
href="https://github.com/fge/json-schema-core/wiki/ChangeLog">ChangeLog</a>, <a
href="http://fge.github.com/json-schema-core/stable/index.html">Javadoc</a>).</p>

<p>The current development verson is <b>1.1.1</b> (<a
href="https://github.com/fge/json-schema-core/wiki/ChangeLog.devel">ChangeLog</a>, <a
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

<h2>Versioning scheme policy</h2>

<p>The versioning scheme is defined by the <b>middle digit</b> of the version number:</p>

* if this number is <b>even</b>, then this is the <b>stable</b> version; no new features will be
  added to such versions, and the user API will not change (save for some additions if requested).
* if this number is <b>odd</b>, then this is the <b>development</b> version; new features will be
  added to those versions only, <b>and the user API may change</b>.

