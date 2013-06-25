package com.github.fge.jsonschema.load.transform;

import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static org.testng.Assert.assertEquals;

/**
 * Test StripJavascriptCommentsInputStream.
 */
public final class StripJavascriptCommentsInputStreamTest
{
    @Test
    public void testFiltering()
        throws IOException
    {
        // generate javascript commented source
        ByteArrayOutputStream generateInputStream = new ByteArrayOutputStream();
        PrintWriter generateInputWriter = new PrintWriter(new OutputStreamWriter(generateInputStream));
        generateInputWriter.println("/**");
        generateInputWriter.println(" * multiline comment");
        generateInputWriter.println(" */");
        generateInputWriter.println("{ // line comment");
        generateInputWriter.println("\"key\"/* multiline comment */: \"value\" // line comment");
        generateInputWriter.print("} // line comment");
        generateInputWriter.close();

        // filter javascript commented source
        InputStream filteredInputStream = new StripJavascriptCommentsInputStream(new ByteArrayInputStream(generateInputStream.toByteArray()));
        BufferedReader filteredInputReader = new BufferedReader(new InputStreamReader(filteredInputStream));
        assertEquals(filteredInputReader.readLine().trim(), "");
        assertEquals(filteredInputReader.readLine().trim(), "{");
        assertEquals(filteredInputReader.readLine().trim(), "\"key\": \"value\"");
        assertEquals(filteredInputReader.readLine().trim(), "}");
        assertEquals(filteredInputReader.readLine(), null);
    }
}
