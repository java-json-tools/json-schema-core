/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.keyword.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.core.messages.JsonSchemaSyntaxMessageBundle;
import com.github.fge.jsonschema.core.report.AbstractProcessingReport;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.util.ValueHolder;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;

public final class SyntaxProcessorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaSyntaxMessageBundle.class);
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String K1 = "k1";
    private static final String K2 = "k2";
    private static final String ERRMSG = "foo";

    private AbstractProcessingReport report;
    private SyntaxProcessor processor;
    private SyntaxChecker checker;

    @BeforeMethod
    public void initialize()
    {
        report = spy(new TestProcessingReport());
        final DictionaryBuilder<SyntaxChecker> builder
            = Dictionary.newBuilder();

        checker = mock(SyntaxChecker.class);
        builder.addEntry(K1, checker);
        builder.addEntry(K2, new SyntaxChecker()
        {
            @Override
            public EnumSet<NodeType> getValidTypes()
            {
                return EnumSet.noneOf(NodeType.class);
            }

            @Override
            public void checkSyntax(final Collection<JsonPointer> pointers,
                final MessageBundle bundle, final ProcessingReport report,
                final SchemaTree tree)
                throws ProcessingException
            {
                report.error(new ProcessingMessage().setMessage(ERRMSG));
            }
        });

        processor = new SyntaxProcessor(BUNDLE, builder.freeze());
    }

    @DataProvider
    public Iterator<Object[]> notSchemas()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.OBJECT);
    }

    @Test(dataProvider = "notSchemas")
    public void syntaxProcessorYellsOnNonSchemas(final JsonNode node)
        throws ProcessingException
    {
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        final SchemaTree tree = new CanonicalSchemaTree(node);
        final ValueHolder<SchemaTree> holder = ValueHolder.hold("schema", tree);

        processor.process(report, holder);

        verify(report).log(same(LogLevel.ERROR), captor.capture());

        final ProcessingMessage message = captor.getValue();
        final NodeType type = NodeType.getNodeType(node);
        assertMessage(message)
            .hasMessage(BUNDLE.printf("core.notASchema", type))
            .hasField("found", type);
    }

    @Test
    public void unknownKeywordsAreReportedAsWarnings()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put("foo", "");
        node.put("bar", "");

        final SchemaTree tree = new CanonicalSchemaTree(node);
        final ValueHolder<SchemaTree> holder = ValueHolder.hold("schema", tree);

        final ArrayNode ignored = FACTORY.arrayNode();
        // They appear in alphabetical order in the report!
        ignored.add("bar");
        ignored.add("foo");
        final Iterable<String> iterable = Iterables.transform(ignored,
            new Function<JsonNode, String>()
            {
                @Override
                public String apply(final JsonNode input)
                {
                    return input.textValue();
                }
            });

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        processor.process(report, holder);
        verify(report).log(same(LogLevel.WARNING), captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).hasField("ignored", ignored)
            .hasMessage(BUNDLE.printf("core.unknownKeywords", iterable));
    }

    @Test
    public void errorsAreCorrectlyReported()
        throws ProcessingException
    {
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        final ObjectNode schema = FACTORY.objectNode();
        schema.put(K2, "");

        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final ValueHolder<SchemaTree> holder = ValueHolder.hold("schema", tree);

        processor.process(report, holder);

        verify(report).log(same(LogLevel.ERROR), captor.capture());

        final ProcessingMessage msg = captor.getValue();
        assertMessage(msg).hasMessage(ERRMSG);
    }

    @Test
    public void checkingWillNotDiveIntoUnknownKeywords()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put(K1, K1);
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("foo", node);
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final ValueHolder<SchemaTree> holder = ValueHolder.hold("schema", tree);

        processor.process(report, holder);
        verify(checker, never()).checkSyntax(anyCollectionOf(JsonPointer.class),
            any(MessageBundle.class), anyReport(), anySchema());
    }

    private static class TestProcessingReport
        extends AbstractProcessingReport
    {
        @Override
        public void log(final LogLevel level, final ProcessingMessage message)
        {
        }
    }
}