/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.syntax.dictionaries;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.syntax.checkers.helpers.TypeOnlySyntaxChecker;
import com.github.fge.jsonschema.syntax.checkers.helpers.URISyntaxChecker;
import com.github.fge.jsonschema.syntax.checkers.hyperschema.LinksSyntaxChecker;
import com.github.fge.jsonschema.syntax.checkers.hyperschema.MediaSyntaxChecker;

/**
 * Draft v3 specific syntax checkers
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
