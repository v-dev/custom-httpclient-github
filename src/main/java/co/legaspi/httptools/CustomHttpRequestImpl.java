package co.legaspi.httptools;

import co.legaspi.httptools.model.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * A complete HTTP request including: 1. request line, 2. headers, 3. body/entity.
 * After building all desired fields,  simply submit() it.
 *
 * @author vl
 */
public final class CustomHttpRequestImpl implements CustomHttpRequest {
    private static final Logger LOG = LogManager.getLogger();

    private static final int DEFAULT_HTTP_PORT = 80;

    private final Protocol protocol;
    private final Method method;
    private final String host;
    private final int port;
    private final String basePath;
    private final ProxyInfo proxy;
    private final Headers headers;
    private final Parameters queryParams;
    private final ICustomHttpEntity entity;  //TODO: wrap
    private final CookieJar cookieJar;
    private final boolean sslHostnameCheck;

    private final String encodedQueryString;


    public static class Builder {
        // Required parameters
        private final String host;
        private final String basePath;

        // Optional parameters - initialized to default values
        private Protocol protocol = Protocol.HTTP;
        private Method method = Method.GET;
        private int port = DEFAULT_HTTP_PORT;
        private ProxyInfo proxy = new ProxyInfo();
        private Headers headers = new Headers();
        private Parameters queryParams = new Parameters();
        private ICustomHttpEntity entity = new CustomHttpEntity();
        private CookieJar cookieJar = new CookieJar();
        private boolean sslHostnameCheck = false;

        private String encodedQueryString = "";

        public Builder(String host, String basePath) {
            this.host = host;
            this.basePath = basePath;
        }

        public Builder protocol(Protocol p) {
            protocol = p;
            return this;
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder proxy(ProxyInfo proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder addHeader(String name, String value) {
            headers.add(new Header(name, value));
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        public Builder queryParams(Parameters params) {
            this.queryParams = params;
            return this;
        }

        public Builder addQueryParam(String name, String value) {
            queryParams.add(new KeyValuePair(name, value));
            return this;
        }


        public Builder entity(ICustomHttpEntity entity) {
            this.entity = entity;
            return this;
        }

        public Builder cookieJar(CookieJar jar) {
            cookieJar = jar;
            return this;
        }

        /**
         * Whether or not to disable SSL Hostname check.
         * true: yes, disable the hostname check
         * ; false: no, do not disable hostname check [default]
         *
         * @param disableCheck
         * @return
         */
        public Builder disableSslHostnameCheck(boolean disableCheck) {
            sslHostnameCheck = disableCheck;
            return this;
        }


        public CustomHttpRequestImpl build() {
            return new CustomHttpRequestImpl(this);
        }
    }


    private CustomHttpRequestImpl(Builder builder) {
        protocol = builder.protocol;
        method = builder.method;
        host = builder.host;
        port = builder.port;
        basePath = builder.basePath;
        proxy = builder.proxy;
        headers = builder.headers;
        queryParams = builder.queryParams;
        entity = builder.entity;
        cookieJar = builder.cookieJar;
        sslHostnameCheck = builder.sslHostnameCheck;
        encodedQueryString = Utils.encodeQueryString(queryParams);
    }

    private String createPathWithEncodedQueryString() {
        return basePath + "?" + encodedQueryString;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Method getMethod() {
        return method;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return basePath;
    }

    public ProxyInfo getProxy() {
        return proxy;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Parameters getQueryParams() {
        return queryParams;
    }

    public ICustomHttpEntity getEntity() {
        return entity;
    }

    public CookieJar getCookieJar() {
        return cookieJar;
    }

    public boolean isSslHostnameCheckDisabled() {
        return sslHostnameCheck;
    }

    @Override
    public String toString() {
        return "CustomHttpRequestImpl [protocol=" + protocol + ", method="
                + method + ", host=" + host + ", port=" + port + ", basePath="
                + basePath + ", proxy=" + proxy + ", headers=" + headers
                + ", queryParams=" + queryParams + ", entity=" + entity
                + ", cookieJar=" + cookieJar + "]";
    }


    // below fields are to support the submit() method
    private HttpHost target;
    private DefaultHttpClient client;
    private HttpContext context;
    private HttpRequestBase requestMethod;
    private String fullPath;

    @Override
    public CustomHttpResponse submit() {
        setupApacheHttpClient();
        setupFullPath();
        setupRequestMethod();
        addHeaders();
        setupProxy();
        //printRequestToConsole();
        return executeRequest();
    }

    private void setupApacheHttpClient() {
        target = setupTargetHost();
        setupClientPerHostnameCheckFlag();
        context = new BasicHttpContext();
        context.setAttribute(ClientContext.COOKIE_STORE, getCookieJar());
        client.setCookieStore(getCookieJar());
        requestMethod = null;
    }

    private HttpHost setupTargetHost() {
        HttpHost host;
        if (getPort() == DEFAULT_HTTP_PORT) {
            host = new HttpHost(getHost(), -1, getProtocol().toString());
        } else {
            host = new HttpHost(getHost(), getPort(), getProtocol().toString());
        }

        return host;
    }

    private void setupClientPerHostnameCheckFlag() {
        if (isSslHostnameCheckDisabled()) {
            hostNameVerifier();
        } else {
            client = new DefaultHttpClient();
        }
    }

    private void hostNameVerifier() {
        final int httpsPort = 443;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());
            SSLSocketFactory sf = new SSLSocketFactory(sc, new X509HostnameVerifier() {
                public void verify(String string, X509Certificate xc) throws SSLException {
                }

                public void verify(String string, String[] strings, String[] strings1) throws SSLException {
                }

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }
            });
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", httpsPort, sf));
            schemeRegistry.register(new Scheme("http", DEFAULT_HTTP_PORT, new PlainSocketFactory()));
            client = new DefaultHttpClient(new SingleClientConnManager(schemeRegistry));
        } catch (Exception e) {
            LOG.error("Exception occurred setting up host name verifieer", e);
        }
    }


    private void setupFullPath() {
        if (getQueryParams().size() > 0) {
            fullPath = createPathWithEncodedQueryString();
        } else {
            fullPath = getPath();
        }
    }

    private void setupRequestMethod() {
        switch (getMethod()) {
            case GET:
                requestMethod = new HttpGet(fullPath);
                break;
            case POST:
                requestMethod = new HttpPost(fullPath);
                setupPostEntity();
                break;
        }
    }

    private void setupPostEntity() {
        try {
            getEntity().getContent();
            ((HttpPost) requestMethod).setEntity(getEntity());
        } catch (IllegalStateException ex) {
            LOG.error("Illegal state exception caught setting up post entity: ", ex);
        } catch (IOException ex) {
            LOG.error("IO Exception caught setting up post entity: ", ex);
        }
    }

    private void addHeaders() {
        if (getHeaders().size() > 0) {
            for (Header header : getHeaders()) {
                requestMethod.addHeader(header.getName(), header.getValue());
            }
        }
    }

    private void setupProxy() {
        HttpHost pxy = new HttpHost(proxy.getProxyHost(), proxy.getProxyPort(), proxy.getProxyProtocol().toString());
        if (getProxy().isProxyEnabled()) {
            client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, pxy);
        }
    }

