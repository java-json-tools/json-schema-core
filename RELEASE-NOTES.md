### 1.2.11

* Depend on jackson-coreutils 1.10.
* Update dependencies on jsr305.
* Make explicit the Guava and Jackson Databind dependencies.
* Fix javadoc generation
* Fix issue #55: `SchemaTree.setPointer` with missing path yields a tree with a null node.
* Delay load error bundle messages until there is an error.
* Fix issue #57: `Iterators.<T>emptyIterator()` removed from Guava.

### 1.2.10

* Fix issue #41: Remove Objects.toStringHelper to allow users of later versions of Guava to continue to operate.

### 1.2.9

TODO

### 1.2.8

TODO

### 1.2.7

TODO

### 1.2.6

TODO

### 1.2.5

* Fix issue #15: URNs were incorrectly normalized


### 1.2.4

* Introduce `SchemaKey`; allows to cure [issue #102 of
  -validator](https://github.com/fge/json-schema-validator/issues/102).

### 1.2.3

* Fix problems with resource loading.
* Add missing key to syntax messages.

### 1.2.1

* Fix bug with Rhino context: sealing the context can conflict with other
  packages which also do this, such as, for instance, WebLogic.

### 1.2.0

* New major version.

