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
        String testFile = TEST_DIRECTORY + "file.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(testFile))) {
            writer.println("abc def");
            writer.println("ghi jkl");
            writer.println("mno pqr");
        }
        try (ReverseFileReader reader = new ReverseFileReader(testFile)) {
            assertEquals("mno pqr", reader.readLine());
            assertEquals("ghi jkl", reader.readLine());
            assertEquals("abc def", reader.readLine());
            assertNull(reader.readLine()); // End of file
        } finally {
            new File(testFile).delete();
        }
    }

    @Test
    public void testLargeFile() throws IOException {
        String largeFile = TEST_DIRECTORY + "large-file.txt";
        // Create a large file with 1 million lines
        try (PrintWriter writer = new PrintWriter(new FileWriter(largeFile))) {
            for (int i = 1; i <= 1_000_000; i++) {
                writer.println("Line " + i);
            }
        }
        try (ReverseFileReader reader = new ReverseFileReader(largeFile)) {
            for (int i = 1_000_000; i >= 1; i--) {
                assertEquals("Line " + i, reader.readLine());
            }
            assertNull(reader.readLine());
        } finally {
            new File(largeFile).delete();
        }
    }

    @Test
    public void testEmptyFile() throws IOException {
        String emptyFile = TEST_DIRECTORY + "empty-file.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(emptyFile))) {
            // Write nothing to create an empty file
        }
        try (ReverseFileReader reader = new ReverseFileReader(emptyFile)) {
            assertNull(reader.readLine()); // Should return null for an empty file
        } finally {
            new File(emptyFile).delete();
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testNonExistentFile() throws IOException {
        String nonExistentFilePath = TEST_DIRECTORY + "nonexistent.txt";
        new ReverseFileReader(nonExistentFilePath);
    }

    @Test(expected = IOException.class)
    public void testIOExceptionDuringRead() throws IOException {
        String errorFilePath = TEST_DIRECTORY + "error-file.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(errorFilePath))) {
            writer.println("test");
        }

        try (ReverseFileReader reader = new ReverseFileReader(errorFilePath)) {
            // Simulate a scenario where an IOException might occur during read
            File file = new File(errorFilePath);
            boolean isSuccess = file.setReadable(false);
            if (!file.setReadable(false)) {
                fail("Could not set file to unreadable.");
            }
            reader.readLine(); // This should throw an IOException
        } finally {
            // Clean up
            new File(errorFilePath).setReadable(true); // Reset the file permissions
            new File(errorFilePath).delete();
        }
    }

    @After
    public void tearDown() {}
}
