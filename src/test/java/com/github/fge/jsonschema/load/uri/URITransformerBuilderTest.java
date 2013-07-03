package com.github.fge.jsonschema.load.uri;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class URITransformerBuilderTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final URI DUMMY = URI.create("");

    private URITransformerBuilder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = URITransformer.newBuilder();
    }

    @Test
    public void nullNamespaceIsNotAccepted()
    {
        try {
            builder.setNamespace((URI) null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }

        try {
            builder.setNamespace((String) null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }
    }

    @DataProvider
    public Iterator<Object[]> invalidPathURIs()
    {
        final List<Object[]> list = Lists.newArrayList();

        String uri;
        String key;

        key = "uriTransform.notAbsolute";
        list.add(new Object[] { "", key });

        uri = "foo://bar/#";
        key = "uriTransform.fragmentNotNull";
        list.add(new Object[] { uri, key });

        uri = "foo://bar?baz=meh";
        key = "uriTransform.queryNotNull";
        list.add(new Object[] { uri, key });

        uri = "foo://bar/baz";
        key = "uriTransform.noEndingSlash";
        list.add(new Object[] { uri, key });

        return list.iterator();
    }

    @Test(dataProvider = "invalidPathURIs")
    public void cannotSetNonPathURINamespace(final String uri, final String key)
    {
        try {
            builder.setNamespace(uri);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }

        try {
            builder.setNamespace(URI.create(uri));
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }
    }

    @Test
    public void nullPathsAreNotAccepted()
    {
        try {
            builder.addPathRedirect(null, DUMMY);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }

        try {
            builder.addPathRedirect(URI.create("foo://bar/"), null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }

        try {
            builder.addPathRedirect(null, "");
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }

        try {
            builder.addPathRedirect("foo://bar/", null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }
    }

    @Test(dataProvider = "invalidPathURIs")
    public void cannotSetNonPathURIs(final String uri, final String key)
    {
        final String s = "foo://bar/";

        try {
            builder.addPathRedirect(uri, s);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }

        try {
            builder.addPathRedirect(s, uri);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }

        try {
            builder.addPathRedirect(URI.create(uri), URI.create(s));
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }

        try {
            builder.addPathRedirect(URI.create(s), URI.create(uri));
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }
    }

    @Test
    public void cannotSetNonNullSchemaRedirects()
    {
        try {
            builder.addSchemaRedirect(null, DUMMY);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }

        try {
            builder.addSchemaRedirect(URI.create("foo://bar"), null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }

        try {
            builder.addSchemaRedirect(null, "");
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }

        try {
            builder.addSchemaRedirect("foo://bar/", null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriTransform.nullInput"));
        }
    }

    @DataProvider
    public Iterator<Object[]> invalidSchemaURIs()
    {
        final List<Object[]> list = Lists.newArrayList();

        String uri;
        String key;

        key = "uriTransform.notAbsolute";
        list.add(new Object[] { "", key });

        uri = "foo://bar/#/a";
        key = "uriTransform.notAbsoluteRef";
        list.add(new Object[] { uri, key });

        uri = "foo://bar/baz/";
        key = "uriTransform.endingSlash";
        list.add(new Object[] { uri, key });

        return list.iterator();
    }

    @Test(dataProvider = "invalidSchemaURIs")
    public void cannotSetNonSchemaURIRedirects(final String uri,
        final String key)
    {
        final String s = "foo://bar#";

        try {
            builder.addSchemaRedirect(uri, s);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }

        try {
            builder.addSchemaRedirect(s, uri);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }

        try {
            builder.addSchemaRedirect(URI.create(uri), URI.create(s));
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }

        try {
            builder.addSchemaRedirect(URI.create(s), URI.create(uri));
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }
    }
}
