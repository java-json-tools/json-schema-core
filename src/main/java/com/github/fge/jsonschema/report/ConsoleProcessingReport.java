package com.github.fge.jsonschema.report;

/**
 * A simple processing report printing its messages to {@code System.out}
 */
public final class ConsoleProcessingReport
    extends AbstractProcessingReport
{
    public ConsoleProcessingReport(final LogLevel logLevel,
        final LogLevel exceptionThreshold)
    {
        super(logLevel, exceptionThreshold);
    }

    public ConsoleProcessingReport(final LogLevel logLevel)
    {
        super(logLevel);
    }

    public ConsoleProcessingReport()
    {
    }

    @Override
    public void log(final LogLevel level, final ProcessingMessage message)
    {
        System.out.println(message);
    }
}
