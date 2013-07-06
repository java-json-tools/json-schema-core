package com.github.fge.jsonschema.util;

import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class URIUtilsTest
{
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
}
