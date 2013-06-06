package com.github.fge.jsonschema.syntax;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;

public final class SyntaxMessageBundle
{
    private static final MessageBundle BUNDLE;

    static {
        final MessageSource source;
        try {
            source = PropertiesMessageSource
                .fromResource("/com/github/fge/jsonschema/core/syntax.properties");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        BUNDLE = MessageBundle.newBuilder().appendSource(source).freeze();
    }

    private SyntaxMessageBundle()
    {
    }

    public static MessageBundle get()
    {
        return BUNDLE;
    }
}
