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

package com.github.fge.jsonschema.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.exceptions.ExceptionProvider;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;
import com.github.fge.jsonschema.messages.CoreMessageBundles;
import com.github.fge.jsonschema.messages.MessageBundle;
import com.github.fge.jsonschema.util.AsJson;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

/**
 * One processing message
 *
 * <p>Internally, a processing message has a {@link Map} whose keys are strings
 * and values are {@link JsonNode}s. Note that all methods altering the message
 * contents accept {@code null}: in this case, the value for that key will be a
 * {@link NullNode}.</p>
 *
 * <p>You can alter the behaviour of a processing message in two ways: its log
 * level and its {@link ExceptionProvider} (used in {@link #asException()}.</p>
 *
 * <p>All mutation methods of a message return {@code this}.</p>
 */
@NotThreadSafe
public final class ProcessingMessage
    implements AsJson
{
    private static final MessageBundle BUNDLE
        = CoreMessageBundles.PROCESSING;

    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final Map<String, JsonNode> map = Maps.newLinkedHashMap();

    private ExceptionProvider exceptionProvider
        = SimpleExceptionProvider.getInstance();

    private LogLevel level;

    /**
     * Constructor
     *
     * <p>By default, a message is generated with a log level of {@link
     * LogLevel#INFO}.</p>
     */
    public ProcessingMessage()
    {
        setLogLevel(LogLevel.INFO);
    }

    /**
     * Set the exception provider for that particular message
     *
     * @param exceptionProvider the exception provider
     * @return this
     * @throws ProcessingError exception provider is null
     */
    public ProcessingMessage setExceptionProvider(
        final ExceptionProvider exceptionProvider)
    {
        if (exceptionProvider == null)
            throw new ProcessingError(
                BUNDLE.getString("nullExceptionProvider"));
        this.exceptionProvider = exceptionProvider;
        return this;
    }

    /**
     * Set the log level for this message
     *
     * @param level the log level
     * @return this
     * @throws ProcessingError log level is null
     */
    public ProcessingMessage setLogLevel(final LogLevel level)
    {
        if (level == null)
            throw new ProcessingError(BUNDLE.getString("nullLevel"));
        this.level = Preconditions.checkNotNull(level,
            "log level cannot be null");
        return put("level", level);
    }

    /**
     * Set the main message
     *
     * @param message the message as a string
     * @return this
     */
    public ProcessingMessage message(final String message)
    {
        return put("message", message);
    }

    /**
     * Set the main message
     *
     * <p>The value type can be anything. It is your responsibility to make sure
     * that {@link Object#toString()} is implemented correctly!</p>
     *
     * @param value the value
     * @param <T> the type of the value
     * @return this
     */
    public <T> ProcessingMessage message(final T value)
    {
        return put("message", value);
    }

    /**
     * Get the main message
     *
     * @return the main message as a string
     */
    public String getMessage()
    {
        return map.containsKey("message") ? map.get("message").asText()
            : "(no message)";
    }

    /**
     * Add a key/value pair to this message
     *
     * <p>This is the main method. All other put methods call this one.</p>
     *
     * <p>Note that if the key is {@code null}, the content is <b>ignored</b>.
     * </p>
     * @param key the key
     * @param value the value as a {@link JsonNode}
     * @return this
     */
    public ProcessingMessage put(final String key, final JsonNode value)
    {
        if (key == null)
            return this;
        if (value == null)
            return putNull(key);
        map.put(key, value.deepCopy());
        return this;
    }

    /**
     * Add a key/value pair to this message
     *
     * @param key the key
     * @param asJson the value, which implements {@link AsJson}
     * @return this
     */
    public ProcessingMessage put(final String key, final AsJson asJson)
    {
        return put(key, asJson.asJson());
    }

    /**
     * Add a key/value pair to this message
     *
     * @param key the key
     * @param value the value
     * @return this
     */
    public ProcessingMessage put(final String key, final String value)
    {
        return value == null ? putNull(key) : put(key, FACTORY.textNode(value));
    }

    /**
     * Add a key/value pair to this message
     *
     * @param key the key
     * @param value the value as an integer
     * @return this
     */
    public ProcessingMessage put(final String key, final int value)
    {
        return put(key, FACTORY.numberNode(value));
    }

    /**
     * Add a key/value pair to this message
     *
     * <p>As for {@link #message(Object)}, ensure that {@code toString()} is
     * correctly implemented.</p>
     *
     * @param key the key
     * @param value the value
     * @param <T> the type of the value
     * @return this
     */
    public <T> ProcessingMessage put(final String key, final T value)
    {
        return value == null
            ? putNull(key)
            : put(key, FACTORY.textNode(value.toString()));
    }

    /**
     * Add a key/value pair to this message, where the value is a collection of
     * items
     *
     * <p>This will put all values (again, using {@link #toString()} of the
     * collection into an array.</p>
     *
     * @param key the key
     * @param values the collection of values
     * @param <T> the element type of the collection
     * @return this
     */
    public <T> ProcessingMessage put(final String key, final Iterable<T> values)
    {
        if (values == null)
            return putNull(key);
        final ArrayNode node = FACTORY.arrayNode();
        for (final T value: values)
            node.add(value == null
                ? FACTORY.nullNode()
                : FACTORY.textNode(value.toString()));
        return put(key, node);
    }

    /**
     * Get the log level for this message
     *
     * @return the log level
     */
    public LogLevel getLogLevel()
    {
        return level;
    }

    /**
     * Put a {@link NullNode} as a value for a key
     *
     * <p>Note: if {@code key} is null, the put will be <b>ignored</b>.</p>
     *
     * @param key the key
     * @return this
     */
    private ProcessingMessage putNull(final String key)
    {
        if (key == null)
            return this;
        map.put(key, FACTORY.nullNode());
        return this;
    }

    @Override
    public JsonNode asJson()
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.putAll(map);
        return ret;
    }

    /**
     * Build an exception out of this message
     *
     * <p>This uses the {@link ExceptionProvider} built into the message and
     * invokes {@link ExceptionProvider#doException(ProcessingMessage)} with
     * {@code this} as an argument.</p>
     *
     * @return an exception
     * @see #setExceptionProvider(ExceptionProvider)
     */
    public ProcessingException asException()
    {
        return exceptionProvider.doException(this);
    }

    @Override
    public String toString()
    {
        final Map<String, JsonNode> tmp = Maps.newLinkedHashMap(map);
        final String message = tmp.remove("message").textValue();
        final StringBuilder sb = new StringBuilder().append(level).append(": ");
        sb.append(message == null ? "(no message)" : message);
        for (final Map.Entry<String, JsonNode> entry: tmp.entrySet())
            sb.append("\n    ").append(entry.getKey()).append(": ")
                .append(entry.getValue());
        return sb.append('\n').toString();
    }
}
