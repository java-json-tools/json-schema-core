package com.github.fge.jsonschema.load.transform;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Filtering InputStream used to strip Javascript comments from JSON. Technically,
 * comments are not valid JSON and thus must be stripped if present. Usage of this
 * class should be opt-in by configuration, (not be enabled by default).
 */
public final class StripJavascriptCommentsInputStream
    extends FilterInputStream
{
    private final static int EOF = -1;
    private final static int STRING_DELIM = '"';
    private final static int STRING_ESCAPE = '\\';
    private final static int CR = '\r';
    private final static int LF = '\n';
    private final static int COMMENT_DELIM = '/';
    private final static int MULTILINE_COMMENT_DELIM = '*';
    private final static int LINE_COMMENT_DELIM = '/';

    private State state = new State();
    private State markState;

    public StripJavascriptCommentsInputStream(final InputStream in)
    {
        super(in);
    }

    @Override
    public int read()
        throws IOException
    {
        // return read ahead
        int read;
        if (state.readAhead != null) {
            read = state.readAhead;
            state.readAhead = null;
            return read;
        }

        // read from input stream, return if EOF
        read = in.read();
        if (read == EOF)
            return read;

        // read raw string literals and skip comments
        if (state.inString) {
            // read raw string; use read ahead if escape read
            if (read == STRING_ESCAPE)
                state.readAhead = in.read();
            else if (read == STRING_DELIM)
                state.inString = false;
        } else if (read == COMMENT_DELIM) {
            // skip line or multiline comments; use read ahead to
            // detect multicharacter comment delimiters
            int readAhead = in.read();
            if (readAhead == LINE_COMMENT_DELIM)
                read = skipLineComment();
            else if (readAhead == MULTILINE_COMMENT_DELIM)
                read = skipMultilineComment();
            else
                state.readAhead = readAhead;
        }
        return read;
    }

    /**
     * Skip line comment.
     *
     * @return line separator after comment or EOF character
     * @throws IOException on read exception
     */
    private int skipLineComment()
        throws IOException
    {
        // read until EOF, CR, or LF
        for (;;) {
            int read = in.read();
            if (read == EOF || read == CR || read == LF)
                return read;
        }
    }

    /**
     * Skip multiline comment.
     *
     * @return character immediately following comment
     * @throws IOException on read exception
     */
    private int skipMultilineComment()
        throws IOException
    {
        // read until EOF or end multiline comment delimiters
        for (;;) {
            int read = in.read();
            if (read == EOF)
                return read;
            if (read == MULTILINE_COMMENT_DELIM) {
                read = in.read();
                if (read == EOF)
                    return read;
                if (read == COMMENT_DELIM)
                    return in.read();
            }
        }
    }

    @Override
    public int read(final byte[] bytes, final int offset, final int length)
        throws IOException
    {
        // filter via read() instead of in.read(bytes, offset, length);
        // implementation taken from InputStream
        if (bytes == null)
            throw new NullPointerException();
        else if (offset < 0 || length < 0 || length > bytes.length - offset)
            throw new IndexOutOfBoundsException();
        else if (length == 0)
            return 0;
        int c = read();
        if (c == -1)
            return -1;
        bytes[offset] = (byte)c;
        int i = 1;
        try {
            for (; i < length; i++) {
                c = read();
                if (c == -1)
                    break;
                bytes[offset+i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        return i;
    }

    @Override
    public synchronized void mark(final int readlimit)
    {
        // mark and save read state
        in.mark(readlimit);
        markState = new State(state);
    }

    @Override
    public synchronized void reset() throws IOException
    {
        // reset and restore read state
        try {
            in.reset();
            state = markState;
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            markState = null;
        }
    }

    /**
     * Class capturing read state.
     */
    private final static class State
    {
        private boolean inString;
        private Integer readAhead;

        private State()
        {
        }

        private State(State state)
        {
            this.inString = state.inString;
            this.readAhead = state.readAhead;
        }
    }
}
