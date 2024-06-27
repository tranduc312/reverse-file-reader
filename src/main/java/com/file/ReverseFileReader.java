package com.file;


import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.function.BiPredicate;

public class ReverseFileReader implements Closeable {
    private static final byte LF = 0xA; // Newline character (LF)
    private static final byte CR = 0xD; // Carriage return (CR)
    private static final String READ_MODE  = "r";

    private final RandomAccessFile randomAccessFile;
    private long currentPosition;
    private final BiPredicate<Long, Long> isLastReadPosition = (filePointer, currentPosition) -> filePointer != currentPosition - 1;

    public ReverseFileReader(final String filePath) throws IOException {
        try {
            randomAccessFile = new RandomAccessFile(filePath, READ_MODE);
            currentPosition = randomAccessFile.length();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(String.format("The specified file was not found: %s", filePath));
        } catch (IOException e) {
            throw new IOException(String.format("An I/O error occurred while initializing the file reader: %s", e.getMessage()));
        }
    }

    public String readLine() throws IOException {
        if (currentPosition <= 0) {
            return null; // No more lines to read
        }

        StringBuilder line = new StringBuilder();
        long filePointer = currentPosition - 1;

        try {
            while (filePointer >= 0) {
                randomAccessFile.seek(filePointer);
                int readByte = randomAccessFile.readByte();

                if (readByte == LF) { // is \n Newline character (LF)
                    if (isLastReadPosition.test(filePointer, currentPosition)) {
                        filePointer--;
                        break;
                    }
                } else if (readByte == CR) { // is \r Carriage return (CR)
                    // Skip CR and check next character
                    if (isLastReadPosition.test(filePointer, currentPosition)) {
                        filePointer--;
                        continue;
                    }
                } else {
                    line.append((char) readByte);
                }
                filePointer--;
            }
        } catch (IOException e) {
            throw new IOException(String.format("An I/O error occurred while reading the file: %s", e.getMessage()));
        }
        currentPosition = filePointer;
        return line.reverse().toString();
    }

    @Override
    public void close() throws IOException {
        try {
            if (Objects.nonNull(randomAccessFile))
                randomAccessFile.close();
        } catch (IOException e) {
            throw new IOException(String.format("An error occurred while closing the file: %s", e.getMessage()));
        }
    }
}
