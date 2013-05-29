package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.messages.CoreMessageBundles;
import com.github.fge.jsonschema.messages.MessageBundle;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV3SyntaxCheckerDictionary;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.util.Thawed;
import com.github.fge.jsonschema.walk.collectors.DraftV3PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;

public final class SchemaWalkingConfigurationBuilder
    implements Thawed<SchemaWalkingConfiguration>
{
    private static final MessageBundle BUNDLE
        = CoreMessageBundles.PROCESSING;

    DictionaryBuilder<PointerCollector> collectors;
    DictionaryBuilder<SyntaxChecker> checkers;
    boolean resolveRefs = false;
    LoadingConfiguration loadingCfg;

    SchemaWalkingConfigurationBuilder()
    {
        collectors = DraftV4PointerCollectorDictionary.get().thaw();
        checkers = DraftV4SyntaxCheckerDictionary.get().thaw();
    }

    SchemaWalkingConfigurationBuilder(final SchemaWalkingConfiguration cfg)
    {
        collectors = cfg.collectors.thaw();
        checkers = cfg.checkers.thaw();
        resolveRefs = cfg.resolveRefs;
        loadingCfg = cfg.loadingCfg;
    }

    public SchemaWalkingConfigurationBuilder setVersion(
        final SchemaVersion version)
    {
        BUNDLE.checkNotNull(version, "nullVersion");
        collectors = version == SchemaVersion.DRAFTV4
            ? DraftV4PointerCollectorDictionary.get().thaw()
            : DraftV3PointerCollectorDictionary.get().thaw();
        checkers = version == SchemaVersion.DRAFTV4
            ? DraftV4SyntaxCheckerDictionary.get().thaw()
            : DraftV3SyntaxCheckerDictionary.get().thaw();
        return this;
    }

    public void setResolveRefs(final boolean resolveRefs)
    {
        this.resolveRefs = resolveRefs;
    }

    public void setLoadingConfiguration(final LoadingConfiguration loadingCfg)
    {
        BUNDLE.checkNotNull(loadingCfg, "nullLoadingCfg");
        this.loadingCfg = loadingCfg;
    }

    @Override
    public SchemaWalkingConfiguration freeze()
    {
        return new SchemaWalkingConfiguration(this);
    }
}
