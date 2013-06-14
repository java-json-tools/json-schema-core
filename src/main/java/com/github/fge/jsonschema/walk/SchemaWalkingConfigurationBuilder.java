/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.walk;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.syntax.SyntaxMessageBundle;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.walk.collectors.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;

public final class SchemaWalkingConfigurationBuilder
    implements Thawed<SchemaWalkingConfiguration>
{
    private static final MessageBundle BUNDLE
        = MessageBundleFactory.getBundle(JsonSchemaCoreMessageBundle.class);

    Dictionary< PointerCollector > collectors;
    Dictionary<SyntaxChecker> checkers;
    MessageBundle bundle;
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
