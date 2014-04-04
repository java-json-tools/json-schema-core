/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
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
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3.DraftV3DependenciesSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3.DraftV3ItemsSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3.DraftV3PropertiesSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3.DraftV3TypeKeywordSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3.ExtendsSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.DivisorSyntaxChecker;

/**
 * Draft v3 specific syntax checkers
 */
public final class DraftV3SyntaxCheckerDictionary
{
    private static final Dictionary<SyntaxChecker> DICTIONARY;

    public static Dictionary<SyntaxChecker> get()
    {
        return DICTIONARY;
    }

    private DraftV3SyntaxCheckerDictionary()
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
         * Draft v3 specific syntax checkers
         */

        /*
         * Arrays
         */
        keyword = "items";
        checker = DraftV3ItemsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * Numbers and integers
         */
        keyword = "divisibleBy";
        checker = new DivisorSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        /*
         * Objects
         */
        keyword = "properties";
        checker = DraftV3PropertiesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "dependencies";
        checker = DraftV3DependenciesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * All / metadata
         */
        keyword = "extends";
        checker = ExtendsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "type";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "disallow";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        DICTIONARY = builder.freeze();
    }
}
