package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.SchemaDescriptor;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;

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
