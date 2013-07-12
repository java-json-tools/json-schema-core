package com.github.fge.jsonschema.loader.read;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

@Beta
public abstract class SchemaReader
{
    protected static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    public abstract SchemaTree read(final JsonRef ref, final InputStream in)
        throws IOException;

    public final SchemaTree read(final InputStream in)
        throws IOException
    {
        return read(JsonRef.emptyRef(), in);
    }

    public abstract SchemaTree read(final JsonRef ref, final Reader reader)
        throws IOException;

    public final SchemaTree read(final Reader reader)
        throws IOException
    {
        return read(JsonRef.emptyRef(), reader);
    }
}
