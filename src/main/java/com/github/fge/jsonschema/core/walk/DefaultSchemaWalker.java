package com.github.fge.jsonschema.core.walk;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;

public final class DefaultSchemaWalker
    extends SchemaWalker
{
    public DefaultSchemaWalker(final SchemaDescriptor descriptor)
    {
        super(descriptor);
    }

    @Override
    protected SchemaTree resolveTree(final SchemaTree tree,
        final ProcessingReport report)
        throws ProcessingException
    {
        return tree;
    }

    @Override
    public String toString()
    {
        return "default schema walker";
    }
}
