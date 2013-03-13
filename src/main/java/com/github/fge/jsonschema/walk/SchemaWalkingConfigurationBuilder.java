package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.util.Thawed;
import com.github.fge.jsonschema.walk.collectors.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;

public final class SchemaWalkingConfigurationBuilder
    implements Thawed<SchemaWalkingConfiguration>
{
    final DictionaryBuilder<PointerCollector> collectors;
    final DictionaryBuilder<SyntaxChecker> checkers;

    SchemaWalkingConfigurationBuilder()
    {
        collectors = DraftV4PointerCollectorDictionary.get().thaw();
        checkers = DraftV4SyntaxCheckerDictionary.get().thaw();
    }

    SchemaWalkingConfigurationBuilder(final SchemaWalkingConfiguration cfg)
    {
        collectors = cfg.collectors.thaw();
        checkers = cfg.checkers.thaw();
    }

    @Override
    public SchemaWalkingConfiguration freeze()
    {
        return new SchemaWalkingConfiguration(this);
    }
}
