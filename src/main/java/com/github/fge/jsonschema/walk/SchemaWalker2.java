package com.github.fge.jsonschema.walk;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public abstract class SchemaWalker2
{
    protected final Map<String, PointerCollector> pointerCollectors
        = Maps.newTreeMap();

    protected SchemaWalker2(final Dictionary<PointerCollector> dict)
    {
        pointerCollectors.putAll(dict.entries());
    }

    public final <T> void accept(final SchemaTree tree,
        final SchemaVisitor<T> visitor)
        throws ProcessingException
    {
        doWalk(tree, visitor, JsonPointer.empty());
    }

    private <T> void doWalk(final SchemaTree tree,
        final SchemaVisitor<T> visitor, final JsonPointer pointer)
        throws ProcessingException
    {
        final SchemaTree newTree;

        visitor.enteringPath(pointer);
        newTree = resolveTree(tree);
        visitor.visitingPath(newTree);

        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final Map.Entry<String, PointerCollector> entry:
            pointerCollectors.entrySet())
            if (tree.getNode().has(entry.getKey()))
                entry.getValue().collect(pointers, tree);

        for (final JsonPointer ptr: pointers)
            doWalk(tree.append(ptr), visitor, pointer.append(ptr));

        visitor.exitingPath(pointer);
    }

    protected abstract SchemaTree resolveTree(final SchemaTree tree)
        throws ProcessingException;
}
