package de.torq.clojure.jsr223;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import java.util.Properties;
import javax.script.ScriptEngine;

public class ClojureScriptEngineComponent {

  protected void activate(ComponentContext context) throws Exception {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(
        ClojureScriptEngineComponent.class.getClassLoader());
      ScriptEngine se = (ScriptEngine) context
        .getBundleContext()
        .getBundle()
        .loadClass("de.torq.clojure.jsr223.ClojureScriptEngine")
        .newInstance();
      context.getBundleContext().registerService(ScriptEngine.class.getName(), se, new Properties());
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }

    ServiceReference r = context.getBundleContext().getServiceReference(ScriptEngine.class.getName());
    ScriptEngine theEngine = (ScriptEngine)context.getBundleContext().getService(r);

    System.out.println(theEngine.eval("\"foo\"").toString());
  }

}
