package com.github.fge.jsonschema.syntax.checkers.hyperschema.draftv4;

import com.fasterxml.jackson.core.JsonProcessingException;

public final class ReadOnlyTest
    extends DraftV4HyperSchemaSyntaxCheckersTest
{
    public ReadOnlyTest()
        throws JsonProcessingException
    {
        super("readOnly");
    }
}
