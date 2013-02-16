package com.github.fge.jsonschema.jsonpointer;

import com.fasterxml.jackson.core.TreeNode;
import com.github.fge.jsonschema.messages.JsonPointerMessages;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public abstract class TreePointer<T extends TreeNode>
    implements Iterable<TokenResolver<T>>
{
    protected static final char SLASH = '/';

    protected final T missing;

    protected final List<TokenResolver<T>> tokenResolvers;

    protected TreePointer(final T missing,
        final List<TokenResolver<T>> tokenResolvers)
    {
        this.missing = missing;
        // WARNING: it is up to the actual constructor to ensure that the list
        // is a copy of the original
        this.tokenResolvers = tokenResolvers;
    }

    protected TreePointer(final List<TokenResolver<T>> tokenResolvers)
    {
        this(null, tokenResolvers);
    }

    protected static List<ReferenceToken> tokensFromInput(final String input)
        throws JsonPointerException
    {
        final List<ReferenceToken> ret = Lists.newArrayList();
        String s = input;
        String cooked;
        int index;
        char c;

        while (!s.isEmpty()) {
            c = s.charAt(0);
            if (c != SLASH)
                throw new JsonPointerException(new ProcessingMessage()
                    .message(JsonPointerMessages.NOT_SLASH)
                    .put("expected", Character.valueOf(SLASH))
                    .put("found", Character.valueOf(c)));
            s = s.substring(1);
            index = s.indexOf(SLASH);
            cooked = index == -1 ? s : s.substring(0, index);
            ret.add(ReferenceToken.fromCooked(cooked));
            if (index == -1)
                break;
            s = s.substring(index);
        }

        return ret;
    }

    public final T get(final T node)
    {
        T ret = node;
        for (final TokenResolver<T> tokenResolver: tokenResolvers) {
            if (ret == null)
                break;
            ret = tokenResolver.get(ret);
        }

        return ret;
    }

    public final T path(final T node)
    {
        final T ret = get(node);
        return ret == null ? missing : ret;
    }

    @Override
    public final Iterator<TokenResolver<T>> iterator()
    {
        return tokenResolvers.iterator();
    }

    @Override
    public final int hashCode()
    {
        return tokenResolvers.hashCode();
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        final TreePointer<T> other = (TreePointer<T>) obj;
        return tokenResolvers.equals(other.tokenResolvers);
    }

    @Override
    public final String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (final TokenResolver<T> tokenResolver: tokenResolvers)
            sb.append('/').append(tokenResolver);

        return sb.toString();
    }
}
