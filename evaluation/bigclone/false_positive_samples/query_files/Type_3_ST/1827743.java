package net.sf.jfacebookiml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

/**
 * 
 * @author Maxime Chéramy
 *
 */
public class FacebookHttpClient {

    private static Logger logger = Logger.getLogger(FacebookAdapter.class);

    /**
	 * The http client we use to simulate a browser.
	 */
    private HttpClient httpClient;

    /**
     * The default parameters.
     * Instantiated in {@link #setup setup}.
     */
    private static HttpParams defaultParameters = null;

    /**
     * The scheme registry.
     * Instantiated in {@link #setup setup}.
     */
    private static SchemeRegistry supportedSchemes;

    /**
     * The adapter of current account.
     */
    private FacebookAdapter adapter;

    public FacebookHttpClient(FacebookAdapter adapter) {
        this.adapter = adapter;
        setup();
        httpClient = createHttpClient();
    }

    /**
     * Performs general setup.
     * This should be called only once.
     */
    private static final void setup() {
        supportedSchemes = new SchemeRegistry();
        SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        sf = SSLSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("https", sf, 443));
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpProtocolParams.setHttpElementCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/2008052906 Firefox/3.0");
        defaultParameters = params;
    }

    /**
     * Set up proxy according to the proxy setting consts
     * @param dhc the http client we're setting up.
     */
    private void setUpProxy(DefaultHttpClient dhc) {
        final HttpHost proxy = new HttpHost(adapter.getProxyHost(), adapter.getProxyPort(), "http");
        dhc.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        AuthState authState = new AuthState();
        authState.setAuthScope(new AuthScope(proxy.getHostName(), proxy.getPort()));
        AuthScope authScope = authState.getAuthScope();
        Credentials creds = new UsernamePasswordCredentials(adapter.getProxyUsername(), adapter.getProxyPassword());
        dhc.getCredentialsProvider().setCredentials(authScope, creds);
        logger.trace("Facebook: executing request via " + proxy);
    }

    /**
     * Get default http client parameters
     * @return default http client parameters
     */
    private static final HttpParams getParams() {
        return defaultParameters;
    }

    /**
     * Creates a http client with default settings
     * @return a http client with default settings
     */
    private final HttpClient createHttpClient() {
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(getParams(), supportedSchemes);
        DefaultHttpClient dhc = new DefaultHttpClient(ccm, getParams());
        dhc.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        dhc.setRedirectHandler(new DefaultRedirectHandler());
        if (adapter.getUseProxy()) {
            setUpProxy(dhc);
        }
        return dhc;
    }

    /**
	 * The general facebook post method.
	 * @param host the host
	 * @param urlPostfix the post fix of the URL
	 * @param data the parameter
	 * @return the response string
	 */
    public String postMethod(String host, String urlPostfix, List<NameValuePair> nvps) {
        logger.info("Facebook: @executing facebookPostMethod():" + host + urlPostfix);
        String responseStr = null;
        try {
            HttpPost httpost = new HttpPost(host + urlPostfix);
            httpost.addHeader("Accept-Encoding", "gzip");
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            HttpResponse postResponse = httpClient.execute(httpost);
            HttpEntity entity = postResponse.getEntity();
            logger.trace("Facebook: facebookPostMethod: " + postResponse.getStatusLine());
            if (entity != null) {
                InputStream in = entity.getContent();
                if (postResponse.getEntity().getContentEncoding().getValue().equals("gzip")) {
                    in = new GZIPInputStream(in);
                }
                StringBuffer sb = new StringBuffer();
                byte[] b = new byte[4096];
                int n;
                while ((n = in.read(b)) != -1) {
                    sb.append(new String(b, 0, n));
                }
                responseStr = sb.toString();
                in.close();
                logger.trace("Facebook: " + responseStr);
                entity.consumeContent();
            }
            logger.info("Facebook: Post Method done(" + postResponse.getStatusLine().getStatusCode() + "), response string length: " + (responseStr == null ? 0 : responseStr.length()));
        } catch (IOException e) {
            logger.warn("Facebook: ", e);
        }
        return responseStr;
    }

    /**
	 * The general facebook get method.
	 * @param url the URL of the page we wanna get
	 * @return the response string
	 */
    public String getMethod(String url) {
        logger.info("Facebook: @executing facebookGetMethod():" + url);
        String responseStr = null;
        try {
            HttpGet loginGet = new HttpGet(url);
            loginGet.addHeader("Accept-Encoding", "gzip");
            HttpResponse response = httpClient.execute(loginGet);
            HttpEntity entity = response.getEntity();
            logger.trace("Facebook: facebookGetMethod: " + response.getStatusLine());
            if (entity != null) {
                InputStream in = response.getEntity().getContent();
                if (response.getEntity().getContentEncoding().getValue().equals("gzip")) {
                    in = new GZIPInputStream(in);
                }
                StringBuffer sb = new StringBuffer();
                byte[] b = new byte[4096];
                int n;
                while ((n = in.read(b)) != -1) {
                    sb.append(new String(b, 0, n));
                }
                responseStr = sb.toString();
                in.close();
                entity.consumeContent();
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                logger.warn("Facebook: Error Occured! Status Code = " + statusCode);
                responseStr = null;
            }
            logger.info("Facebook: Get Method done(" + statusCode + "), response string length: " + (responseStr == null ? 0 : responseStr.length()));
        } catch (IOException e) {
            logger.warn("Facebook: ", e);
        }
        return responseStr;
    }

    /**
	 * The general facebook get method but return a byte array (needed to download an image for example).
	 * @param url the URL of the page we wanna get
	 * @return the response string
	 */
    public byte[] getBytesMethod(String url) {
        logger.info("Facebook: @executing facebookGetMethod():" + url);
        byte[] responseBytes = null;
        try {
            HttpGet loginGet = new HttpGet(url);
            loginGet.addHeader("Accept-Encoding", "gzip");
            HttpResponse response = httpClient.execute(loginGet);
            HttpEntity entity = response.getEntity();
            logger.trace("Facebook: getBytesMethod: " + response.getStatusLine());
            if (entity != null) {
                InputStream in = response.getEntity().getContent();
                if (response.getEntity().getContentEncoding().getValue().equals("gzip")) {
                    in = new GZIPInputStream(in);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] b = new byte[4096];
                int n;
                while ((n = in.read(b)) != -1) {
                    out.write(b, 0, n);
                }
                responseBytes = out.toByteArray();
                in.close();
                entity.consumeContent();
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                logger.warn("Facebook: Error Occured! Status Code = " + statusCode);
                responseBytes = null;
            }
            logger.info("Facebook: Get Bytes Method done(" + statusCode + "), response bytes length: " + (responseBytes == null ? 0 : responseBytes.length));
        } catch (IOException e) {
            logger.warn("Facebook: ", e);
        }
        return responseBytes;
    }

    /**
	 * Gets the internal httpClient object. Should not be used (kept while FacebookAdapter still needs it).
	 * @return the httpClient object
	 */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
	 * Gets the cookies associated with the internal httpClient.
	 * @return the cookies.
	 */
    public List<Cookie> getCookies() {
        return ((DefaultHttpClient) httpClient).getCookieStore().getCookies();
    }
}
