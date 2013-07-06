package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.load.read.DefaultSchemaReaderModule;
import com.github.fge.jsonschema.load.read.SchemaReaderModule;
import com.github.fge.jsonschema.load.resolve.DefaultURIResolverModule;
import com.github.fge.jsonschema.load.resolve.URIResolverModule;
import com.github.fge.jsonschema.load.translate.URITranslatorModule;
import com.google.common.annotations.Beta;

@Beta
public final class SchemaLoaderFactory
{
    public static final class Builder
    {
        private SchemaReaderModule readerModule;
        private URITranslatorModule translatorModule;
        private URIResolverModule resolverModule;

        private Builder()
        {
            readerModule = new DefaultSchemaReaderModule();
            translatorModule = new URITranslatorModule();
            resolverModule = new DefaultURIResolverModule();
        }

        public Builder setReaderModule(final SchemaReaderModule readerModule)
        {
            this.readerModule = readerModule;
            return this;
        }

        public Builder setTranslatorModule(
            final URITranslatorModule translatorModule)
        {
            this.translatorModule = translatorModule;
            return this;
        }

        public Builder setResolverModule(final URIResolverModule resolverModule)
        {
            this.resolverModule = resolverModule;
            return this;
        }
    }
}
