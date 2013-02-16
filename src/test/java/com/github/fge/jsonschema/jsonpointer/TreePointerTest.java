package com.github.fge.jsonschema.jsonpointer;

import com.fasterxml.jackson.core.TreeNode;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.JsonPointerMessages.*;
import static org.mockito.Mockito.*;
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

    @Test
    public void gettingTraversalResultGoesNoFurtherThanFirstMissing()
    {
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token1 = mock(TokenResolver.class);
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token2 = mock(TokenResolver.class);
        final TreeNode missing = mock(TreeNode.class);

        when(token1.get(any(TreeNode.class))).thenReturn(null);

        final DummyPointer ptr = new DummyPointer(missing,
            ImmutableList.of(token1, token2));

        final TreeNode node = mock(TreeNode.class);
        final TreeNode ret = ptr.get(node);
        verify(token1, only()).get(node);
        verify(token2, never()).get(any(TreeNode.class));

        assertNull(ret);
    }

    @Test
    public void gettingPathOfMissingNodeReturnsMissingNode()
    {
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token1 = mock(TokenResolver.class);
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token2 = mock(TokenResolver.class);
        final TreeNode missing = mock(TreeNode.class);

        when(token1.get(any(TreeNode.class))).thenReturn(null);

        final DummyPointer ptr = new DummyPointer(missing,
            ImmutableList.of(token1, token2));

        final TreeNode node = mock(TreeNode.class);
        final TreeNode ret = ptr.path(node);
        verify(token1, only()).get(node);
        verify(token2, never()).get(any(TreeNode.class));

        assertSame(ret, missing);
    }

    private static final class DummyPointer
        extends TreePointer<TreeNode>
    {
        DummyPointer(final TreeNode missing,
            final List<TokenResolver<TreeNode>> tokenResolvers)
        {
            super(missing, tokenResolvers);
        }
    }
}
