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

package com.github.fge.jsonschema.core.misc.analyzer;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

@Beta
final class SchemaAnalysis
{
    private final Set<JsonPointer> visited;
    private final ListProcessingReport report;

    SchemaAnalysis(final Set<JsonPointer> visited,
        final ListProcessingReport report)
    {
        this.visited = ImmutableSet.copyOf(visited);
        this.report = report;
    }

    boolean isVisited(final JsonPointer ptr)
    {
        return visited.contains(ptr);
    }

    ProcessingReport getReport()
    {
        return report;
    }
}
