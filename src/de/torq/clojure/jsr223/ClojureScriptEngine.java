package de.torq.clojure.jsr223;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import clojure.lang.Associative;
import clojure.lang.Compiler;
import clojure.lang.LineNumberingPushbackReader;
import clojure.lang.LispReader;
import clojure.lang.RT;
import clojure.lang.Symbol;
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
 * TODO:
 * - How to retrieve a map of current bindings? Use namespace-stuff?
 * - get("a") can probably be implemented by simply evaluating "a"
 * - What's best for set("a", (Object)a)? Use Var.find and Var.set?
 *
 */
public class ClojureScriptEngine extends AbstractScriptEngine
                                 implements Invocable
{

    private static final ScriptEngineFactory factory = new ClojureScriptEngineFactory();

    private AtomicBoolean closed = new AtomicBoolean(false);
    private final ExecutorService executor;

    // BEGIN From clojure.lang.Repl
    static final Symbol USER = Symbol.create(ClojureBindings.nsUser);
    static final Symbol CLOJURE = Symbol.create(ClojureBindings.nsClojure);

    static final Var in_ns = RT.var(ClojureBindings.nsClojure, "in-ns");
    static final Var refer = RT.var(ClojureBindings.nsClojure, "refer");
    static final Var ns = RT.var(ClojureBindings.nsClojure, "*ns*");
    static final Var compile_path = RT.var(ClojureBindings.nsClojure, "*compile-path*");
    static final Var warn_on_reflection = RT.var(ClojureBindings.nsClojure, "*warn-on-reflection*");
    static final Var print_meta = RT.var(ClojureBindings.nsClojure, "*print-meta*");
    static final Var print_length = RT.var(ClojureBindings.nsClojure, "*print-length*");
    static final Var print_level = RT.var(ClojureBindings.nsClojure, "*print-level*");
    // END From clojure.lang.Repl

    private ClojureBindings engineBindings = new ClojureBindings();
    private static final Associative globalBindings = RT.map(
        ns, ns.get(),
        warn_on_reflection, warn_on_reflection.get(),
        print_meta, print_meta.get(),
        print_length, print_length.get(),
        print_level, print_level.get(),
        compile_path, "classes"
    );

    public ClojureScriptEngine()
    {
        // TODO: use ScriptContext's engineScope/globalScope; store bindings
        // locally; push on stack before each invocation and pop off stack
        // after each invocation of a method
        //Var.pushThreadBindings();
        executor = Executors.newSingleThreadExecutor();
        submitAndGetResult(new CallableClojureInitialization(globalBindings));
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
     * We need to perform some cleanup (Clojure-related stuff has to be done on
     * the executor's thread).
     */
    protected void finalize() throws Throwable
    {
        System.out.println("entering: finalize()");
        submitAndGetResult(new CallableClojureFinalization());
        // TODO: we need to make sure the thread is interruptible;
        // newSingleThreadExecutor does not make any guarantees about that, so
        // we need to find another mechanism for spawning the thread.
        // TODO: maybe the thread-locals (in clojure.lang.Var) have some
        // implicit or explicit reference to the thread.
        executor.shutdownNow();
        System.out.println("leaving: finalize()");
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
        //System.out.println("entering: eval(String script, ScriptContext context)");
        Object result = eval(new StringReader(script), context);
        //System.out.println("leaving: eval(String script, ScriptContext context)");
        return result;
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
    private final Associative bindings;

    public CallableClojureInitialization(Associative bindings)
    {
        this.bindings = bindings;
    }

    public Object call()
    {
        try
        {
            Var.pushThreadBindings(bindings);
            ClojureScriptEngine.in_ns.invoke(ClojureScriptEngine.USER);
            ClojureScriptEngine.refer.invoke(ClojureScriptEngine.CLOJURE);

            return null;
        }
        catch (Exception e )
        {
            throw new RuntimeException(e);
        }
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
        return handleInput(ClojureBindings.toAssociative(context.getBindings(ScriptContext.ENGINE_SCOPE)));
    }

    private Object handleInput(Associative a)
    {
        Object result = null;
        try
        {
            Var.pushThreadBindings(a);

            //repl IO support
            LineNumberingPushbackReader rdr = new LineNumberingPushbackReader(reader);
            Writer w = context.getWriter();
            Object EOF = new Object();

            //start the loop
            for(; ;)
            {
                try
                {
                    Object r = LispReader.read(rdr, false, EOF, false);
                    if(r == EOF)
                    {
                        w.flush();
                        break;
                    }
                    Object ret = Compiler.eval(r);
                    result = ret;
                }
                catch(Throwable e)
                {
                    Throwable c = e;
                    while(c.getCause() != null)
                    {
                        c = c.getCause();
                    }
                    ((PrintWriter) context.getErrorWriter()).println(e instanceof Compiler.CompilerException ? e : c);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            Var.popThreadBindings();
        }

        return result;
    }
}
