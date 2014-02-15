package com.github.fge.jsonschema.core.util;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class URIUtilsTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @DataProvider
    public Iterator<Object[]> schemeData()
    {
        final List<Object[]> list = Lists.newArrayList();

        String orig, dst;

        orig = "http";
        dst = "http";
        list.add(new Object[] { orig, dst });

        orig = "hTTp";
        dst = "http";
        list.add(new Object[] { orig, dst });

        orig = "GIT+ssh";
        dst = "git+ssh";
        list.add(new Object[] { orig, dst });

        list.add(new Object[] { null, null });

        return list.iterator();
    }

    @Test(dataProvider = "schemeData")
    public void schemesAreCorrectlyNormalized(final String orig,
        final String dst)
    {
        assertEquals(URIUtils.normalizeScheme(orig), dst);
    }

    @DataProvider
    public Iterator<Object[]> uriData()
    {
        final List<Object[]> list = Lists.newArrayList();

        String orig, dst;

        orig = "HTTP://SLAShDOt.ORG/foo/BAR/baz";
        dst = "http://slashdot.org/foo/BAR/baz";
        list.add(new Object[] { URI.create(orig), URI.create(dst) });

        orig = "file:///hello/worlD";
        dst = "file:/hello/worlD";
        list.add(new Object[] { URI.create(orig), URI.create(dst) });

        orig = "git+ssh://Gloubi.Boulga/a/c/../b";
        dst = "git+ssh://gloubi.boulga/a/b";
        list.add(new Object[] { URI.create(orig), URI.create(dst) });

        list.add(new Object[] { null, null });

        return list.iterator();
    }

    @Test(dataProvider = "uriData")
    public void urisAreCorrectlyNormalized(final URI orig, final URI dst)
    {
        assertEquals(URIUtils.normalizeURI(orig), dst);
    }

    @DataProvider
    public Iterator<Object[]> schemaURIs()
    {
        final List<Object[]> list = Lists.newArrayList();

        String orig, dst;

        orig = "a/b";
        dst = "a/b#";
        list.add(new Object[] { URI.create(orig), URI.create(dst) });

        orig = "http://my.site/schema#definitions/foo";
        dst = "http://my.site/schema#definitions/foo";
        list.add(new Object[] { URI.create(orig), URI.create(dst) });

        orig = "http://my.site/schema";
        dst = "http://my.site/schema#";
        list.add(new Object[] { URI.create(orig), URI.create(dst) });
        list.add(new Object[] { null, null });

        return list.iterator();
    }


    @Test(dataProvider = "schemaURIs")
    public void schemaURIsAreCorrectlyNormalized(final URI orig, final URI dst)
    {
        assertEquals(URIUtils.normalizeSchemaURI(orig), dst);
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
    public void invalidPathURIsAreRejected(final String uri, final String key)
    {
        try {
            URIUtils.checkPathURI(URI.create(uri));
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
    public void invalidSchemaURIsAreRejected(final String uri, final String key)
    {
        try {
            URIUtils.checkSchemaURI(URI.create(uri));
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), BUNDLE.printf(key, uri));
        }
    }

}
