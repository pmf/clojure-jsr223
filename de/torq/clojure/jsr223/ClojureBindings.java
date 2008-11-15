package de.torq.clojure.jsr223;

import javax.script.Bindings;
import java.util.HashMap;

// TODO: use APersistentMap, since it implements Associative and therefore can
// be used to push and pop Clojure-bindings
public class ClojureBindings extends HashMap<String, Object>
                             implements Bindings
{
}

