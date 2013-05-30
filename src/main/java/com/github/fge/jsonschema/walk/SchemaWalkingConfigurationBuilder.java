package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.CoreMessageBundle;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.syntax.SyntaxMessageBundle;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.util.Thawed;
import com.github.fge.jsonschema.walk.collectors.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;

public final class SchemaWalkingConfigurationBuilder
    implements Thawed<SchemaWalkingConfiguration>
{
    private static final CoreMessageBundle BUNDLE
        = CoreMessageBundle.getInstance();

    Dictionary<PointerCollector> collectors;
    Dictionary<SyntaxChecker> checkers;
    MessageBundle bundle;
    boolean resolveRefs = false;
    LoadingConfiguration loadingCfg;

    SchemaWalkingConfigurationBuilder()
    {
        collectors = DraftV4PointerCollectorDictionary.get();
        checkers = DraftV4SyntaxCheckerDictionary.get();
        loadingCfg = LoadingConfiguration.byDefault();
        bundle = SyntaxMessageBundle.get();
    }

    SchemaWalkingConfigurationBuilder(final SchemaWalkingConfiguration cfg)
    {
        collectors = cfg.collectors;
        checkers = cfg.checkers;
        resolveRefs = cfg.resolveRefs;
        loadingCfg = cfg.loadingCfg;
    }

    public SchemaWalkingConfigurationBuilder setCheckers(
        final Dictionary<SyntaxChecker> checkers)
    {
        this.checkers = checkers;
        return this;
    }

    public SchemaWalkingConfigurationBuilder setCollectors(
        final Dictionary<PointerCollector> collectors)
    {
        this.collectors = collectors;
        return this;
    }

    public SchemaWalkingConfigurationBuilder setMessageBundle(
        final MessageBundle bundle)
    {
        BUNDLE.checkNotNull(bundle, "processing.nullBundle");
        this.bundle = bundle;
        return this;
    }

    public SchemaWalkingConfigurationBuilder setResolveRefs(
        final boolean resolveRefs)
    {
        this.resolveRefs = resolveRefs;
        return this;
    }

    public SchemaWalkingConfigurationBuilder setLoadingConfiguration(
        final LoadingConfiguration loadingCfg)
    {
        BUNDLE.checkNotNull(loadingCfg, "processing.nullLoadingCfg");
        this.loadingCfg = loadingCfg;
        return this;
    }

    @Override
    public SchemaWalkingConfiguration freeze()
    {
        return new SchemaWalkingConfiguration(this);
    }
}
