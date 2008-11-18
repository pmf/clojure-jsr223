Overview
========

My modest attempt at providing a Clojure-engine that is compliant with JSR 223
(the Java Scripting API). JSR 233 defines an API which makes it possible to
interchangably use any scripting-engine for which an implementation exists by
exposing objects from the host application to the scripting engine.

Implementations exist for example for JS, Groovy, Scala, and I think Clojure
should not be left out.

Status
======

Very rudimentary.

Implementation
==============

Due to Clojure's design (which uses thread-local bindings rather than a
dedicated context-object for different active runtimes), each
ClojureScriptEngine has a dedicated thread on which it executes all operations.

License
=======

Since I included parts of clojure.lang.Repl and use classes from
clojure.lang.*, all stuff is under the CPL (see CPL.txt).

