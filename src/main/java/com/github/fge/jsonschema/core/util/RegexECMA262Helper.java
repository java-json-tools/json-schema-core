/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import javax.annotation.concurrent.ThreadSafe;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.regex.Pattern;

/**
 * <p>ECMA 262 validation helper. A script engine is used instead of {@link
 * java.util.regex} because the latter doesn't comply with ECMA 262:</p>
 *
 * <ul>
 *     <li>ECMA 262 doesn't have {@link Pattern#DOTALL};</li>
 *     <li>ECMA 262 doesn't have "possessive" quantifiers ({@code ++},
 *     {@code ?+}, etc);</li>
 *     <li>there is only one word delimiter in ECMA 262, which is {@code \b};
 *     {@code \<} (for beginning of word) and {@code \>} (for end of word) are
 *     not understood.</li>
 * </ul>
 *
 * <p>And many, many other things. See
 * <a href="http://www.regular-expressions.info/javascript.html">here</a> for
 * the full story. And if you don't yet have Jeffrey Friedl's "Mastering regular
 * expressions", just <a href="http://regex.info">buy it</a> :p</p>
 *
 * <p>As script engine is used either GraalJS, Nashorn or Rhino as their
 * fallback. Nashorn is only available on Java 8 up to 14.</p>
 *
 * <p>GraalJS is the first choice as it supports more RegExp features, e.g.
 * lookbehind assertions, than both alternatives.</p>
 *
 * <p>Rhino is the fallback as it is tremendously slower than Nashorn.</p>
 */
@ThreadSafe
public final class RegexECMA262Helper
{
    private static final String REGEX_IS_VALID_FUNCTION_NAME = "regexIsValid";

    private static final String REG_MATCH_FUNCTION_NAME = "regMatch";

    /**
     * JavaScript scriptlet defining functions for validating a regular
     * expression and for matching an input against a regular expression.
     */
    private static final String jsAsString
        = "function " + REGEX_IS_VALID_FUNCTION_NAME + "(re)"
        + '{'
        + "    try {"
        + "         new RegExp(re);"
        + "         return true;"
        + "    } catch (e) {"
        + "        return false;"
        + "    }"
        + '}'
        + ""
        + "function " + REG_MATCH_FUNCTION_NAME + "(re, input)"
        + '{'
        + "    return new RegExp(re).test(input);"
        + '}';

    private static final RegexScript REGEX_SCRIPT = determineRegexScript();

    private RegexECMA262Helper()
    {
    }

    private static RegexScript determineRegexScript()
    {
        try {
            return new GraalJsScript();
        } catch(final ScriptException e) {
            // most probably GraalJS is simply not available
        }
        try {
            return new NashornScript();
        } catch(final ScriptException e) {
            // most probably Nashorn is simply not available
        }
        return new RhinoScript();
    }

    /**
     * Validate that a regex is correct
     *
     * @param regex the regex to validate
     * @return true if the regex is valid
     */
    public static boolean regexIsValid(final String regex)
    {
        return REGEX_SCRIPT.regexIsValid(regex);
    }

    /**
     * <p>Matches an input against a given regex, in the <b>real</b> sense
     * of matching, that is, the regex can match anywhere in the input. Java's
     * {@link java.util.regex} makes the unfortunate mistake to make people
     * believe that matching is done on the whole input... Which is not true.
     * </p>
     *
     * <p>Also note that the regex MUST have been validated at this point
     * (using {@link #regexIsValid(String)}).</p>
     *
     * @param regex the regex to use
     * @param input the input to match against (and again, see description)
     * @return true if the regex matches the input
     */
    public static boolean regMatch(final String regex, final String input)
    {
        return REGEX_SCRIPT.regMatch(regex, input);
    }

    private interface RegexScript
    {
        boolean regexIsValid(String regex);

        boolean regMatch(String regex, String input);
    }

    private static abstract class ScriptEngineScript implements RegexScript
    {
        /**
         * Script engine
         */
        private final Invocable scriptEngine;

        private ScriptEngineScript(final String engineName) throws ScriptException
        {
            final ScriptEngine engine = new ScriptEngineManager()
                    .getEngineByName(engineName);
            if(engine == null) {
                throw new ScriptException("ScriptEngine '" + engineName + "' not found.");
            }
            engine.eval(jsAsString);
            this.scriptEngine = (Invocable) engine;
        }

        boolean invoke(final String function, final Object... values)
        {
            try {
                return (Boolean) scriptEngine.invokeFunction(function,
                        values);
            } catch(final ScriptException e) {
                throw new IllegalStateException(
                        "Unexpected error on invoking Script.", e);
            } catch(final NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Unexpected error on invoking Script.", e);
            }
        }

        @Override
        public boolean regexIsValid(final String regex)
        {
            return invokeScriptEngine(REGEX_IS_VALID_FUNCTION_NAME, regex);
        }

        @Override
        public boolean regMatch(final String regex, final String input)
        {
            return invokeScriptEngine(REG_MATCH_FUNCTION_NAME, regex, input);
        }

        abstract boolean invokeScriptEngine(final String function, final Object... values);
    }

    private static class GraalJsScript extends ScriptEngineScript
    {
        private GraalJsScript() throws ScriptException
        {
            super("graal.js");
        }

        // GraalJS works single-threaded. The synchronized ensures this.
        @Override
        synchronized boolean invokeScriptEngine(final String function,
                                                final Object... values) {
            return invoke(function, values);
        }
    }

    private static class NashornScript extends ScriptEngineScript
    {
        private NashornScript() throws ScriptException
        {
            super("nashorn");
        }

        @Override
        boolean invokeScriptEngine(final String function,
                                   final Object... values) {
            return invoke(function, values);
        }
    }

    private static class RhinoScript implements RegexScript
    {
        /**
         * Script scope
         */
        private final Scriptable scope;

        /**
         * Reference to Javascript function for regex validation
         */
        private final Function regexIsValid;

        /**
         * Reference to Javascript function for regex matching
         */
        private final Function regMatch;

        private RhinoScript()
        {
            final Context ctx = Context.enter();
            try {
                this.scope = ctx.initStandardObjects(null, false);
                try {
                    ctx.evaluateString(scope, jsAsString, "re", 1, null);
                } catch(final UnsupportedOperationException e) {
                    // See: http://stackoverflow.com/questions/3859305/problems-using-rhino-on-android
                    ctx.setOptimizationLevel(-1);
                    ctx.evaluateString(scope, jsAsString, "re", 1, null);
                }
                this.regexIsValid = (Function)
                        scope.get(REGEX_IS_VALID_FUNCTION_NAME, scope);
                this.regMatch = (Function)
                        scope.get(REG_MATCH_FUNCTION_NAME, scope);
            } finally {
                Context.exit();
            }
        }

        private boolean invokeScriptEngine(final Function function,
                                           final Object... values)
        {
            final Context context = Context.enter();
            try {
                return (Boolean) function.call(context, scope, scope, values);
            } finally {
                Context.exit();
            }
        }

        @Override
        public boolean regexIsValid(final String regex)
        {
            return invokeScriptEngine(regexIsValid, regex);
        }

        @Override
        public boolean regMatch(final String regex, final String input)
        {
            return invokeScriptEngine(regMatch, regex, input);
        }
    }
}
