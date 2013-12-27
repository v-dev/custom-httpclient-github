package co.legaspi.httptools.test.submit;

import co.legaspi.httptools.CustomHttpRequestImpl;
import co.legaspi.httptools.CustomHttpResponse;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;
import net.lightbody.bmp.proxy.http.BrowserMobHttpRequest;
import net.lightbody.bmp.proxy.http.RequestInterceptor;
import net.lightbody.bmp.proxy.jetty.http.HttpRequest;
import net.lightbody.bmp.proxy.jetty.util.MultiMap;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Integration Tests of the CustomHttpRequestImpl class' submit method using
 * BrowserMobProxy's interceptors.
 *
 * @author v
 */
public class CustomHttpRequestImplBMPIT extends BaseSubmitTst {
    private static ProxyServer proxyServer;

    private HttpRequest proxiedRequest;


    @BeforeClass
    public static void beforeClass() throws Exception {
        proxyServer = new ProxyServer(PROXY_PORT);
        proxyServer.start();

        PROXY_INFO.enableProxy();
    }

    @Before
    public void before() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void process(BrowserMobHttpRequest request, Har har) {
                proxiedRequest = request.getProxyRequest();
            }
        };
        proxyServer.addRequestInterceptor(requestInterceptor);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        proxyServer.stop();
    }


    @Test
    public void getWithQueryParam() {
        request = new CustomHttpRequestImpl.Builder(PROXY_HOST, TEST_PATH)
                .proxy(PROXY_INFO)
                .addQueryParam(TEST_KEYVALUEPAIR.getName(), TEST_KEYVALUEPAIR.getValue())
                .build();

        CustomHttpResponse response = request.submit();
        String res = response.getRaw();

        //LOG.info("\nres: " + res);

        MultiMap actualParams = proxiedRequest.getParameters();
        LOG.info("proxied actual params: " + actualParams);
        assertThat(actualParams.getString(TEST_KEYVALUEPAIR.getName())).isEqualTo(TEST_KEYVALUEPAIR.getValue());
    }

}
