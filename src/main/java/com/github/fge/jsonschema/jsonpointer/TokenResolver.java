package com.github.fge.jsonschema.jsonpointer;

import com.fasterxml.jackson.core.TreeNode;

public abstract class TokenResolver<T extends TreeNode>
{
    protected final ReferenceToken token;

    protected TokenResolver(final ReferenceToken token)
    {
        this.token = token;
    }

    public abstract T get(final T node);

    @Override
    public final int hashCode()
    {
        return token.hashCode();
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
        final TokenResolver<T> other = (TokenResolver<T>) obj;
        return token.equals(other.token);
    }

    @Override
    public final String toString()
    {
        return token.toString();
    }
}
