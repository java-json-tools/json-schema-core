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

package com.github.fge.jsonschema.core.analyzer;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.google.common.annotations.Beta;
import com.google.common.primitives.Longs;

@Beta
final class SchemaKey
{
    private final SchemaTree tree;
    private final long id;
    private final JsonPointer ptr;

    static SchemaKey atRoot(final SchemaTree tree)
    {
        return new SchemaKey(tree, JsonPointer.empty());
    }

    static SchemaKey atPointer(final SchemaTree tree)
    {
        return new SchemaKey(tree);
    }

    private SchemaKey(final SchemaTree tree, final JsonPointer ptr)
    {
        this.tree = tree;
        id = tree.getId();
        this.ptr = ptr;
    }

    private SchemaKey(final SchemaTree tree)
    {
        this(tree, tree.getPointer());
    }

    SchemaTree getTree()
    {
        return tree;
    }

    @Override
    public int hashCode()
    {
        return 31 * Longs.hashCode(id) + ptr.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj.getClass() != getClass())
            return false;
        final SchemaKey other = (SchemaKey) obj;
        return id == other.id && ptr.equals(other.ptr);
    }

    @Override
    public String toString()
    {
        return "id: " + id + ", ptr: " + ptr;
    }
}
