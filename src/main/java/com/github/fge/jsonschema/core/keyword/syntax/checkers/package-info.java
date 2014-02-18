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

/**
 * Keyword syntax checkers
 *
 * <p>Syntax checkers play a critical role in the validation process. If syntax
 * validation checking fails, the validation process stops, and syntax
 * validation depends on these checkers.</p>
 *
 * <p>A successful syntax validation ensures that digesters and validators will
 * not see malformed inputs, and as such they do not even need to care whether
 * their input is valid -- they know that it is.</p>
 *
 * <p>Even though you can turn it off, it is not recommended. Take this schema
 * as an example:</p>
 *
 * <pre>
 *     {
 *         "$ref": "#/properties",
 *         "properties": {
 *             "type": { "type": "string" }
 *         }
 *     }
 * </pre>
 *
 * <p>This schema is syntactically valid; however, if someone tries and
 * validates against this schema, the JSON Reference leads to a schema which is
 * <b>not</b> valid; syntax checking will detect this since it takes place
 * right after JSON Reference processing. If there were no syntax checking, the
 * matching digester for the {@code type} keyword would throw a {@link
 * java.lang.NullPointerException}.</p>
 */

package com.github.fge.jsonschema.core.keyword.syntax.checkers;
