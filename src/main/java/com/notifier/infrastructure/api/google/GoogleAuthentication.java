package com.notifier.infrastructure.api.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleAuthentication {

    private static final String CREDENTIALS_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    protected static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    protected static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    protected static final int SERVER_PORT = 8888;

    public Credential getCredentials(NetHttpTransport httpTransport) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(CREDENTIALS_PATH)) {
            if (in == null) {
                throw new IOException("Could not find credentials file: " + CREDENTIALS_PATH);
            }

            GoogleClientSecrets clientSecrets;
            try (InputStreamReader reader = new InputStreamReader(in)) {
                clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
            }

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(SERVER_PORT).build();
            try {
                return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            } finally {
                receiver.stop();
            }
        }
    }
}
