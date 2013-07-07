package com.github.fge.jsonschema.keyword;

import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

import java.util.Collection;
import java.util.EnumSet;

public final class KeywordDescriptor
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final SyntaxChecker SYNTAXCHECKER_ALWAYSTRUE
        = new SyntaxChecker()
    {
        @Override
        public EnumSet<NodeType> getValidTypes()
        {
            return EnumSet.allOf(NodeType.class);
        }

        @Override
        public void checkSyntax(final Collection<JsonPointer> pointers,
            final MessageBundle bundle, final ProcessingReport report,
            final SchemaTree tree)
            throws ProcessingException
        {
        }
    };

    private static final PointerCollector POINTERCOLLECTOR_NOPOINTERS
        = new PointerCollector()
    {
        @Override
        public void collect(final Collection<JsonPointer> pointers,
            final SchemaTree tree)
        {
        }
    };

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
        private PointerCollector pointerCollector = POINTERCOLLECTOR_NOPOINTERS;
        private SyntaxChecker syntaxChecker = SYNTAXCHECKER_ALWAYSTRUE;

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
    }
}
