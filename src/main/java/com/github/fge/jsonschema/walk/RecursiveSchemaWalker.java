/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.walk;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.SchemaWalkingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.jsonpointer.TokenResolver;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.processors.ref.RefResolver;
import com.github.fge.jsonschema.processors.validation.SchemaTreeEquivalence;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import static com.github.fge.jsonschema.messages.SchemaWalkerMessages.*;

public final class RecursiveSchemaWalker
    extends SchemaWalker
{
    private static final Equivalence<SchemaTree> EQUIVALENCE
        = SchemaTreeEquivalence.getInstance();

    private final RefResolver resolver;

    public RecursiveSchemaWalker(final SchemaVersion version,
        final SchemaTree tree, final LoadingConfiguration cfg)
    {
        super(version, tree);
        resolver = new RefResolver(new SchemaLoader(cfg));
    }

    public RecursiveSchemaWalker(final SchemaVersion version,
        final SchemaTree tree)
    {
        this(version, tree, LoadingConfiguration.byDefault());
    }

    public RecursiveSchemaWalker(final Dictionary<PointerCollector> dict,
        final SchemaTree tree, final LoadingConfiguration cfg)
    {
        /*
         * TODO:
         * - check versions
         * - check syntax on resolution
         */
        super(dict, tree);
        resolver = new RefResolver(new SchemaLoader(cfg));
    }

    @Override
    public <T> void resolveTree(final SchemaListener<T> listener,
        final ProcessingReport report)
        throws ProcessingException
    {
        final SchemaTree newTree = resolver.process(report,
            new SchemaHolder(tree)).getValue();
        if (EQUIVALENCE.equivalent(tree, newTree))
            return;
        checkTrees(tree, newTree);
        report.debug(new ProcessingMessage().message("tree change")
            .put("old", tree).put("new", newTree));
        listener.onNewTree(tree, newTree);
        tree = newTree;
    }

    @Override
    public String toString()
    {
        return "recursive tree walker ($ref resolution)";
    }

    private static void checkTrees(final SchemaTree tree,
        final SchemaTree newTree)
        throws ProcessingException
    {
        /*
         * We can rely on URIs here: at worst the starting URI was empty, but if
         * we actually fetched another schema, it will never be the empty URI. A
         * simple equality check on URIs can immediately tell us whether the
         * schema is the same.
         */
        if (!tree.getLoadingRef().equals(newTree.getLoadingRef()))
            return;
        /*
         * If it is, we just need to check that their pointers are disjoint. If
         * they are not, it means one is a prefix for the other one. Test this
         * by collecting the two trees' token resolvers and see if they share a
         * common subset at index 0.
         *
         * Note that the pointer can not be equal, of course: this would have
         * been caught by the ref resolver.
         */
        final JsonPointer sourcePointer = tree.getPointer();
        final JsonPointer targetPointer = newTree.getPointer();

        final List<TokenResolver<JsonNode>> sourceTokens
            = Lists.newArrayList(sourcePointer);
        final List<TokenResolver<JsonNode>> targetTokens
            = Lists.newArrayList(targetPointer);

        final ProcessingMessage message = new ProcessingMessage().message("")
            .put("schemaURI", tree.getLoadingRef())
            .put("source", sourcePointer.toString())
            .put("target", targetPointer.toString());
        /*
         * Check if there is an attempt to expand to a parent tree
         */
        if (Collections.indexOfSubList(sourceTokens, targetTokens) == 0)
            throw new SchemaWalkingException(message.message(PARENT_EXPAND));
        /*
         * Check if there is an attempt to expand to a subtree
         */
        if (Collections.indexOfSubList(targetTokens, sourceTokens) == 0)
            throw new SchemaWalkingException(message.message(SUBTREE_EXPAND));
    }
}
