package com.github.fge.jsonschema.analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.keyword.SchemaDescriptor;
import com.github.fge.jsonschema.keyword.SchemaDescriptorBuilder;
import com.github.fge.jsonschema.keyword.SchemaSelector;
import com.github.fge.jsonschema.keyword.SchemaSelectorConfiguration;
import com.github.fge.jsonschema.messages.JsonSchemaSyntaxMessageBundle;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SchemaAnalyzerTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaSyntaxMessageBundle.class);
    private static final URI ROOT = URI.create("foo://bar#");
    private static final String K1 = "k1";
    private static final String K2 = "k2";

    private SyntaxChecker checker1;
    private PointerCollector collector1;
    private SyntaxChecker checker2;
    private PointerCollector collector2;

    private SchemaAnalyzer analyzer;

    @BeforeMethod
    public void init()
    {
        checker1 = mock(SyntaxChecker.class);
        collector1 = mock(PointerCollector.class);
        checker2 = mock(SyntaxChecker.class);
        collector2 = mock(PointerCollector.class);

        final SchemaDescriptorBuilder builder = SchemaDescriptor.newBuilder()
            .setLocator(ROOT);

        KeywordDescriptor descriptor;

        descriptor = KeywordDescriptor.withName(K1).setSyntaxChecker(checker1)
            .setPointerCollector(collector1).build();
        builder.addKeyword(descriptor);

        descriptor = KeywordDescriptor.withName(K2).setSyntaxChecker(checker2)
            .setPointerCollector(collector2).build();
        builder.addKeyword(descriptor);

        final SchemaSelectorConfiguration cfg
            = SchemaSelectorConfiguration.newBuilder()
                .addDescriptor(builder.freeze(), true).freeze();

        analyzer = new SchemaAnalyzer(BUNDLE, new SchemaSelector(cfg));
    }

    @Test
    public void checkersAndCollectorsAreCalled()
        throws ProcessingException
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put(K1, "");
        node.put(K2, "");

        final SchemaTree tree = new CanonicalSchemaTree(node);
        analyzer.analyze(tree);

        verify(checker1).checkSyntax(anyCollectionOf(JsonPointer.class),
            same(BUNDLE), any(ProcessingReport.class), same(tree));
        verify(checker2).checkSyntax(anyCollectionOf(JsonPointer.class),
            same(BUNDLE), any(ProcessingReport.class), same(tree));
        verify(collector1).collect(anyCollectionOf(JsonPointer.class),
            same(tree));
        verify(collector2).collect(anyCollectionOf(JsonPointer.class),
            same(tree));
    }

    @Test(dependsOnMethods = "checkersAndCollectorsAreCalled")
    public void sameTreeOnlyGetsAnalyzedOnce()
        throws ProcessingException
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put(K1, "");
        node.put(K2, "");

        final SchemaTree tree = new CanonicalSchemaTree(node);
        analyzer.analyze(tree);
        analyzer.analyze(tree);

        verify(checker1, only()).checkSyntax(anyCollectionOf(JsonPointer.class),
            same(BUNDLE), any(ProcessingReport.class), same(tree));
        verify(checker2, only()).checkSyntax(anyCollectionOf(JsonPointer.class),
            same(BUNDLE), any(ProcessingReport.class), same(tree));
        verify(collector1, only()).collect(anyCollectionOf(JsonPointer.class),
            same(tree));
        verify(collector2, only()).collect(anyCollectionOf(JsonPointer.class),
            same(tree));

    }

    @Test(dependsOnMethods = "checkersAndCollectorsAreCalled")
    public void onlyKeywordsPresentInSchemaAreAnalyzed()
        throws ProcessingException
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put(K1, "");

        final SchemaTree tree = new CanonicalSchemaTree(node);
        analyzer.analyze(tree);
        analyzer.analyze(tree);

        verify(checker1, only()).checkSyntax(anyCollectionOf(JsonPointer.class),
            same(BUNDLE), any(ProcessingReport.class), same(tree));
        verify(collector1, only()).collect(anyCollectionOf(JsonPointer.class),
            same(tree));
        verifyZeroInteractions(checker2, collector2);

    }

    @Test
    public void unknownKeywordsAreReported()
        throws ProcessingException
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("foo", "");

        final SchemaTree tree = new CanonicalSchemaTree(node);
        final ProcessingReport report = analyzer.analyze(tree);

        final List<ProcessingMessage> list
            = Lists.newArrayList(report);

        assertEquals(list.size(), 1);
        final ProcessingMessage msg = list.get(0);

        final ImmutableList<String> l = ImmutableList.of("foo");
        assertMessage(msg).hasLevel(LogLevel.WARNING)
            .hasMessage(BUNDLE.printf("core.unknownKeywords", l))
            .hasField("ignored", l);
    }

    @Test
    public void nonObjectsGetRejectedImmediately()
        throws ProcessingException
    {
        final JsonNode node = JacksonUtils.nodeFactory().arrayNode();
        final SchemaTree tree = new CanonicalSchemaTree(node);
        final ProcessingReport report = analyzer.analyze(tree);

        final List<ProcessingMessage> list
            = Lists.newArrayList(report);

        assertEquals(list.size(), 1);
        final ProcessingMessage msg = list.get(0);
        final NodeType type = NodeType.ARRAY;
        assertMessage(msg).hasLevel(LogLevel.ERROR).hasField("found", type)
            .hasMessage(BUNDLE.printf("core.notASchema", type));
        verifyZeroInteractions(checker1, collector1, checker2, collector2);
    }
}

