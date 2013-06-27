package com.github.fge.jsonschema.load.uri;

import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class URITransformerTest
{
    private static final URI SRCPATH
        = URI.create("http://my.site/schemas/");
    private static final URI DSTPATH
        = URI.create("resource:/com/mycompany/schemas/");

    private URITransformerBuilder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = URITransformer.newBuilder();
    }

    @Test
    public void defaultTransformerNormalizesURIs()
    {
        final URI source = URI.create("foo:///bar/../baz");
        final URI expected = URI.create("foo:/baz");

        final URITransformer transformer = builder.freeze();

        assertEquals(transformer.transform(source), expected);
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

        return list.iterator();
    }

    @Test(dataProvider = "pathRedirectionData")
    public void pathRedirectionsWork(final URI from, final URI to)
    {
        final URITransformer transformer
            = builder.addPathRedirect(SRCPATH, DSTPATH).freeze();

        assertEquals(transformer.transform(from), to);
    }
}
