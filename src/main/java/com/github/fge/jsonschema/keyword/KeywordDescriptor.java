package com.github.fge.jsonschema.keyword;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

public final class KeywordDescriptor
{
    private final String name;

    public static Builder withName(final String name)
    {
        return new Builder(name);
    }

    private KeywordDescriptor(final Builder builder)
    {
        name = builder.name;
    }

    public String getName()
    {
        return name;
    }

    public static final class Builder
    {
        private static final MessageBundle BUNDLE
            = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

        private final String name;

        private Builder(final String name)
        {
            this.name = BUNDLE.checkNotNull(name, "keywordDescriptor.nullName");
        }
    }
}
