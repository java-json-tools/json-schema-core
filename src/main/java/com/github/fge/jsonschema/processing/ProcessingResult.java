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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.CoreMessageBundle;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;

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
    private static final CoreMessageBundle BUNDLE
        = CoreMessageBundle.getInstance();

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
                .put("info", BUNDLE.getKey("processing.moreMessages")));
            ret.mergeWith(report);
        } catch (ProcessingException ignored) {
            // can't happen
        }
        return ret;
    }
}
