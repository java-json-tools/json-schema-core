/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.util.equivalence;

import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.google.common.base.Equivalence;

/**
 * Schema tree equivalence
 *
 * <p>Two schema trees are considered equivant if their loading URI, current
 * URI context, base node and pointers are equivalent.</p>
 */
// TODO move
public final class SchemaTreeEquivalence
    extends Equivalence<SchemaTree>
{
    private static final Equivalence<SchemaTree> INSTANCE
        = new SchemaTreeEquivalence();

    public static Equivalence<SchemaTree> getInstance()
    {
        return INSTANCE;
    }

    private SchemaTreeEquivalence()
    {
    }

    @Override
    protected boolean doEquivalent(final SchemaTree a, final SchemaTree b)
    {
        return a.getLoadingRef().equals(b.getLoadingRef())
            && a.getContext().equals(b.getContext())
            && a.getPointer().equals(b.getPointer())
            && a.getBaseNode().equals(b.getBaseNode());
    }

    @Override
    protected int doHash(final SchemaTree t)
    {
        int ret = t.getLoadingRef().hashCode();
        ret = 31 * ret + t.getContext().hashCode();
        ret = 31 * ret + t.getPointer().hashCode();
        ret = 31 * ret + t.getBaseNode().hashCode();
        return ret;
    }
}
