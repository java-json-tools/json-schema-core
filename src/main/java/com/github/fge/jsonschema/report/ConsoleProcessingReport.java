package com.github.fge.jsonschema.report;

import java.io.PrintStream;

/**
 * A simple processing report printing its messages to the console
 *
 * <p>It will print all messages with a log level of {@link LogLevel#INFO} or
 * less to {@code System.out}, and messages with a log level of {@link
 * LogLevel#WARNING} or higher to {@code System.err}.</p>
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
        final PrintStream out = level.compareTo(LogLevel.WARNING) < 0
            ? System.out : System.err;

        out.println(message);
    }
}
