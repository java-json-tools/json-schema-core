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
 * <p>As script engine is used either Nashorn or Rhino as its fallback.
 * Nashorn is only available on Java 8 runtimes or higher.</p>
 *
 * <p>Rhino is only the fallback as it is tremendously slower.</p>
 */
@ThreadSafe
public final class RegexECMA262Helper
{
    private static final String REGEX_IS_VALID_FUNCTION_NAME = "regexIsValid";

    private static final String REG_MATCH_FUNCTION_NAME = "regMatch";

    /**
     * JavaScript scriptlet defining functions {@link #REGEX_IS_VALID}
     * and {@link #REG_MATCH}
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

    /**
     * Script scope
     */
    private static final Scriptable SCOPE;

    /**
     * Reference to Javascript function for regex validation
     */
    private static final Function REGEX_IS_VALID;

    /**
     * Reference to Javascript function for regex matching
     */
    private static final Function REG_MATCH;

    private static final Invocable PRIMARY_SCRIPT_ENGINE;

    private RegexECMA262Helper()
    {
    }

    static {
        PRIMARY_SCRIPT_ENGINE = tryResolvePrimaryEngine();

        final Context ctx = Context.enter();
        try {
            SCOPE = ctx.initStandardObjects(null, false);
            try {
                ctx.evaluateString(SCOPE, jsAsString, "re", 1, null);
            } catch(UnsupportedOperationException e) {
                // See: http://stackoverflow.com/questions/3859305/problems-using-rhino-on-android
                ctx.setOptimizationLevel(-1);
                ctx.evaluateString(SCOPE, jsAsString, "re", 1, null);
            }
            REGEX_IS_VALID = (Function) SCOPE.get(REGEX_IS_VALID_FUNCTION_NAME, SCOPE);
            REG_MATCH = (Function) SCOPE.get(REG_MATCH_FUNCTION_NAME, SCOPE);
        } finally {
            Context.exit();
        }
    }

    private static Invocable tryResolvePrimaryEngine() {
        final ScriptEngine engine = new ScriptEngineManager()
                .getEngineByName("nashorn");
        if(engine != null) {
            try {
                engine.eval(jsAsString);
                return (Invocable) engine;
            } catch(final ScriptException e) {
                // the script can't be parsed - the script engine can't be used
            }
        }
        return null;
    }

    /**
     * Validate that a regex is correct
     *
     * @param regex the regex to validate
     * @return true if the regex is valid
     */
    public static boolean regexIsValid(final String regex)
    {
        if(PRIMARY_SCRIPT_ENGINE != null)
        {
            return invokeScriptEngine(REGEX_IS_VALID_FUNCTION_NAME, regex);
        }
        return invokeFallbackEngine(REGEX_IS_VALID, regex);
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
        if(PRIMARY_SCRIPT_ENGINE != null)
        {
            return invokeScriptEngine(REG_MATCH_FUNCTION_NAME, regex, input);
        }
        return invokeFallbackEngine(REG_MATCH, regex, input);
    }

    private static boolean invokeScriptEngine(final String function,
                                              final Object... values)
    {
        try {
            return (Boolean) PRIMARY_SCRIPT_ENGINE.invokeFunction(function,
                    values);
        } catch(final ScriptException e) {
            throw new IllegalStateException(
                    "Unexpected error on invoking Script.", e);
        } catch(final NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Unexpected error on invoking Script.", e);
        }
    }

    private static boolean invokeFallbackEngine(final Function function,
                                                final Object... values)
    {
        final Context context = Context.enter();
        try {
            return (Boolean) function.call(context, SCOPE, SCOPE, values);
        } finally {
            Context.exit();
        }
    }
}
