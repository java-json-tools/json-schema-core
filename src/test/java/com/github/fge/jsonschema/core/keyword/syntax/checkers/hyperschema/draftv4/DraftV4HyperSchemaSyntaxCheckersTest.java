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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.hyperschema.draftv4;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxCheckersTest;
import com.github.fge.jsonschema.core.keyword.syntax.dictionaries.DraftV4HyperSchemaSyntaxCheckerDictionary;

public abstract class DraftV4HyperSchemaSyntaxCheckersTest
    extends SyntaxCheckersTest
{
    protected DraftV4HyperSchemaSyntaxCheckersTest(final String keyword)
        throws JsonProcessingException
    {
        super(DraftV4HyperSchemaSyntaxCheckerDictionary.get(),
            "hyperschema/draftv4", keyword);
    }
}
