package com.github.fge.jsonschema.inject;


import com.github.fge.jsonschema.keyword.SchemaSelectorModule;
import com.github.fge.jsonschema.loader.read.DefaultSchemaReaderModule;
import com.github.fge.jsonschema.messages.JsonSchemaSyntaxMessageBundle;
import com.github.fge.jsonschema.registry.translate.URITranslatorModule;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class JsonSchemaFactoryBuilder
{
    Module schemaSelectorModule;
    Module schemaReaderModule;
    Module uriTranslatorModule;
    Module syntaxMessageBundleModule;


    JsonSchemaFactoryBuilder()
    {
        schemaSelectorModule = new SchemaSelectorModule();
        schemaReaderModule = new DefaultSchemaReaderModule();
        uriTranslatorModule = new URITranslatorModule();
        syntaxMessageBundleModule = new MessageBundleModule("syntaxMessages",
            MessageBundles.getBundle(JsonSchemaSyntaxMessageBundle.class));
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

    public final JsonSchemaFactory build()
    {
        return new JsonSchemaFactory(this);
    }
}
