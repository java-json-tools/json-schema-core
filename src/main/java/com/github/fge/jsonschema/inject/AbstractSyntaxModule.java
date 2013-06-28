package com.github.fge.jsonschema.inject;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.messages.JsonSchemaSyntaxMessageBundle;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundleBuilder;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public abstract class AbstractSyntaxModule
    extends AbstractModule
{
    protected static final MessageBundle BASE_BUNDLE
        = MessageBundleFactory.getBundle(JsonSchemaSyntaxMessageBundle.class);

    protected final DictionaryBuilder<SyntaxChecker> checkers
        = Dictionary.newBuilder();
    protected final MessageBundleBuilder messages = MessageBundle.newBuilder();

    @Override
    protected final void configure()
    {
        bind(MessageBundle.class).toInstance(messages.freeze());
        bind(new TypeLiteral<Dictionary<SyntaxChecker>>() {})
            .toInstance(checkers.freeze());
    }
}
