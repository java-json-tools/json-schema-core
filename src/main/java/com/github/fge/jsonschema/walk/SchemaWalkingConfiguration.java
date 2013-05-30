package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.util.Frozen;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;

public final class SchemaWalkingConfiguration
    implements Frozen<SchemaWalkingConfigurationBuilder>
{
    final Dictionary<PointerCollector> collectors;
    final Dictionary<SyntaxChecker> checkers;
    final MessageBundle bundle;
    final LoadingConfiguration loadingCfg;

    public static SchemaWalkingConfiguration byDefault()
    {
        return new SchemaWalkingConfigurationBuilder().freeze();
    }

    public static SchemaWalkingConfigurationBuilder newBuilder()
    {
        return new SchemaWalkingConfigurationBuilder();
    }

    SchemaWalkingConfiguration(final SchemaWalkingConfigurationBuilder builder)
    {
        collectors = builder.collectors;
        checkers = builder.checkers;
        bundle = builder.bundle;
        loadingCfg = builder.loadingCfg;
    }

    @Override
    public SchemaWalkingConfigurationBuilder thaw()
    {
        return new SchemaWalkingConfigurationBuilder(this);
    }
}
