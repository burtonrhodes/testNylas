package com.afs.nylas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nylas.NylasClient;
import com.nylas.models.*;
import com.nylas.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MaxSizeExceededTest {

    private static String nylas3_apiKey = "";
    private static String nylas3_grantId_outlook_com = "";
    private static String nylas3_grantId_google = "";
    private static String test_email_to = "";

    // 25MB is the limit for Google and Microsoft
    private static final int max_provider_attachment_size = 26 * 1024 * 1024;  // 26MB

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests sending a message with an attachment that is too large for the provider
     * <br/>
     * Provider: GOOGLE
     */
    @Test
    public void maxSizeExceededTest_google() throws IOException {

        // Create a file that is too large for the provider
        Path file_too_large = Paths.get("file_too_large.txt");
        this.generateFile(file_too_large, max_provider_attachment_size);

        NylasClient nylasClient = new NylasClient.Builder(nylas3_apiKey).build();
        CreateDraftRequest draftRequest = new CreateDraftRequest.Builder()
                .to(Collections.singletonList(new EmailName(test_email_to, test_email_to)))
                .subject("Test")
                .body("This is a test email message that is too large.")
                .attachments(Collections.singletonList(FileUtils.attachFileRequestBuilder(file_too_large.toString())))
                .build();

        // When creating a message with an attachment that is too large
        NylasApiError nylasApiError = assertThrows(NylasApiError.class, () -> {
            nylasClient.drafts().create(nylas3_grantId_google, draftRequest);
        });

        // Then the error should be of type "provider_error"
        assertEquals("provider_error", nylasApiError.getType());

        // And the error message should contain "too large"
        String strProviderErrors = objectMapper.writeValueAsString(nylasApiError.getProviderError());
        assertTrue(strProviderErrors.contains("too large"));

        // Clean up
        Files.delete(file_too_large);
    }

    /**
     * Tests sending a message with an attachment that is too large for the provider
     * <br/>
     * Provider: MICROSOFT
     */
    @Test
    public void maxSizeExceededTest_microsoft() throws IOException {

        // Create a file that is too large for the provider
        Path file_too_large = Paths.get("file_too_large.txt");
        this.generateFile(file_too_large, max_provider_attachment_size);

        NylasClient nylasClient = new NylasClient.Builder(nylas3_apiKey).build();
        CreateDraftRequest draftRequest = new CreateDraftRequest.Builder()
                .to(Collections.singletonList(new EmailName(test_email_to, test_email_to)))
                .subject("Test")
                .body("This is a test email message that is too large.")
                .attachments(Collections.singletonList(FileUtils.attachFileRequestBuilder(file_too_large.toString())))
                .build();

        // When creating a message with an attachment that is too large
        NylasApiError nylasApiError = assertThrows(NylasApiError.class, () -> {
            nylasClient.drafts().create(nylas3_grantId_outlook_com, draftRequest);
        });

        // Then the error should be of type "provider_error"
        assertEquals("provider_error", nylasApiError.getType());

        // And the error message should contain "too large" (or something similar)
        String strProviderErrors = objectMapper.writeValueAsString(nylasApiError.getProviderError());
        assertTrue(strProviderErrors.contains("too large"));

        // Clean up
        Files.delete(file_too_large);
    }

    /**
     * Generates a file with the specified size in bytes
     *
     * @param filePath Path to the file to be generated
     * @param fileSize Size of the file in bytes
     * @throws IOException If an I/O error occurs
     */
    private void generateFile(Path filePath, int fileSize) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // The character to be written to the file
            char ch = 'A';

            // Define the size of the buffer (10KB)
            int bufferSize = 10 * 1024;
            char[] buffer = new char[bufferSize];

            // Fill the buffer with the character 'A'
            Arrays.fill(buffer, ch);

            int bytesWritten = 0;

            // Write to the file in chunks
            while (bytesWritten < fileSize) {
                if (bytesWritten + bufferSize > fileSize) {
                    // If remaining bytes are less than buffer size, adjust the buffer size
                    bufferSize = fileSize - bytesWritten;
                }
                writer.write(buffer, 0, bufferSize);
                bytesWritten += bufferSize;
            }
        }
    }

}