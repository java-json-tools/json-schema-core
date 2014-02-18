/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.keyword;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordDescriptorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Test
    public void cannotCreateKeywordWithNullName()
    {
        try {
            KeywordDescriptor.withName(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("keywordDescriptor.nullName"));
        }
    }

    @Test(dependsOnMethods = "cannotCreateKeywordWithNullName")
    public void cannotSubmitNullPointerCollector()
    {
        try {
            KeywordDescriptor.withName("foo").setPointerCollector(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("keywordDescriptor.nullPointerCollector"));
        }
    }

    @Test(dependsOnMethods = "cannotCreateKeywordWithNullName")
    public void cannotSubmitNullSyntaxChecker()
    {
        try {
            KeywordDescriptor.withName("foo").setSyntaxChecker(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("keywordDescriptor.nullSyntaxChecker"));
        }
    }

    @Test
    public void mustHaveSyntaxCheckerWithPointerCollector()
    {
        try {
            KeywordDescriptor.withName("foo")
                .setPointerCollector(mock(PointerCollector.class)).build();
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("keywordDescriptor.illegal"));
        }
    }

    @Test
    public void descriptorElementsAreRetrievable()
    {
        final String name = "foo";
        final SyntaxChecker checker = mock(SyntaxChecker.class);
        final PointerCollector collector = mock(PointerCollector.class);
        final KeywordDescriptor descriptor = KeywordDescriptor.withName(name)
            .setSyntaxChecker(checker).setPointerCollector(collector).build();

        assertSame(descriptor.getName(), name);
        assertSame(descriptor.getSyntaxChecker(), checker);
        assertSame(descriptor.getPointerCollector(), collector);
    }
}
