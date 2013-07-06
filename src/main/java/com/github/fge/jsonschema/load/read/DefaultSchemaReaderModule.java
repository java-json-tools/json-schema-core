package com.github.fge.jsonschema.load.read;

import com.github.fge.jsonschema.load.Dereferencing;
import com.google.common.annotations.Beta;

import java.util.EnumSet;

import static com.fasterxml.jackson.core.JsonParser.*;

@Beta
public class DefaultSchemaReaderModule
    extends SchemaReaderModule
{
    /**
     * Default JsonParser feature set. Unfortunately, Jackson does not use
     * EnumSets to collect them, so we have to do that...
     */
    private static final EnumSet<Feature> DEFAULT_PARSER_FEATURES;

    static {
        DEFAULT_PARSER_FEATURES = EnumSet.noneOf(Feature.class);

        for (final Feature feature: Feature.values())
            if (feature.enabledByDefault())
                DEFAULT_PARSER_FEATURES.add(feature);
    }

    private Dereferencing dereferencing = Dereferencing.CANONICAL;
    private final EnumSet<Feature> parserFeatures
        = EnumSet.copyOf(DEFAULT_PARSER_FEATURES);

    protected final void setDereferencing(final Dereferencing dereferencing)
    {
        this.dereferencing = BUNDLE.checkNotNull(dereferencing,
            "loadingCfg.nullDereferencingMode");
    }
    protected final void addParserFeature(final Feature feature)
    {
        parserFeatures.add(BUNDLE.checkNotNull(feature,
            "loadingCfg.nullJsonParserFeature"));
    }

    @Override
    protected final SchemaReader newReader()
    {
        return new DefaultSchemaReader(dereferencing, parserFeatures);
    }
}
