package everyos.discord.exobot.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;

import everyos.discord.exobot.StaticFunctions;

public class YoutubeUtil {
	private static final String CLIENT_SECRETS = "googleauth.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");
    private static final String APPLICATION_NAME = "JavaBot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static YouTube youtubeService;

    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        InputStream in = ClassLoader.getSystemResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        Builder builder = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES);
        builder.setCredentialDataStore(StoredCredential.getDefaultDataStore(new FileDataStoreFactory(new File(StaticFunctions.getAppData("youtube.config")))));
        builder.setAccessType("offline");
        GoogleAuthorizationCodeFlow flow = builder.build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        credential.getRefreshToken();
        return credential;
    }

    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    public static SearchListResponse search(String search)
        throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        if (youtubeService==null) youtubeService = getService();
        YouTube.Search.List request = youtubeService.search()
            .list("snippet");
        SearchListResponse response = request.setMaxResults(1L)
            .setQ(search)
            .setType("video")
            .execute();
        return response;
    }
}