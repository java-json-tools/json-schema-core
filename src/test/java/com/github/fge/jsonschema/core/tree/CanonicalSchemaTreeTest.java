/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.tree;

import static org.testng.Assert.assertFalse;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;

public final class CanonicalSchemaTreeTest
{
    private SchemaTree schemaTree;
    private JsonNode lookups;

    @BeforeClass
    public void loadData()
        throws IOException
    {
        final JsonNode data = JsonLoader.fromResource("/tree/retrieval.json");
        lookups = data.get("lookups");

        final JsonNode schema = data.get("schema");
        schemaTree = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);
    }

    @DataProvider
    public Iterator<Object[]> getLookups()
        throws ProcessingException
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode lookup: lookups)
            set.add(new Object[] {
                JsonRef.fromString(lookup.get("id").textValue())
            });

        return set.iterator();
    }

    @Test(dataProvider = "getLookups")
    public void canonicalTreeDoesNotContainInlineContexts(final JsonRef ref)
    {
        assertFalse(schemaTree.containsRef(ref));
    }
}
