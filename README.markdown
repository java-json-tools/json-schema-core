<h2>Read me first</h2>

<p>The license of this project is LGPLv3 or later. See file src/main/resources/LICENSE for the full
text.</p>

<h2>What this is</h2>

<p>This package was first extracted from the <a
href="https://github.com/fge/json-schema-validator">json-schema-validator library</a>. Since then,
however, it has become its own beast.</p>

<p>The goal of this package is to provide a sufficiently comprehensive infrastructure to build
processing chains for anything JSON related, and JSON Schema related in particular. To this effect,
this package can be used, for instance, to perform the following:</p>

<ul>
    <li>check the syntax of a JSON Schema;</li>
    <li>walk a JSON Schema and plug a listener into the walking process.</li>
</ul>

<p>But this does not stop there. This package also provides a complete, tested <a
href="http://tools.ietf.org/html/draft-ietf-appsawg-json-patch-10">JSON Patch</a>
implementation.</p>

<p>More details on this library can be found <a
href="https://github.com/fge/json-schema-core/wiki/Architecture">here</a>. Future plans can be
viewed <a
href="https://github.com/fge/json-schema-core/wiki/Roadmap">here</a>. As always, ideas/contributions
are welcome!</p>

<p>You can see sample usages of this library in a <a
href="https://github.com/fge/json-schema-processor-examples">separate project</a>.</p>

<h2>Versions</h2>

<p>The current stable verson is <b>1.0.2</b> (<a
href="https://github.com/fge/json-schema-core/wiki/ChangeLog">ChangeLog</a>, <a
href="http://fge.github.com/json-schema-core/stable/index.html">Javadoc</a>).</p>

<p>The current development verson is <b>1.1.3</b> (<a
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

