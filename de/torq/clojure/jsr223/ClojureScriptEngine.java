package de.torq.clojure.jsr223;

import java.io.Reader;
import java.io.StringReader;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.script.Bindings;
import javax.script.AbstractScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptContext;
import javax.script.Invocable;
import javax.script.ScriptException;

import clojure.lang.Var;

/**
 * The design of Clojure is somewhat special in that there is no way to get
 * some kind of context-object that serves as handle for a certain instance of
 * an engine; instead, clojure.lang.RT works by using thread-locals as handle.
 * This has the implication that we must manage internal handles to different
 * engines on a separate thread for each instance. In other words, the instance
 * of ClojureScriptEngine (which wraps RT) is a regular object, but internally
 * it has to communicate with a reference to RT on another thread. This is not
 * important to users of this class, I just felt a need to justify my rather
 * strange design.
 *
 * TODO: use java.util.concurrent.Executors.newSingleThreadExecutor
 */
public class ClojureScriptEngine extends AbstractScriptEngine
                                 implements Invocable
{

    private static final ScriptEngineFactory factory = new ClojureScriptEngineFactory();

    private AtomicBoolean closed = new AtomicBoolean(false);
    private Bindings engineBindings = null;
    private final ExecutorService executor;

    public ClojureScriptEngine()
    {
        // TODO: use ScriptContext's engineScope/globalScope; store bindings
        // locally; push on stack before each invocation and pop off stack
        // after each invocation of a method
        //Var.pushThreadBindings();
        // How to call popThreadBindings?
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Submit the given callable to our executor and block until we have the
     * result.
     */
    private Object submitAndGetResult(Callable<Object> c)
    {
        Future<Object> f = executor.submit(c);

        try
        {
            return f.get();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        catch (ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true if this ScriptEngine has been closed; further invocation of
     * any methods will have undefined behaviour.
     */
    public boolean isClosed()
    {
        return closed.get();
    }

    /*
     * Closes this ScriptEngine.
     * TODO: needed because I need to call Var.popThreadBindings when we are
     *       done with this instance; someone please find a better way...
     */
    public void close()
    {
        closed.compareAndSet(false, true);

        // Var.popThreadBindings();
    }

    @Override
    public ScriptEngineFactory getFactory()
    {
        return factory;
    }

    @Override
    public Bindings createBindings()
    {
        return new ClojureBindings();
    }

    @Override
    public Object eval(Reader reader, ScriptContext context)
    {
        CallableEval c = new CallableEval(reader, context);

        return submitAndGetResult(c);
    }

    @Override
    public Object eval(String script, ScriptContext context)
    {
        return eval(new StringReader(script), context);
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

class InternalEngine extends AbstractScriptEngine
                             implements Invocable
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

class CallableClojureInitialization implements Callable<Object>
{
    public CallableClojureInitialization()
    {
    }

    public Object call()
    {
        Var.pushThreadBindings(null);
        return null;
    }
}

class CallableClojureFinalization implements Callable<Object>
{
    public CallableClojureFinalization()
    {
    }

    public Object call()
    {
        Var.popThreadBindings();
        return null;
    }
}

class CallableEval implements Callable<Object>
{
    private final Reader reader;
    private final ScriptContext context;

    public CallableEval(Reader reader, ScriptContext context)
    {
        this.reader = reader;
        this.context = context;
    }

    public Object call()
    {
        return null;
    }
}

