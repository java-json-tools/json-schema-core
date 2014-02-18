/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

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
