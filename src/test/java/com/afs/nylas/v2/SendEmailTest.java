package com.afs.nylas.v2;

import com.nylas2.Draft;
import com.nylas2.NameEmail;
import com.nylas2.NylasAccount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

class SendEmailTest {

    private static String nylas2_accessToken;
    private static String test_email_from;
    private static String test_email_to;

    @BeforeAll
    static void initAll() throws IOException {

        // Set properties from resources/test.properties file
        Properties properties = new Properties();
        properties.load(new FileReader("src/test/resources/test.properties"));

        nylas2_accessToken = properties.getProperty("nylas2.accessToken");
        test_email_from = properties.getProperty("test.email.from");
        test_email_to = properties.getProperty("test.email.to");
    }

    /**
     * Tests sending a message
     */
    @Test
    public void sendEmail() throws Exception {

        com.nylas2.NylasClient client = new com.nylas2.NylasClient();
        NylasAccount account = client.account(nylas2_accessToken);

        Draft draft = new Draft();
        draft.setFrom(new NameEmail(test_email_from, test_email_from));
        draft.setTo(Collections.singletonList(new NameEmail(test_email_to, test_email_to)));
        draft.setSubject("Nylas API v2 Test");
        draft.setBody("This is a test email using Nylas API v2.");

        account.drafts().send(draft);
    }

}