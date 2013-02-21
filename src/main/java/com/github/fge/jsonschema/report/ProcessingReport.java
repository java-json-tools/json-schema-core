package com.github.fge.jsonschema.report;

import com.github.fge.jsonschema.exceptions.ProcessingException;

public interface ProcessingReport
    extends MessageProvider, Iterable<ProcessingMessage>
{
    LogLevel getLogLevel();

    LogLevel getExceptionThreshold();

    void debug(ProcessingMessage message)
        throws ProcessingException;

    void info(ProcessingMessage message)
            throws ProcessingException;

    void warn(ProcessingMessage message)
                throws ProcessingException;

    void error(ProcessingMessage message)
                    throws ProcessingException;

    boolean isSuccess();

    void mergeWith(ProcessingReport other)
                        throws ProcessingException;
}
