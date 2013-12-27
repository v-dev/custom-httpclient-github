package co.legaspi.httptools.test.submit;

import co.legaspi.httptools.CustomHttpRequestImpl;
import co.legaspi.httptools.CustomHttpResponse;
import co.legaspi.httptools.Utils;
import co.legaspi.httptools.model.Method;
import co.legaspi.httptools.model.Parameters;
import co.legaspi.httptools.model.Protocol;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Integration Tests of the CustomHttpRequestImpl class' submit method using
 * the LittleProxy.
 *
 * @author v
 */
public class CustomHttpRequestImplLittleProxyIT extends BaseLittleProxyTst {
    private static String BASE_EXPECTED_URI = "http://" + PROXY_HOST + ":" + NON_EXISTENT_PORT + TEST_PATH;
    private static final String HTTP_200_OK = "HTTP/1.1 200 OK";

    @Before
    public void before() {
        super.before();
    }

    @After
    public void after() {
        proxyServer.stop();
    }


    @Test
    public void simpleGet() {
        request = new CustomHttpRequestImpl.Builder(PROXY_HOST, TEST_PATH)
                .port(NON_EXISTENT_PORT)
                .proxy(PROXY_INFO)
                .build();

        request.submit();

        assertThat(interceptedRequest.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(interceptedRequest.getUri()).contains(BASE_EXPECTED_URI);
        assertThat(interceptedRequest.getEntity()).isEmpty();
    }

    @Test
    public void getWithQueryParam() {
        Parameters queryParams = new Parameters();
        queryParams.add(TEST_KEYVALUEPAIR);
        String encodedQueryParam = Utils.encodeQueryString(queryParams);
        String expectedUri = BASE_EXPECTED_URI + "?"
                + encodedQueryParam;

        request = new CustomHttpRequestImpl.Builder(PROXY_HOST, TEST_PATH)
                .proxy(PROXY_INFO)
                .port(NON_EXISTENT_PORT)
                .method(Method.GET)
                .queryParams(queryParams)
                .build();

        request.submit();

        assertThat(interceptedRequest.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(interceptedRequest.getUri()).contains(expectedUri);
        assertThat(interceptedRequest.getEntity()).isEmpty();
    }

    @Test
    public void getTrustedSsl() {
        String trustedSslHost = "accounts.google.com";
        String expectedUri = trustedSslHost + ":" + SSL_PORT;
        String expectedTitle = "<title>Sign in - Google Accounts</title>";

        request = new CustomHttpRequestImpl.Builder(trustedSslHost, "/")
                .proxy(PROXY_INFO)
                .protocol(Protocol.HTTPS)
                .port(SSL_PORT)
                .build();

        CustomHttpResponse response = request.submit();

        LOG.info("\ninterceptedRequest: " + interceptedRequest);

        assertThat(interceptedRequest.getMethod()).isEqualTo(HttpMethod.CONNECT);
        assertThat(interceptedRequest.getUri()).contains(expectedUri);
        assertThat(interceptedRequest.getEntity()).isEmpty();

        assertThat(response.getRaw()).startsWith(HTTP_200_OK);
        assertThat(response.getEntity()).contains(expectedTitle);
    }

    @Test
    public void getUntrustedSsl() {
        String unTrustedSslHost = "www.legaspi.co";
        String expectedUri = unTrustedSslHost + ":" + SSL_PORT;
        String expectedHttpStatus = "HTTPS/1.1 500";

        request = new CustomHttpRequestImpl.Builder(unTrustedSslHost, "/")
                .proxy(PROXY_INFO)
                .protocol(Protocol.HTTPS)
                .port(SSL_PORT)
                .build();

        CustomHttpResponse response = request.submit();

        LOG.info("\ninterceptedRequest: " + interceptedRequest);

        assertThat(interceptedRequest.getMethod()).isEqualTo(HttpMethod.CONNECT);
        assertThat(interceptedRequest.getUri()).contains(expectedUri);
        assertThat(interceptedRequest.getEntity()).isEmpty();

        assertThat(response.getRaw()).startsWith(expectedHttpStatus);
    }

    @Test
    public void getUntrustedSslWithHostnameCheckDisabled() {
        String unTrustedSslHost = "www.legaspi.co";
        String expectedUri = unTrustedSslHost + ":" + SSL_PORT;
        String expectedTitle = "<title>legaspi.co - Home</title>";

        request = new CustomHttpRequestImpl.Builder(unTrustedSslHost, "/")
                .proxy(PROXY_INFO)
                .protocol(Protocol.HTTPS)
                .disableSslHostnameCheck(true)
                .port(SSL_PORT)
                .build();

        CustomHttpResponse response = request.submit();

        LOG.info("\ninterceptedRequest: " + interceptedRequest);

        assertThat(interceptedRequest.getMethod()).isEqualTo(HttpMethod.CONNECT);
        assertThat(interceptedRequest.getUri()).contains(expectedUri);
        assertThat(interceptedRequest.getEntity()).isEmpty();

        assertThat(response.getRaw()).startsWith(HTTP_200_OK);
        assertThat(response.getEntity()).contains(expectedTitle);
    }
}
