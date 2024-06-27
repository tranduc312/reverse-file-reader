package com.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class ReverseFileReaderTest {
    private static final String TEST_DIRECTORY = "src/test/resources/";

    @Before
    public void setUp() {}

    @Test
    public void testReadLine() throws IOException {
        String testFilePath = TEST_DIRECTORY + "file.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(testFilePath))) {
            writer.println("abc def");
            writer.println("ghi jkl");
            writer.println("mno pqr");
        }
        try (ReverseFileReader reader = new ReverseFileReader(testFilePath)) {
            assertEquals("mno pqr", reader.readLine());
            assertEquals("ghi jkl", reader.readLine());
            assertEquals("abc def", reader.readLine());
            assertNull(reader.readLine()); // End of file
        } finally {
            new File(testFilePath).delete();
        }
    }

    @Test
    public void testLargeFile() throws IOException {
        String largeFilePath = TEST_DIRECTORY + "large-file.txt";
        // Create a large file with 1 million lines
        try (PrintWriter writer = new PrintWriter(new FileWriter(largeFilePath))) {
            for (int i = 1; i <= 1_000_000; i++) {
                writer.println("Line " + i);
            }
        }
        try (ReverseFileReader reader = new ReverseFileReader(largeFilePath)) {
            for (int i = 1_000_000; i >= 1; i--) {
                assertEquals("Line " + i, reader.readLine());
            }
            assertNull(reader.readLine());
        } finally {
            new File(largeFilePath).delete();
        }
    }

    @Test
    public void testEmptyFile() throws IOException {
        String emptyFilePath = TEST_DIRECTORY + "empty-file.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(emptyFilePath))) {
            // Write nothing to create an empty file
        }
        try (ReverseFileReader reader = new ReverseFileReader(emptyFilePath)) {
            assertNull(reader.readLine()); // Should return null for an empty file
        } finally {
            new File(emptyFilePath).delete();
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testNonExistentFile() throws IOException {
        String nonExistentFilePath = TEST_DIRECTORY + "nonexistent.txt";
        new ReverseFileReader(nonExistentFilePath);
    }

    @Test(expected = IOException.class)
    public void testIOExceptionOnClosedFile() throws IOException {
        String ioExceptionFilePath = TEST_DIRECTORY + "ioexception.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(ioExceptionFilePath))) {
            writer.println("This is a test file.");
        }

        ReverseFileReader reader = new ReverseFileReader(ioExceptionFilePath);
        reader.close(); // Close the file to simulate IOException on read

        try {
            reader.readLine();
        } finally {
            // Clean up
            new File(ioExceptionFilePath).delete();
        }
    }

    @After
    public void tearDown() {}
}
