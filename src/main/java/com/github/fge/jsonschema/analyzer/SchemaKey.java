package com.github.fge.jsonschema.analyzer;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.tree.SchemaTree;
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
