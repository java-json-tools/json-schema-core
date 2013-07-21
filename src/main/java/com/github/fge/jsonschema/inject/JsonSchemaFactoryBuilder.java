package com.github.fge.jsonschema.inject;


import com.github.fge.jsonschema.keyword.SchemaSelectorModule;
import com.github.fge.jsonschema.loader.read.DefaultSchemaReaderModule;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.messages.JsonSchemaSyntaxMessageBundle;
import com.github.fge.jsonschema.registry.translate.URITranslatorModule;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;

public final class JsonSchemaFactoryBuilder
{
    private static final MessageBundle CORE_BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private Module schemaSelectorModule;
    private Module schemaReaderModule;
    private Module uriTranslatorModule;
    private Module syntaxMessageBundleModule;


    JsonSchemaFactoryBuilder()
    {
        schemaSelectorModule = new SchemaSelectorModule();
        schemaReaderModule = new DefaultSchemaReaderModule();
        uriTranslatorModule = new URITranslatorModule();
        syntaxMessageBundleModule = new MessageBundleModule("syntaxMessages",
            MessageBundles.getBundle(JsonSchemaSyntaxMessageBundle.class));
    }

    public JsonSchemaFactoryBuilder setSchemaSelectorModule(final Module module)
    {
        schemaSelectorModule = CORE_BUNDLE.checkNotNull(module,
            "factoryBuilder.nullModule");
        return this;
    }

    public JsonSchemaFactoryBuilder setSchemaReaderModule(final Module module)
    {
        schemaReaderModule = CORE_BUNDLE.checkNotNull(module,
            "factoryBuilder.nullModule");
        return this;
    }

    public JsonSchemaFactoryBuilder setURITranslatorModule(final Module module)
    {
        uriTranslatorModule = CORE_BUNDLE.checkNotNull(module,
            "factoryBuilder.nullModule");
        return this;
    }

    public JsonSchemaFactoryBuilder setSyntaxMessageBundle(
        final MessageBundle bundle)
    {
        CORE_BUNDLE.checkNotNull(bundle, "factoryBuilder.nullBundle");
        syntaxMessageBundleModule = new MessageBundleModule("syntaxMessages",
            bundle);
        return this;
    }

    public JsonSchemaFactory build()
    {
        final Injector injector = Guice.createInjector(schemaReaderModule,
            schemaSelectorModule, uriTranslatorModule,
            syntaxMessageBundleModule);
        return new JsonSchemaFactory(injector);
    }

    private static final class MessageBundleModule
        extends AbstractModule
    {
        final String name;
        final MessageBundle bundle;

        private MessageBundleModule(final String name,
            final MessageBundle bundle)
        {
            this.name = name;
            this.bundle = bundle;
        }

        @Override
        protected void configure()
        {
            bind(MessageBundle.class).annotatedWith(Names.named(name))
                .toInstance(bundle);
        }
    }

}
