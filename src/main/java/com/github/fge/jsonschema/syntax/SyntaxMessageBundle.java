package com.github.fge.jsonschema.syntax;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;

public final class SyntaxMessageBundle
{
    private static final String PATH
        = "/com/github/fge/jsonschema/core/syntax.properties";

    private static final MessageBundle BUNDLE = PropertiesBundle.forPath(PATH);

    private SyntaxMessageBundle()
    {
    }

    public static MessageBundle get()
    {
        return BUNDLE;
    }
}
