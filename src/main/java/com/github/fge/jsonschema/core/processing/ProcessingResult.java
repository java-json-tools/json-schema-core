/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.processing;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

/**
 * Wrapper class over a processing result
 *
 * <p>This class is useful when you write your own wrappers over processors and
 * want to be able to customize the output for said classes. It offers two
 * static factory methods and offers the ability to grab the processing result,
 * the processing report and the success status.</p>
 *
 * <p>It also offers a wrapper which swallows {@link ProcessingException}s and
 * wraps them in a report instead (which will always be a {@link
 * ListProcessingReport}: in this case, the exception message will always be
 * the first message in the report.</p>
 *
 * @param <R> type of the processing output
 */
public final class ProcessingResult<R extends MessageProvider>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final ProcessingReport report;
    private final R result;

    private ProcessingResult(final ProcessingReport report, final R result)
    {
        BUNDLE.checkNotNull(report, "processing.nullReport");
        this.report = report;
        this.result = result;
    }

    /**
     * Build a result out of a processor, a report and an input
     *
     * @param processor the processor
     * @param report the report
     * @param input the input
     * @param <IN> type of the input
     * @param <OUT> type of the output
     * @return a processing result
     * @throws ProcessingException processing failed
     * @throws NullPointerException the processor or report are null
     */
    public static <IN extends MessageProvider, OUT extends MessageProvider>
        ProcessingResult<OUT> of(final Processor<IN, OUT> processor,
        final ProcessingReport report, final IN input)
        throws ProcessingException
    {
        BUNDLE.checkNotNull(processor, "processing.nullProcessor");
        final OUT out = processor.process(report, input);
        return new ProcessingResult<OUT>(report, out);
    }

    /**
     * Build a result out of a computation and wrap any processing exception
     *
     * @param processor the processor to use
     * @param report the report to use
     * @param input the input
     * @param <IN> type of the input
     * @param <OUT> type of the output
     * @return a processing result
     * @throws NullPointerException the processor or report are null
     */
    public static <IN extends MessageProvider, OUT extends MessageProvider>
        ProcessingResult<OUT> uncheckedResult(
        final Processor<IN, OUT> processor, final ProcessingReport report,
        final IN input)
    {
        try {
            return of(processor, report, input);
        } catch (ProcessingException e) {
            return new ProcessingResult<OUT>(buildReport(report, e), null);
        }
    }

    /**
     * Get the report out of this result
     *
     * @return the report
     */
    public ProcessingReport getReport()
    {
        return report;
    }

    /**
     * Get the result of the computation
     *
     * <p>Note that in the event of a processing failure, the return value of
     * this method is <b>undefined</b>.</p>
     *
     * @return the result
     */
    public R getResult()
    {
        return result;
    }

    /**
     * Tell whether the result is a success
     *
     * @return true if the computation occurred without a problem
     * @see ProcessingReport#isSuccess()
     */
    public boolean isSuccess()
    {
        return report.isSuccess();
    }

    private static ProcessingReport buildReport(final ProcessingReport report,
        final ProcessingException e)
    {
        final ListProcessingReport ret
            = new ListProcessingReport(LogLevel.DEBUG, LogLevel.NONE);
        try {
            ret.fatal(e.getProcessingMessage()
                .put("info", BUNDLE.getMessage("processing.moreMessages")));
            ret.mergeWith(report);
        } catch (ProcessingException ignored) {
            // can't happen
        }
        return ret;
    }
}
