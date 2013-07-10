package com.github.fge.jsonschema.load.transform;

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
                BUNDLE.getMessage("uriChecks.nullInput"));
        }

        try {
            builder.setNamespace((String) null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriChecks.nullInput"));
        }
    }

    @DataProvider
    public Iterator<Object[]> invalidPathURIs()
    {
        final List<Object[]> list = Lists.newArrayList();

        String uri;
        String key;

        key = "uriChecks.notAbsolute";
        list.add(new Object[] { "", key });

        uri = "foo://bar/#";
        key = "uriChecks.fragmentNotNull";
        list.add(new Object[] { uri, key });

        uri = "foo://bar?baz=meh";
        key = "uriChecks.queryNotNull";
        list.add(new Object[] { uri, key });

        uri = "foo://bar/baz";
        key = "uriChecks.noEndingSlash";
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

    @DataProvider
    public Iterator<Object[]> invalidSchemaURIs()
    {
        final List<Object[]> list = Lists.newArrayList();

        String uri;
        String key;

        key = "uriChecks.notAbsolute";
        list.add(new Object[] { "", key });

        uri = "foo://bar/#/a";
        key = "uriChecks.notAbsoluteRef";
        list.add(new Object[] { uri, key });

        uri = "foo://bar/baz/";
        key = "uriChecks.endingSlash";
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
