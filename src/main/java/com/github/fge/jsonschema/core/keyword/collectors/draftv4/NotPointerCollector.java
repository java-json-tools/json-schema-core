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

package com.github.fge.jsonschema.core.keyword.collectors.draftv4;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.github.fge.jsonschema.core.keyword.collectors.helpers.AbstractPointerCollector;

import java.util.Collection;

public final class NotPointerCollector
    extends AbstractPointerCollector
{
    private static final PointerCollector INSTANCE
        = new NotPointerCollector();

    private NotPointerCollector()
    {
        super("not");
    }

    public static PointerCollector getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void collect(final Collection<JsonPointer> pointers,
        final SchemaTree tree)
    {
        pointers.add(basePointer);
    }
}