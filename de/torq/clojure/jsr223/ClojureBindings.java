package de.torq.clojure.jsr223;

import javax.script.Bindings;
import java.util.HashMap;
import java.util.Map;

import clojure.lang.Associative;
import clojure.lang.PersistentHashMap;
import clojure.lang.RT;
import clojure.lang.Var;

// TODO:
// - support fully qualified names (as desribed in JSR 223 released version, p. 141)
public class ClojureBindings extends HashMap<String, Object>
                             implements Bindings
{

    public static final String nsUser = "user";
    public static final String nsClojure = "clojure.core";

    public ClojureBindings()
    {
        super();
    }

    public ClojureBindings(Associative a)
    {
        super();
    }

    /* Convert an arbitrary Bindings-implementation to an Associative-instance
     * (since JSR223 demands that we are able to accept any
     * Bindings-implementation.
     */
    public static Associative toAssociative(Bindings b)
    {
        Associative result = PersistentHashMap.create();
        for (Map.Entry<String, Object> e : b.entrySet())
        {
            Var var = RT.var(nsUser, e.getKey());
            result = result.assoc(var, var.get());
        }

        return result;
    }
}

