package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.messages.MessageBundle;
import com.github.fge.jsonschema.messages.MessageBundles;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.util.Frozen;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;

public final class SchemaWalkingConfiguration
    implements Frozen<SchemaWalkingConfigurationBuilder>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.PROCESSING;

    final Dictionary<PointerCollector> collectors;
    final Dictionary<SyntaxChecker> checkers;
    final boolean resolveRefs;
    final LoadingConfiguration loadingCfg;

    public static SchemaWalkingConfiguration byDefault()
    {
        return new SchemaWalkingConfigurationBuilder().freeze();
    }

    SchemaWalkingConfiguration(final SchemaWalkingConfigurationBuilder builder)
    {
        collectors = builder.collectors.freeze();
        checkers = builder.checkers.freeze();
        resolveRefs = builder.resolveRefs;
        loadingCfg = builder.loadingCfg;
        if (resolveRefs)
            BUNDLE.checkNotNull(loadingCfg, "noLoadingCfg");
    }

    @Override
    public SchemaWalkingConfigurationBuilder thaw()
    {
        return new SchemaWalkingConfigurationBuilder(this);
    }
}
