package com.github.fge.jsonschema.modules.read;

import com.github.fge.jsonschema.load.Dereferencing;
import com.google.common.annotations.Beta;

import java.util.EnumSet;

import static com.fasterxml.jackson.core.JsonParser.*;

@Beta
public final class DefaultReaderModule
    extends ReaderModule
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
        = EnumSet.copyOf(DEFAULT_PARSER_FEATURES);

    public DefaultReaderModule(final Dereferencing dereferencing,
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
        return new DefaultSchemaReader(dereferencing, parserFeatures);
    }
}
