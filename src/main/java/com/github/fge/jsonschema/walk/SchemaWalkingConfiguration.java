package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.util.Frozen;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;

public final class SchemaWalkingConfiguration
    implements Frozen<SchemaWalkingConfigurationBuilder>
{
    final Dictionary<PointerCollector> collectors;
    final Dictionary<SyntaxChecker> checkers;

    public static SchemaWalkingConfiguration byDefault()
    {
        return new SchemaWalkingConfigurationBuilder().freeze();
    }

    SchemaWalkingConfiguration(final SchemaWalkingConfigurationBuilder builder)
    {
        collectors = builder.collectors.freeze();
        checkers = builder.checkers.freeze();
    }

    @Override
    public SchemaWalkingConfigurationBuilder thaw()
    {
        return new SchemaWalkingConfigurationBuilder(this);
    }
}
