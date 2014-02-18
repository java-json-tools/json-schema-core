/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A {@link SchemaTree} using inline dereferencing
 *
 * <p>In inline dereferencing, implementations are expected to trust that the
 * schema is the referrent document for all contexts declared by {@code id}.
 * For instance, with this schema:</p>
 *
 * <pre>
 *     {
 *         "id": "x;//y/z",
 *         "sub": {
 *             "id": "t"
 *         }
 *     }
 * </pre>
 *
 * <p>JSON Reference {@code x://y/t#} is this JSON document at JSON Pointer
 * {@code /sub}.</p>
 */
public final class InlineSchemaTree
    extends BaseSchemaTree
{
    /**
     * The list of contexts whose URIs are absolute JSON References
     */
    private final Map<JsonRef, JsonPointer> absRefs;

    /**
     * The list of contexts whose URIs are not absolute JSON References, or
     * outright illegal JSON References
     */
    private final Map<JsonRef, JsonPointer> otherRefs;

    public InlineSchemaTree(final JsonNode baseNode)
    {
        this(JsonRef.emptyRef(), baseNode);
    }

    public InlineSchemaTree(final JsonRef loadingRef, final JsonNode baseNode)
    {
        super(loadingRef, baseNode, JsonPointer.empty());
        final Map<JsonRef, JsonPointer> abs = Maps.newHashMap();
        final Map<JsonRef, JsonPointer> other = Maps.newHashMap();
        walk(loadingRef, baseNode, JsonPointer.empty(), abs, other);
        absRefs = ImmutableMap.copyOf(abs);
        otherRefs = ImmutableMap.copyOf(other);
    }

    private InlineSchemaTree(final InlineSchemaTree other,
        final JsonPointer newPointer)
    {
        super(other, newPointer);
        absRefs = other.absRefs;
        otherRefs = other.otherRefs;
    }

    @Override
    public SchemaTree append(final JsonPointer pointer)
    {
        final JsonPointer newPointer = this.pointer.append(pointer);
        return new InlineSchemaTree(this, newPointer);
    }

    @Override
    public SchemaTree setPointer(final JsonPointer pointer)
    {

        return new InlineSchemaTree(this, pointer);
    }

    @Override
    public boolean containsRef(final JsonRef ref)
    {
        return getMatchingPointer(ref) != null;
    }

    @Override
    public JsonPointer matchingPointer(final JsonRef ref)
    {
        final JsonPointer ret = getMatchingPointer(ref);
        if (ret == null)
            return null;

        return ret.path(baseNode).isMissingNode() ? null : ret;
    }

    private JsonPointer getMatchingPointer(final JsonRef ref)
    {
        if (otherRefs.containsKey(ref))
            return otherRefs.get(ref);
        if (!ref.isLegal())
            return null;
        return  refMatchingPointer(ref);
    }

    /**
     * Return a matching pointer for a JSON Pointer terminated fully resolved
     * reference
     *
     * <p>This includes the loading URI. Note, however, that due to "id"
     * intricacies, the test against the loading reference is done only as a
     * last resort.</p>
     *
     * @param ref the target reference
     * @return the matching pointer, or {@code null} if not found
     */
    private JsonPointer refMatchingPointer(final JsonRef ref)
    {
        final JsonPointer refPtr = ref.getPointer();

        /*
         * When using inline addressing, we must favor whatever "id" has defined
         * as a URI scope over what the loading URI is...
         */

        for (final Map.Entry<JsonRef, JsonPointer> entry: absRefs.entrySet())
            if (entry.getKey().contains(ref))
                return entry.getValue().append(refPtr);

        /*
         * ... Which means this test must be done last... (since refPtr is
         * declared final, this is safe)
         */
        return loadingRef.contains(ref) ? refPtr : null;
    }

    /**
     * Walk a JSON document to collect URI contexts
     *
     * <p>Unlike what happens with a canonical schema tree, we <i>must</i> walk
     * the whole tree in advance here. This is necessary for {@link
     * #containsRef(JsonRef)} and {@link #matchingPointer(JsonRef)} to work.</p>
     *
     * <p>This method is called recursively. Its furst invocation is from the
     * constructor, with {@link #loadingRef} as a reference, {@link #baseNode}
     * as a JSON document and an empty pointer as the document pointer.</p>
     *
     * @param baseRef the current context
     * @param node the current document
     * @param ptr the current pointer into the base document
     * @param absMap map for absolute JSON References
     * @param otherMap map for non absolute and/or illegal JSON References
     *
     * @see #idFromNode(JsonNode)
     */
    private static void walk(final JsonRef baseRef, final JsonNode node,
        final JsonPointer ptr, final Map<JsonRef, JsonPointer> absMap,
        final Map<JsonRef, JsonPointer> otherMap)
    {
        /*
         * FIXME: this means we won't go through schemas in keywords such as
         * "anyOf" and friends. No idea whether this is a concern. It may be.
         */
        if (!node.isObject())
            return;

        final JsonRef ref = idFromNode(node);
        final Map<JsonRef, JsonPointer> targetMap;

        JsonRef nextRef = baseRef;

        if (ref != null) {
            nextRef = baseRef.resolve(ref);
            targetMap = nextRef.isAbsolute() ? absMap : otherMap;
            targetMap.put(nextRef, ptr);
        }

        final Map<String, JsonNode> tmp = JacksonUtils.asMap(node);

        for (final Map.Entry<String, JsonNode> entry: tmp.entrySet())
            walk(nextRef, entry.getValue(), ptr.append(entry.getKey()), absMap,
                otherMap);
    }
}
