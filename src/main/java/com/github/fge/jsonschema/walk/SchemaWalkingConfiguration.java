package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.util.Frozen;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;

import static com.github.fge.jsonschema.messages.ProcessingErrors.*;

public final class SchemaWalkingConfiguration
    implements Frozen<SchemaWalkingConfigurationBuilder>
{
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
            NO_LOADINGCFG.checkThat(loadingCfg != null);
    }

    @Override
    public SchemaWalkingConfigurationBuilder thaw()
    {
        return new SchemaWalkingConfigurationBuilder(this);
    }
}