    private void printRequestToConsole() {
        LOG.info("\n\n-------------------- CustomHttpRequestImpl.submit() Request [printRequestToConsole] --------------------");
        LOG.info(getRawRequest());
    }

    // TODO: the returned HTTP status messages need to be reviewed/corrected
    private CustomHttpResponse executeRequest() {
        final int clientError = 400;
        final int serverError = 500;

        CustomHttpResponse customResponse = null;
        HttpResponse response = null;
        try {
            response = client.execute(target, requestMethod, context);
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException during execute: ", e);
            response = new BasicHttpResponse(new BasicStatusLine
                    (new ProtocolVersion(protocol.toString(), 1, 1), clientError, "ClientProtocolException"));
            HttpEntity ent = new ByteArrayEntity("error400".getBytes());
            response.setEntity(ent);
        } catch (IOException e) {
            LOG.error("IOException during execute: ", e);
            response = new BasicHttpResponse(new BasicStatusLine
                    (new ProtocolVersion(protocol.toString(), 1, 1), serverError, "IOException"));
            HttpEntity ent = new ByteArrayEntity("error500".getBytes());
            response.setEntity(ent);
        } finally {
            customResponse = new CustomHttpResponseImpl(response);
        }
        return customResponse;
    }


    /**
     * Programmatic recreation of what the raw request should be.
     * Only available after the request has been submit'd, otherwise,
     * can throw an NPE.
     * <p/>
     * Currently, does not include any default information Apache HTTP Client
     * could add such as Connection and User-Agent headers.
     *
     * @return
     */
    public String getRawRequest() {
        StringBuilder raw = new StringBuilder();
        raw.append("\n").append(requestMethod.getRequestLine());
        raw.append("\nHost: ").append(target.toHostString());

        for (org.apache.http.Header requestHeader : requestMethod.getAllHeaders()) {
            raw.append("\n").append(requestHeader);
        }

        if (!getCookieJar().getCookies().isEmpty()) {
            for (Cookie cookie : getCookieJar().getCookies()) {
                raw.append("\nCookie: ").append(cookie.getName());
            }
        }

        //TODO: need to refactor and not rely on instanceof check
        //TODO: is this instanceof check even necessary or correct??
        //TODO: just want to return printable characters only -- no binary entities
        if (entity instanceof StringEntity) {
            try {
                raw.append("\n\n").append(entity.getContentAsString());
            } catch (IllegalStateException e) {
                LOG.error("Error trying to append content of request", e);
            }
        }

        return raw.toString();
    }

}
