import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngine;
import java.util.List;
import java.util.ArrayList;

public class ClojureScriptEngineFactory implements ScriptEngineFactory
{
    private static final String engineName = "Clojure";
    private static final String engineVersion = "SVN HEAD";
    private static final String languageName = engineName;
    private static final String languageVersion = engineVersion;
    private static final List<String> fileExtensions = new ArrayList<String>() {{ add("clj"); }};
    private static final List<String> mimeTypes = new ArrayList<String>() {{ add("text/plain"); }};
    private static final List<String> nickNames = new ArrayList<String>() {{ add("Clojure"); add("clojure"); add("clj"); }};

    @Override
    public String getEngineName()
    {
        return engineName;
    }

    @Override
    public String getEngineVersion()
    {
        return engineVersion;
    }

    @Override
    public String getLanguageName()
    {
        return languageName;
    }

    @Override
    public String getLanguageVersion()
    {
        return languageVersion;
    }

    @Override
    public List<String> getExtensions()
    {
        return fileExtensions;
    }

    @Override
    public List<String> getMimeTypes()
    {
        return mimeTypes;
    }

    @Override
    public List<String> getNames()
    {
        return nickNames;
    }

    @Override
    public String getOutputStatement(String toDisplay)
    {
        return "(print \"" + toDisplay + "\")";
    }

    @Override
    public String getProgram(String... statements)
    {
        StringBuilder result = new StringBuilder("");

        for (int i = 0; i < statements.length; i++)
        {
            result.append(statements[i] + "\n");
        }

        return result.toString();
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args)
    {
        return "";
    }

    @Override
    public Object getParameter(String key)
    {
        return null;
    }

    @Override
    public ScriptEngine getScriptEngine()
    {
        return null;
    }

}

