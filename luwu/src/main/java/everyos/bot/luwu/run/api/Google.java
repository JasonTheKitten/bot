package everyos.bot.luwu.run.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.SafeSearchAnnotation;

import everyos.bot.luwu.util.FileUtil;

public class Google {
    private static final String CLIENT_SECRETS = "googleauth.json";
    private static final String CLIENT_SECRETS_SERVICE = "googleauths.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");
    private static final String APPLICATION_NAME = "JavaBot";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static YouTube youtubeService;
    private static Credential credentials;
    private static NetHttpTransport transport;

    static {
        try {
            transport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Credential authorize() throws IOException, GeneralSecurityException {
        if (credentials != null) return credentials;
        InputStream in = new FileInputStream(FileUtil.getAppData(CLIENT_SECRETS));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        Builder builder = new GoogleAuthorizationCodeFlow.Builder(transport, JSON_FACTORY, clientSecrets, SCOPES);
        builder.setCredentialDataStore(StoredCredential.getDefaultDataStore(new FileDataStoreFactory(FileUtil.getAppData("."))));
        builder.setAccessType("offline");
        GoogleAuthorizationCodeFlow flow = builder.build();
        credentials = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credentials;
    }

    private static Credentials authorize2() throws FileNotFoundException, IOException {
        File f = FileUtil.getAppData(CLIENT_SECRETS_SERVICE);
        if (!f.exists()) return null;
        return ServiceAccountCredentials.fromStream(new FileInputStream(f));
    }

    private static YouTube getService() throws GeneralSecurityException, IOException {
        return new YouTube.Builder(transport, JSON_FACTORY, authorize())
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    public static SearchListResponse search(String search)
        throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        if (youtubeService==null) youtubeService = getService();
        SearchListResponse response =
        	youtubeService.search()
            .list(Collections.singletonList("snippet"))
         	.setMaxResults(1L)
            .setQ(search)
            .setType(Collections.singletonList("video"))
            .execute();
        return response;
    }

    public static boolean isImageSafe(String url) throws Exception, IOException {
        //I think this is synchronous rn :/
    	//I actually can't use this, because it wants billing info, but I'll keep the code in
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setImageUri(url).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Type.SAFE_SEARCH_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(authorize2()))
            .build();
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            AnnotateImageResponse res = responses.get(0);
            if (res.hasError()) { return true; }
            SafeSearchAnnotation annotation = res.getSafeSearchAnnotation();
            return annotation.getAdultConfidence()<.60;
        } catch(Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}