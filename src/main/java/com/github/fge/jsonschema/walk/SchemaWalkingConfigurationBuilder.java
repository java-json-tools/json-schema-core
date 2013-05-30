package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.CoreMessageBundle;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.syntax.SyntaxMessageBundle;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV3SyntaxCheckerDictionary;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.util.Thawed;
import com.github.fge.jsonschema.walk.collectors.DraftV3PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;

public final class SchemaWalkingConfigurationBuilder
    implements Thawed<SchemaWalkingConfiguration>
{
    private static final CoreMessageBundle BUNDLE
        = CoreMessageBundle.getInstance();

    DictionaryBuilder <PointerCollector> collectors;
    DictionaryBuilder<SyntaxChecker> checkers;
    MessageBundle bundle;
    boolean resolveRefs = false;
    LoadingConfiguration loadingCfg;

    SchemaWalkingConfigurationBuilder()
    {
        collectors = DraftV4PointerCollectorDictionary.get().thaw();
        checkers = DraftV4SyntaxCheckerDictionary.get().thaw();
        bundle = SyntaxMessageBundle.get();
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
        BUNDLE.checkNotNull(version, "processing.nullVersion");
        collectors = version == SchemaVersion.DRAFTV4
            ? DraftV4PointerCollectorDictionary.get().thaw()
            : DraftV3PointerCollectorDictionary.get().thaw();
        checkers = version == SchemaVersion.DRAFTV4
            ? DraftV4SyntaxCheckerDictionary.get().thaw()
            : DraftV3SyntaxCheckerDictionary.get().thaw();
        return this;
    }

    public SchemaWalkingConfigurationBuilder setMessageBundle(
        final MessageBundle bundle)
    {
        BUNDLE.checkNotNull(bundle, "processing.nullBundle");
        this.bundle = bundle;
        return this;
    }

    public void setResolveRefs(final boolean resolveRefs)
    {
        this.resolveRefs = resolveRefs;
    }

    public void setLoadingConfiguration(final LoadingConfiguration loadingCfg)
    {
        BUNDLE.checkNotNull(loadingCfg, "processing.nullLoadingCfg");
        this.loadingCfg = loadingCfg;
    }

    @Override
    public SchemaWalkingConfiguration freeze()
    {
        return new SchemaWalkingConfiguration(this);
    }
}
