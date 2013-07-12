package com.github.fge.jsonschema.registry.translate;

import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class URITranslatorTest
{
    private static final URI SRCPATH
        = URI.create("http://my.site/schemas/");
    private static final URI DSTPATH
        = URI.create("resource:/com/mycompany/schemas/");
    private static final URI SRCPATH2
        = URI.create("http://json-schema.org/");
    private static final URI DSTPATH2
        = URI.create("file:/usr/share/json-schema/schemas/");
    private static final URI SRCSCHEMA1
        = URI.create("http://my.site/schemas/schema1.json");
    private static final URI DSTSCHEMA1
        = URI.create("ftp://schemas.org/pub/fge/schema1.json");
    private static final URI SRCSCHEMA2
        = URI.create("http://json-schema.org/draft-03/schema");
    private static final URI DSTSCHEMA2
        = URI.create("resource:/draftv3/schema");

    @Test
    public void defaultTransformerNormalizesURIs()
    {
        final URI source = URI.create("foo:///bar/../baz");
        final URI expected = URI.create("foo:/baz");

        final URITranslatorModule module = new URITranslatorModule();
        final URITranslator translator = module.getTranslator();

        assertEquals(translator.translate(source), expected);
    }

    @DataProvider
    public Iterator<Object[]> pathRedirectionData()
    {
        final List<Object[]> list = Lists.newArrayList();

        String from, to;

        from = "http://my.site/schemas/schema1.json#";
        to = "resource:/com/mycompany/schemas/schema1.json#";
        list.add(new Object[] { URI.create(from), URI.create(to)});

        from = "http://my.site/schemas/subs/schema.json";
        to = "resource:/com/mycompany/schemas/subs/schema.json";
        list.add(new Object[] { URI.create(from), URI.create(to)});

        from = "http://other.site/schemas/schema.json#/definitions/p1";
        to = from;
        list.add(new Object[] { URI.create(from), URI.create(to)});

        from = "http://json-schema.org/draft-04/schema#";
        to = "file:/usr/share/json-schema/schemas/draft-04/schema#";
        list.add(new Object[] { URI.create(from), URI.create(to)});

        return list.iterator();
    }

    @Test(dataProvider = "pathRedirectionData")
    public void pathRedirectionsWork(final URI from, final URI to)
    {
        final URITranslatorModule module = new URITranslatorModule()
        {{
            addPathRedirect(SRCPATH, DSTPATH);
            addPathRedirect(SRCPATH2, DSTPATH2);
        }};

        final URITranslator translator = module.getTranslator();

        assertEquals(translator.translate(from), to);
    }

    @DataProvider
    public Iterator<Object[]> schemaRedirectionData()
    {
        final List<Object[]> list = Lists.newArrayList();

        list.add(new Object[] { SRCSCHEMA1, DSTSCHEMA1 });
        list.add(new Object[] { SRCSCHEMA2, DSTSCHEMA2 });

        list.add(new Object[] {
            URI.create("http://my.site/schemas/schema2.json"),
            URI.create("http://my.site/schemas/schema2.json")
        });

        list.add(new Object[] {
            SRCSCHEMA1.resolve("#/definitions/a"),
            DSTSCHEMA1.resolve("#/definitions/a")
        });

        return list.iterator();
    }

    @Test(dataProvider = "schemaRedirectionData")
    public void schemaRedirectionsWork(final URI from, final URI to)
    {
        final URITranslatorModule module = new URITranslatorModule()
        {{
            addSchemaRedirect(SRCSCHEMA1, DSTSCHEMA1);
            addSchemaRedirect(SRCSCHEMA2, DSTSCHEMA2);
        }};

        final URITranslator translator = module.getTranslator();

        assertEquals(translator.translate(from), to);
    }
}
