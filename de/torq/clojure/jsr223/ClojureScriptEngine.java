package de.torq.clojure.jsr223;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.AbstractScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptContext;

public class ClojureScriptEngine extends AbstractScriptEngine
{
    @Override
    public ScriptEngineFactory getFactory()
    {
        return new ClojureScriptEngineFactory();
    }

    @Override
    public Bindings createBindings()
    {
        return new ClojureBindings();
    }

    @Override
    public Object eval(Reader reader, ScriptContext context)
    {
        return null;
    }

    @Override
    public Object eval(String script, ScriptContext context)
    {
        return null;
    }

}

