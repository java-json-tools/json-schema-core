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

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.TypeOnlySyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.URISyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.hyperschema.LinksSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.hyperschema.MediaSyntaxChecker;

/**
 * Draft v4 hyperschema specific syntax checkers
 */
public final class DraftV4HyperSchemaSyntaxCheckerDictionary
{
    private static final Dictionary<SyntaxChecker> DICTIONARY;

    public static Dictionary<SyntaxChecker> get()
    {
        return DICTIONARY;
    }

    private DraftV4HyperSchemaSyntaxCheckerDictionary()
    {
    }

    static {
        final DictionaryBuilder<SyntaxChecker> builder
            = Dictionary.newBuilder();

        /*
         * Put all common checkers
         */
        builder.addAll(DraftV4SyntaxCheckerDictionary.get());

        String keyword;
        SyntaxChecker checker;

        keyword = "pathStart";
        checker = new URISyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "fragmentResolution";
        checker = new TypeOnlySyntaxChecker(keyword, NodeType.STRING);
        builder.addEntry(keyword, checker);

        keyword = "media";
        checker = MediaSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "links";
        checker = LinksSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        DICTIONARY = builder.freeze();
    }
}
