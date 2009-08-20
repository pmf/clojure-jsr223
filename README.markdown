Overview
========

My modest attempt at providing a Clojure-engine that is compliant with JSR 223
(the Java Scripting API). JSR 233 defines an API which makes it possible to
interchangeably use any scripting-engine for which an implementation exists by
exposing objects from the host application to the scripting engine.

Implementations exist for example for JS, Jython, JRuby, Groovy, Scala, and I
think Clojure should not be left out. A long list of languages for which JSR
223 engines exists can be found at
[the Java scripting site](https://scripting.dev.java.net/).

Building
========

NOTE: Requires ant 1.7.0+ (due to the service-task, which only exists since
1.7.0). Sorry for any inconvenience this might cause.

Builds clojure-jsr223.jar, which will contain the implementation of a JSR
223-compliant wrapper around Clojure.  To compile and use it, you must have
built the regular clojure.jar and added it to your classpath.

To build as an OSGi-component, you have to have the OSGi-framework stuff
somewhere on your classpath (if you are using Equinox, org.eclipse.osgi_xxx.jar
and org.eclipse.osgi.services_xxx.jar should be enough). The OSGi-bundle will
include the contents of clojure.jar, which is why you need to point the
Ant-flag clojure_jar to your clojure library (as opposed to the regular
clojure-jsr223.jar, which requires clojure.jar to be on the classpath to run).

To test it, you can use something like:

    jrunscript -cp clojure-jsr223.jar:/my/path/to/clojure.jar -l "Clojure"


Status
======

Engine-internal bindings work.

Implementation
==============

Due to Clojure's design (which uses thread-local bindings rather than a
dedicated context-object for different active runtimes), each
ClojureScriptEngine has a dedicated thread on which it executes all operations.

License
=======

Since I included parts of clojure.lang.Repl and use classes from
the clojure.lang-package, all stuff is under the CPL (see CPL.txt).

