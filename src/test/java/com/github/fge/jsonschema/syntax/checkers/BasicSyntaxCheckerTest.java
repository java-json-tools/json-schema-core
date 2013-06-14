/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.syntax.checkers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.SyntaxMessageBundle;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import static com.github.fge.jackson.NodeType.*;
import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;


public final class BasicSyntaxCheckerTest
{
    private static final MessageBundle BUNDLE = SyntaxMessageBundle.get();
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String KEYWORD = "foo";
    private static final EnumSet<NodeType> VALID_TYPES
        = EnumSet.of(ARRAY, INTEGER, STRING);

    @DataProvider
    public Iterator<Object[]> validTypes()
    {
        return SampleNodeProvider.getSamples(ARRAY, INTEGER, STRING);
    }

    @Test(dataProvider = "validTypes")
    public void syntaxCheckingSucceedsOnValidTypes(final JsonNode node)
        throws ProcessingException
    {
        final AbstractSyntaxChecker checker = spy(new DummyChecker());
        final ProcessingReport report = mock(ProcessingReport.class);
        final ObjectNode schema = FACTORY.objectNode();
        schema.put(KEYWORD, node);
        final SchemaTree tree = new CanonicalSchemaTree(schema);

        checker.checkSyntax(null, BUNDLE, report, tree);
        verify(checker).checkValue(null, BUNDLE, report, tree);
        verify(report, never()).error(anyMessage());
    }

    @DataProvider
    public Iterator<Object[]> invalidTypes()
    {
        return SampleNodeProvider.getSamplesExcept(ARRAY, INTEGER, STRING);
    }

    @Test(dataProvider = "invalidTypes")
    public void syntaxCheckingFailsOnInvalidTypes(final JsonNode node)
        throws ProcessingException
    {
        final NodeType type = NodeType.getNodeType(node);
        final ObjectNode schema = FACTORY.objectNode();
        schema.put(KEYWORD, node);
        final SchemaTree tree = new CanonicalSchemaTree(schema);

        final AbstractSyntaxChecker checker = spy(new DummyChecker());
        final ProcessingReport report = mock(ProcessingReport.class);

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        checker.checkSyntax(null, BUNDLE, report, tree);
        verify(report).error(captor.capture());
        verify(checker, never()).checkValue(null, BUNDLE, report, tree);

        final ProcessingMessage msg = captor.getValue();
        assertMessage(msg).hasField("keyword", KEYWORD).hasField("schema", tree)
            .hasMessage(BUNDLE.printf("common.incorrectType", type, VALID_TYPES))
            .hasField("domain", "syntax")
            .hasField("expected", EnumSet.of(ARRAY, INTEGER, STRING))
            .hasField("found", NodeType.getNodeType(node));
    }

    private static class DummyChecker
        extends AbstractSyntaxChecker
    {
        private DummyChecker()
        {
            super(KEYWORD, ARRAY, INTEGER, STRING);
        }

        @Override
        protected void checkValue(final Collection<JsonPointer> pointers,
            final MessageBundle bundle, final ProcessingReport report,
            final SchemaTree tree)
            throws ProcessingException
        {
        }
    }
}
