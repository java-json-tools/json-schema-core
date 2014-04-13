/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.processing;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingReport;

/**
 * Main processing interface
 *
 * <p>Note that it is required that both inputs and outputs implement {@link
 * MessageProvider}: this allows a processor to grab a context-dependent
 * message to include into the report should the need arise. A {@link
 * ProcessingReport} is passed as an argument so that the processor can add
 * debug/info/warning/error messages.</p>
 *
 * <p>Ideally, processors <b>should not</b> throw unchecked exceptions.</p>
 *
 * @param <IN> input type for that processor
 * @param <OUT> output type for that processor
 */
public interface Processor<IN extends MessageProvider, OUT extends MessageProvider>
{
    /**
     * Process the input
     *
     * @param report the report to use while processing
     * @param input the input for this processor
     * @return the output
     * @throws ProcessingException processing failed
     */
    OUT process(final ProcessingReport report, final IN input)
        throws ProcessingException;
}
