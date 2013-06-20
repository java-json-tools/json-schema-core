package com.github.fge.jsonschema.walk;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.tree.SchemaTree;

public abstract class SchemaVisitor
{
    protected final SchemaTree schemaTree;

    protected SchemaVisitor(final SchemaTree schemaTree)
    {
        this.schemaTree = schemaTree;
    }

    public abstract void enteringPath(final JsonPointer pointer)
        throws ProcessingException;

    public abstract void visitingPath(final SchemaTree tree);

    public abstract void exitingPath(final JsonPointer pointer);
}
