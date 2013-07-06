package com.github.fge.jsonschema.load.read;

import com.github.fge.jsonschema.load.Dereferencing;
import com.google.common.annotations.Beta;

import java.util.EnumSet;

import static com.fasterxml.jackson.core.JsonParser.*;

@Beta
public final class DefaultSchemaReaderModule
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

    private final Dereferencing dereferencing;
    private final EnumSet<Feature> parserFeatures
        = EnumSet.noneOf(Feature.class);

    public DefaultSchemaReaderModule(final Dereferencing dereferencing,
        final Feature... features)
    {
        this.dereferencing = BUNDLE.checkNotNull(dereferencing,
            "loadingCfg.nullDereferencingMode");
        for (final Feature feature: features)
            parserFeatures.add(BUNDLE.checkNotNull(feature,
                "loadingCfg.nullJsonParserFeature"));
    }

    @Override
    protected SchemaReader newReader()
    {
        final EnumSet<Feature> features = EnumSet.copyOf(parserFeatures);
        features.addAll(DEFAULT_PARSER_FEATURES);
        return new DefaultSchemaReader(dereferencing, features);
    }
}
