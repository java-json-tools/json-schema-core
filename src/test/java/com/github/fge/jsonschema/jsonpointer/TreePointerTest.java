package com.github.fge.jsonschema.jsonpointer;

import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.JsonPointerMessages.*;
import static org.testng.Assert.*;

public final class TreePointerTest
{
    @Test
    public void buildingTokenListYellsIfIllegalPointer()
    {
        try {
            TreePointer.tokensFromInput("a/b");
            fail("No exception thrown!!");
        } catch (JsonPointerException e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NOT_SLASH)
                .hasField("expected", '/').hasField("found", 'a');
        }
    }

    @Test
    public void buildingTokenListIsUnfazedByAnEmptyInput()
        throws JsonPointerException
    {
        assertEquals(TreePointer.tokensFromInput(""),
            ImmutableList.<ReferenceToken>of());
    }

    @Test
    public void buildingTokenListIsUnfazedByEmptyToken()
        throws JsonPointerException
    {
        final List<ReferenceToken> expected
            = ImmutableList.of(ReferenceToken.fromCooked(""));
        final List<ReferenceToken> actual = TreePointer.tokensFromInput("/");

        assertEquals(actual, expected);
    }

    @Test
    public void tokenListRespectsOrder()
        throws JsonPointerException
    {
        final List<ReferenceToken> expected = ImmutableList.of(
            ReferenceToken.fromRaw("/"),
            ReferenceToken.fromRaw("~"),
            ReferenceToken.fromRaw("x")
        );
        final List<ReferenceToken> actual
            = TreePointer.tokensFromInput("/~1/~0/x");

        assertEquals(actual, expected);
    }

    @Test
    public void tokenListAccountsForEmptyTokens()
        throws JsonPointerException
    {
        final List<ReferenceToken> expected = ImmutableList.of(
            ReferenceToken.fromRaw("a"),
            ReferenceToken.fromRaw(""),
            ReferenceToken.fromRaw("b")
        );
        final List<ReferenceToken> actual
            = TreePointer.tokensFromInput("/a//b");

        assertEquals(actual, expected);
    }
}
