Overview
========

My modest attempt at providing a Clojure-engine that is compliant with JSR 223
(the Java Scripting API). JSR 233 defines an API which makes it possible to
interchangeably use any scripting-engine for which an implementation exists by
exposing objects from the host application to the scripting engine.

Implementations exist for example for JS, Groovy, Scala, and I think Clojure
should not be left out.

Building
========

NOTE: Requires ant 1.7.0+ (due to the service-task, which only exists since
1.7.0). Sorry for any inconvenience this might cause.

Builds clojure-jsr223.jar, which will contain the implementation of a JSR
223-compliant wrapper around Clojure.  To compile and use it, you must have
built the regular clojure.jar and added it to your classpath.

To test it, you can use something like:

    jrunscript -cp clojure-jsr223.jar:/my/path/to/clojure.jar -l "Clojure"


Status
======

ENGINE_SCOPE bindings work.

Implementation
==============

Due to Clojure's design (which uses thread-local bindings rather than a
dedicated context-object for different active runtimes), each
ClojureScriptEngine has a dedicated thread on which it executes all operations.

License
=======

Since I included parts of clojure.lang.Repl and use classes from
clojure.lang.*, all stuff is under the CPL (see CPL.txt).

