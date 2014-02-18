package com.github.fge.jsonschema.core.keyword;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;

@Beta
public final class KeywordDescriptor
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final String name;
    private final PointerCollector pointerCollector;
    private final SyntaxChecker syntaxChecker;

    public static Builder withName(final String name)
    {
        return new Builder(name);
    }

    private KeywordDescriptor(final Builder builder)
    {
        name = builder.name;
        pointerCollector = builder.pointerCollector;
        syntaxChecker = builder.syntaxChecker;
    }

    public String getName()
    {
        return name;
    }

    public PointerCollector getPointerCollector()
    {
        return pointerCollector;
    }

    public SyntaxChecker getSyntaxChecker()
    {
        return syntaxChecker;
    }

    public static final class Builder
    {
        private final String name;
        private PointerCollector pointerCollector = null;
        private SyntaxChecker syntaxChecker = null;

        private Builder(final String name)
        {
            this.name = BUNDLE.checkNotNull(name, "keywordDescriptor.nullName");
        }

        public Builder setPointerCollector(
            final PointerCollector pointerCollector)
        {
            this.pointerCollector = BUNDLE.checkNotNull(pointerCollector,
                "keywordDescriptor.nullPointerCollector");
            return this;
        }

        public Builder setSyntaxChecker(final SyntaxChecker syntaxChecker)
        {
            this.syntaxChecker = BUNDLE.checkNotNull(syntaxChecker,
                "keywordDescriptor.nullSyntaxChecker");
            return this;
        }

        public KeywordDescriptor build()
        {
            if (pointerCollector != null)
                BUNDLE.checkArgument(syntaxChecker != null,
                    "keywordDescriptor.illegal");
            return new KeywordDescriptor(this);
        }
    }
}
