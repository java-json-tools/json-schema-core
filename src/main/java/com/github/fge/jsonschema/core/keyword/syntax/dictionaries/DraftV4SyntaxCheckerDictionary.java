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

package com.github.fge.jsonschema.core.keyword.syntax.dictionaries;

import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.DefinitionsSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.DraftV4DependenciesSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.DraftV4ItemsSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.DraftV4PropertiesSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.DraftV4TypeSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.NotSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4.RequiredSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.DivisorSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.PositiveIntegerSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.SchemaArraySyntaxChecker;

/**
 * Draft v4 specific syntax checkers
 */
public final class DraftV4SyntaxCheckerDictionary
{
    private static final Dictionary<SyntaxChecker> DICTIONARY;

    public static Dictionary<SyntaxChecker> get()
    {
        return DICTIONARY;
    }

    private DraftV4SyntaxCheckerDictionary()
    {
    }

    static {
        final DictionaryBuilder<SyntaxChecker> builder
            = Dictionary.newBuilder();

        /*
         * Put all common checkers
         */
        builder.addAll(CommonSyntaxCheckerDictionary.get());

        String keyword;
        SyntaxChecker checker;

        /*
         * Array
         */
        keyword = "items";
        checker = DraftV4ItemsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * Integers and numbers
         */
        keyword = "multipleOf";
        checker = new DivisorSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        /*
         * Objects
         */
        keyword = "minProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "maxProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "properties";
        checker = DraftV4PropertiesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "required";
        checker = RequiredSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "dependencies";
        checker = DraftV4DependenciesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * All / metadata
         */
        keyword = "allOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "anyOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "oneOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "not";
        checker = NotSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "definitions";
        checker = DefinitionsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "type";
        checker = DraftV4TypeSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        DICTIONARY = builder.freeze();
    }
}
