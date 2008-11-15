package de.torq.clojure.jsr223;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.AbstractScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptContext;
import javax.script.Invocable;

public class ClojureScriptEngine extends AbstractScriptEngine
                                 implements Invocable
{
    public ClojureScriptEngine()
    {
        //Var.pushThreadBindings();
        // How to call popThreadBindings?
    }

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

    @Override // required by Invocable-interface
    public <T> T getInterface(Class<T> clasz)
    {
        return null;
    }

    @Override // required by Invocable-interface
    public <T> T getInterface(Object thiz, Class<T> clasz)
    {
        return null;
    }

    @Override // required by Invocable-interface
    public Object invokeFunction(String name, Object... args)
    {
        return null;
    }

    @Override // required by Invocable-interface
    public Object invokeMethod(Object thiz, String name, Object... args)
    {
        return null;
    }
}

