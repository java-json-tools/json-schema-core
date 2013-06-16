package com.github.fge.jsonschema.preprocessing;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.SchemaListener;

public final class Preprocessing
{
    /*
     * PREPROCESSING ALGORITHM
     *
     * This part is supposed to happen _before_ the validation processor is
     * built. The goal is to provide the minimal processor available given the
     * schema.
     *
     * First do a syntax check on the schema. This will allow to detect invalid
     * schemas (in which case we don't continue at all), but also the case where
     * unknown keywords are spotted; in this case, full processor as it is now.
     * NOTE: could be eventually improved.
     *
     * Second, walk the schema and see if there are any JSON References in it.
     * If there is none, return a simple, "identity" processor.
     *
     * If there are JSON References and they are all local, try and expand with
     * a "local only" (no external schemas involved) ref resolver. If it
     * succeeds, return a simple, "identity" processor. If it fails, there are
     * recursive or lossy expansions, in this case return a processor with a
     * Map<JsonPointer, JsonPointer> recording the mappings of found pointers.
     *
     * If there are JSON References pointing outside of the schema, return the
     * full processor.
     */
    private static final class SchemaPreprocessor
        implements SchemaListener<PreprocessorType>
    {
        private final SchemaTree schema;
        private final JsonRef location;

        PreprocessorType type = PreprocessorType.NOREF;

        private SchemaPreprocessor(final SchemaTree schema)
        {
            this.schema = schema;
            location = schema.getLoadingRef();
        }

        @Override
        public void onTreeChange(final SchemaTree oldTree,
            final SchemaTree newTree)
            throws ProcessingException
        {
            // cannot happen, we never get called from a resolving walker
        }

        @Override
        public void onWalk(final SchemaTree tree)
            throws ProcessingException
        {
            final JsonNode node = tree.getNode();
            final JsonNode refNode = node.path("$ref");
            if (!refNode.isTextual())
                return;

            final JsonRef ref;
            try {
                ref = JsonRef.fromString(refNode.textValue());
                if (type.compareTo(PreprocessorType.INTERNALREFS) < 0)
                    type = PreprocessorType.INTERNALREFS;
            } catch (JsonReferenceException ignored) {
                // FIXME: this is not supposed to happen here, since we normally
                // have validated the syntax before. Right?
                return;
            }

            final JsonRef resolvedRef = tree.resolve(ref);

            if (tree.containsRef(resolvedRef))
                return;
            if (type.compareTo(PreprocessorType.FULL) < 0)
                type = PreprocessorType.FULL;
        }

        @Override
        public void onEnter(final JsonPointer pointer)
            throws ProcessingException
        {
            // nothing to do
        }

        @Override
        public void onExit(final JsonPointer pointer)
            throws ProcessingException
        {
            // nothing to do
        }

        @Override
        public PreprocessorType getValue()
        {
            return null;
        }
    }

    private enum PreprocessorType
    {
        NOREF,
        INTERNALREFS,
        FULL,
        ;
    }
}
