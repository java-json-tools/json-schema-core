### 1.1.6

* `ProcessingMessage` can now have parameterized messages.
* Reorganize core/syntax bundles, improve.
* Make some syntax messages output parameterized messages.
* Remove deprecated code.
* Improvements to pom.xml.

### 1.1.5

* Use [msg-simple](https://github.com/fge/msg-simple) for all message needs, get
  rid of old enums.
* Syntax validation messages can be modified.
* Schema walking API changes.

### 1.1.4

* Convert all error messages to use a Java resource bundle.
* Maven artifact is now OSGi ready (thanks to [Matt
* Bishop](https://github.com/mbishop)).
* Full hyperschema syntax validation support (as a result, added dependency on
* [uri-template](https://github.com/fge/uri-template)).

### 1.1.3

* Move JSON Pointer support to, and depend on,
* [jackson-coreutils](https://github.com/fge/jackson-coreutils).
* Move JSON Patch to [another package](https://github.com/fge/json-patch).

### 1.1.2

* `.toString()` fix for `AbstractProcessingReport`.
* JSON Patch: fix array index adding.
* Error messages rework.
* Make `ProcessorMap` final.
* Start to implement configuration for schema walking.
* Update Guava dependency to 14.0.
* New class `RawProcessor`.
* One change to `SchemaListener`'s `.onExit()` event.

### 1.1.1

* JSON Patch implementation
* Rework `SchemaListener` events
* Improve `Processing{Message,Report}`'s `.toString()`

### 1.1.0

* First version of new development branch
* Change packages of ref resolving, syntax checking, others.
* Simplify schema walking listeners.
* Provide a more complete `.failOnError()` method in `ProcessorChain`.
* Update Jackson dependency to 2.1.4.
* Remove `SchemaHolder`; improve `ValueHolder`.

