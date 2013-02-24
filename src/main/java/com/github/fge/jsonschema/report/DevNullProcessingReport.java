package com.github.fge.jsonschema.report;

/**
 * A processing report which logs absolutely nothing
 *
 * <p>Use this class if all you are interested in is the processing status.</p>
 */
public final class DevNullProcessingReport
    extends AbstractProcessingReport
{
    public DevNullProcessingReport(final LogLevel logLevel,
        final LogLevel exceptionThreshold)
    {
        super(logLevel, exceptionThreshold);
    }

    public DevNullProcessingReport(final LogLevel logLevel)
    {
        super(logLevel);
    }

    public DevNullProcessingReport()
    {
    }

    @Override
    public void log(final LogLevel level, final ProcessingMessage message)
    {
    }
}
