package com.github.fge.jsonschema.syntax.checkers.hyperschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.tree.SchemaTree;

import java.util.Collection;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

public final class LinksSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker INSTANCE = new LinksSyntaxChecker();

    private LinksSyntaxChecker()
    {
        super("links", NodeType.ARRAY);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);
        final int size = node.size();
        for (int index = 0; index < size; index++)
            checkLDO(report, tree, index);
    }

    private void checkLDO(final ProcessingReport report, final SchemaTree tree,
        final int index)
        throws ProcessingException
    {
        final JsonNode ldo = getNode(tree).get(index);
        final NodeType type = NodeType.getNodeType(ldo);
        if (type != NodeType.OBJECT)
            report.error(newMsg(tree, HS_LINKS_LDO_BAD_TYPE)
                .put("index", index).put("found", type)
                .put("expected", NodeType.OBJECT));
    }
}
