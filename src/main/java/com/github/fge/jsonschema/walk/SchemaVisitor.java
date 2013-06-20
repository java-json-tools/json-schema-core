package com.github.fge.jsonschema.walk;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.tree.SchemaTree;

public abstract class SchemaVisitor<T>
{
    protected final SchemaTree schemaTree;

    protected SchemaVisitor(final SchemaTree schemaTree)
    {
        this.schemaTree = schemaTree;
    }

    public final T visit(final SchemaWalker2 walker)
        throws ProcessingException
    {
        walker.accept(schemaTree, this);
        return getResult();
    }

    public abstract void enteringPath(final JsonPointer pointer)
        throws ProcessingException;

    public abstract void visitingPath(final SchemaTree tree);

    public abstract void exitingPath(final JsonPointer pointer);

    protected abstract T getResult();
}
