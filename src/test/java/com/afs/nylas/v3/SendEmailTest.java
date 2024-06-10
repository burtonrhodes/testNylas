package com.afs.nylas.v3;

import com.nylas.NylasClient;
import com.nylas.models.CreateDraftRequest;
import com.nylas.models.EmailName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

class SendEmailTest {

    private static String nylas3_apiKey;
    private static String nylas3_grantId_outlook_com;
    private static String nylas3_grantId_google;
    private static String test_email_to;

    @BeforeAll
    static void initAll() throws IOException {

        // Set properties from resources/test.properties file
        Properties properties = new Properties();
        properties.load(new FileReader("src/test/resources/test.properties"));

        nylas3_apiKey = properties.getProperty("nylas3.apiKey");
        nylas3_grantId_outlook_com = properties.getProperty("nylas3.grantId.outlook.com");
        nylas3_grantId_google = properties.getProperty("nylas3.grantId.google");
        test_email_to = properties.getProperty("test.email.to");
    }

    /**
     * Tests sending a message using Google
     * <br/>
     * Provider: GOOGLE
     */
    @Test
    public void sendEmail_google() throws Exception {

        NylasClient nylasClient = new NylasClient.Builder(nylas3_apiKey).build();
        CreateDraftRequest draftRequest = new CreateDraftRequest.Builder()
                .to(Collections.singletonList(new EmailName(test_email_to, test_email_to)))
                .subject("Test")
                .body("This is a test email using Google.")
                .build();

        nylasClient.drafts().create(nylas3_grantId_google, draftRequest);
    }

    /**
     * Tests sending a message using Microsoft
     * <br/>
     * Provider: MICROSOFT
     */
    @Test
    public void sendEmail_microsoft() throws Exception {

        NylasClient nylasClient = new NylasClient.Builder(nylas3_apiKey).build();
        CreateDraftRequest draftRequest = new CreateDraftRequest.Builder()
                .to(Collections.singletonList(new EmailName(test_email_to, test_email_to)))
                .subject("Test")
                .body("This is a test email using Microsoft.")
                .build();

        nylasClient.drafts().create(nylas3_grantId_outlook_com, draftRequest);
    }

}