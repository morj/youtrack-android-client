package jetbrains.android.data;

import android.util.Log;
import com.sun.deploy.net.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YouTrackDAO {
    private static final String LOGIN_PATH = "%s/rest/user/login?user=%s&password=%s";
    private static final String ISSUES_PATH = "%s/rest/project/issues/%s?filter=%s&after=%d&max=%d";

    private HttpClient httpClient;
    private String baseUri;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    private boolean loggedIn = false;

    public YouTrackDAO() {
        httpClient = new DefaultHttpClient();
    }

    public void login(String baseUri, String login, String password) throws RequestFailedException {
        this.baseUri = baseUri;
        try {
            String uri = String.format(LOGIN_PATH, baseUri, quote(login), quote(password));
            HttpPost post = new HttpPost(uri);
            HttpResponse response = httpClient.execute(post);
            assertStatus(response);
        } catch (IOException e) {
            throw new RequestFailedException(e);
        } finally {
//            httpClient.getConnectionManager().shutdown();
        }
    }

    public List<Map<String, Object>> getIssues(String project, String query, int position, int max) throws RequestFailedException {
        final ArrayList<Map<String, Object>> issues = new ArrayList<Map<String, Object>>();
        try {
            String uri = String.format(ISSUES_PATH, baseUri, quote(project), quote(query), position, max);
            Log.i(getClass().getSimpleName(), "request: " + uri);
            HttpResponse response = httpClient.execute(new HttpGet(uri));
            assertStatus(response);

            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(response.getEntity().getContent(), new DefaultHandler2() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if ("issue".equalsIgnoreCase(localName)) {
                        Map<String, Object> issue = new HashMap<String, Object>();
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String name = attributes.getLocalName(i);
                            String value = attributes.getValue(i);
                            issue.put(name, value);
                        }
                        issues.add(issue);
                    }
                }
            });

            for (Map<String, Object> issue: issues) {
                issue.put("title", issue.get("id") + " " + issue.get("summary"));
            }
        } catch (Exception e) {
            throw new RequestFailedException(e);
        } finally {
//            httpClient.getConnectionManager().shutdown();
        }
        return issues;
    }


    private void assertStatus(HttpResponse response) throws RequestFailedException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 300) {
            throw new RequestFailedException(status + ": " + response.getStatusLine().getReasonPhrase());
        }
        loggedIn = true;
    }

    private String quote(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
