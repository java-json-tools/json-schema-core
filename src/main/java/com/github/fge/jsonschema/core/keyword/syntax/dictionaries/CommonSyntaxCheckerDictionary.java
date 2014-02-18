/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.keyword.syntax.dictionaries;

import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.common.AdditionalSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.common.EnumSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.common.ExclusiveMaximumSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.common.ExclusiveMinimumSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.common.PatternPropertiesSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.common.PatternSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.PositiveIntegerSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.TypeOnlySyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.URISyntaxChecker;

import static com.github.fge.jackson.NodeType.*;

/**
 * Syntax checkers common to draft v4 and v3
 */
public final class CommonSyntaxCheckerDictionary
{
    private static final Dictionary<SyntaxChecker> DICTIONARY;

    public static Dictionary<SyntaxChecker> get()
    {
        return DICTIONARY;
    }

    private CommonSyntaxCheckerDictionary()
    {
    }

    static {
        final DictionaryBuilder<SyntaxChecker> dict = Dictionary.newBuilder();

        String keyword;
        SyntaxChecker checker;

        /*
         * Arrays
         */

        keyword = "additionalItems";
        checker = new AdditionalSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "minItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "maxItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "uniqueItems";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN);
        dict.addEntry(keyword, checker);

        /*
         * Integers and numbers
         */
        keyword = "minimum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        dict.addEntry(keyword, checker);

        keyword = "exclusiveMinimum";
        checker = ExclusiveMinimumSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        keyword = "maximum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        dict.addEntry(keyword, checker);

        keyword = "exclusiveMaximum";
        checker = ExclusiveMaximumSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        /*
         * Objects
         */
        keyword = "additionalProperties";
        checker = new AdditionalSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "patternProperties";
        checker = PatternPropertiesSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        keyword = "required";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN);
        dict.addEntry(keyword, checker);

        /*
         * Strings
         */
        keyword = "minLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "maxLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "pattern";
        checker = PatternSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        /*
         * All/metadata
         */
        keyword = "$schema";
        checker = new URISyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "$ref";
        checker = new URISyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "id";
        checker = new URISyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "description";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        dict.addEntry(keyword, checker);

        keyword = "title";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        dict.addEntry(keyword, checker);

        keyword = "enum";
        checker = EnumSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        keyword = "format";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        dict.addEntry(keyword, checker);

        // FIXME: we actually ignore this one
        keyword = "default";
        checker = new TypeOnlySyntaxChecker(keyword, ARRAY, values());
        dict.addEntry(keyword, checker);

        DICTIONARY = dict.freeze();
    }
}
