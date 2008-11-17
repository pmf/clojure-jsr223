package de.torq.clojure.jsr223;

import javax.script.Bindings;
import java.util.HashMap;

// TODO:
// - use PersistentHashMap, since it implements Associative and therefore can
//   be used to push and pop Clojure-bindings
// - support fully qualified names (as desribed in JSR 223 released version, p. 141)
//
public class ClojureBindings extends HashMap<String, Object>
                             implements Bindings
{
}

