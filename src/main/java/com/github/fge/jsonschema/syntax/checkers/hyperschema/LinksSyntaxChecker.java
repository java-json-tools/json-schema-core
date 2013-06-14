package com.github.fge.jsonschema.syntax.checkers.hyperschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateParseException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class LinksSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final List<String> REQUIRED_LDO_PROPERTIES
        = ImmutableList.of("href", "rel");

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
        final MessageBundle bundle, final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);
        final int size = node.size();

        JsonNode ldo;
        NodeType type;
        Set<String> set;
        List<String> list;

        for (int index = 0; index < size; index++) {
            ldo = getNode(tree).get(index);
            type = NodeType.getNodeType(ldo);
            if (type != NodeType.OBJECT) {
                report.error(LDOMsg(tree, bundle, "hsLinksLdoBadType", index)
                    .put("expected", NodeType.OBJECT)
                    .putArgument("found", type));
                continue;
            }
            set = Sets.newHashSet(ldo.fieldNames());
            list = Lists.newArrayList(REQUIRED_LDO_PROPERTIES);
            list.removeAll(set);
            if (!list.isEmpty()) {
                report.error(LDOMsg(tree, bundle, "hsLinksLdoMissingReq", index)
                    .put("required", REQUIRED_LDO_PROPERTIES)
                    .putArgument("missing", list));
                continue;
            }
            if (ldo.has("schema"))
                pointers.add(JsonPointer.of(keyword, index, "schema"));
            if (ldo.has("targetSchema"))
                pointers.add(JsonPointer.of(keyword, index, "targetSchema"));
            checkLDO(report, bundle, tree, index);
        }
    }

    private void checkLDO(final ProcessingReport report,
        final MessageBundle bundle, final SchemaTree tree, final int index)
        throws ProcessingException
    {
        final JsonNode ldo = getNode(tree).get(index);

        String value;

        checkLDOProperty(report, bundle, tree, index, "rel", NodeType.STRING,
            "hsLinksLdoRelWrongType");

        if (checkLDOProperty(report, bundle, tree, index, "href",
            NodeType.STRING, "hsLinksLdoHrefWrongType")) {
            value = ldo.get("href").textValue();
            try {
                new URITemplate(value);
            } catch (URITemplateParseException ignored) {
                report.error(LDOMsg(tree, bundle, "hsLinksLdoHrefIllegal",
                    index).putArgument("value", value));
            }
        }

        checkLDOProperty(report, bundle, tree, index, "title", NodeType.STRING,
            "hsLinksLdoTitleWrongType");

        if (checkLDOProperty(report, bundle, tree, index, "mediaType",
            NodeType.STRING, "hsLinksLdoMediatypeWrongType")) {
            value = ldo.get("mediaType").textValue();
            try {
                MediaType.parse(value);
            } catch (IllegalArgumentException ignored) {
                report.error(LDOMsg(tree, bundle, "hsLinksLdoMediatypeIllegal",
                    index).putArgument("value", value));
            }
        }

        checkLDOProperty(report, bundle, tree, index, "method",
            NodeType.STRING, "hsLinksLdoMethodWrongType");

        if (checkLDOProperty(report, bundle, tree, index, "encType",
            NodeType.STRING, "hsLinksLdoEnctypeWrongType")) {
            value = ldo.get("encType").textValue();
            try {
                MediaType.parse(value);
            } catch (IllegalArgumentException ignored) {
                report.error(LDOMsg(tree, bundle, "hsLinksLdoEnctypeIllegal",
                    index).putArgument("value", value));
            }
        }
    }

    private ProcessingMessage LDOMsg(final SchemaTree tree,
        final MessageBundle bundle, final String key, final int index)
    {
        return newMsg(tree, bundle, key).put("index", index);
    }

    private boolean checkLDOProperty(final ProcessingReport report,
        final MessageBundle bundle, final SchemaTree tree, final int index,
        final String name, final NodeType expected, final String key)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree).get(index).get(name);

        if (node == null)
            return false;

        final NodeType type = NodeType.getNodeType(node);

        if (type == expected)
            return true;

        report.error(LDOMsg(tree, bundle, key, index).put("expected", expected)
                .putArgument("found", type));
        return false;
    }
}
