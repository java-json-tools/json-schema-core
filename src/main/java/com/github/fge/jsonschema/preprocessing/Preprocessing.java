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

        private static PreprocessorType worstOf(final PreprocessorType t1,
            final PreprocessorType t2)
        {
            return t1.compareTo(t2) >= 0 ? t1 : t2;
        }
    }
}
