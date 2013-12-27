package co.legaspi.httptools.test.submit;

import co.legaspi.httptools.test.CopiedHttpRequest;
import io.netty.handler.codec.http.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

/**
 * @author vl
 */
public class BaseLittleProxyTst extends BaseSubmitTst {
    protected HttpProxyServer proxyServer;
    protected CopiedHttpRequest interceptedRequest;
    protected HttpFiltersSourceAdapter spyHttpFiltersSource;

    protected void before() {
        interceptedRequest = new CopiedHttpRequest();
        setupProxyServer();
    }

    private void setupProxyServer() {
        setupSpyFilterSource();

        proxyServer = DefaultHttpProxyServer.bootstrap()
                .withPort(PROXY_PORT)
                .withFiltersSource(spyHttpFiltersSource)
                .start();

        PROXY_INFO.enableProxy();
    }

    private void setupSpyFilterSource() {
        spyHttpFiltersSource = new HttpFiltersSourceAdapter() {
            public HttpFilters filterRequest(HttpRequest originalRequest) {
                return setupSpyFiltersAdapter(originalRequest);
            }
        };
    }

    private HttpFiltersAdapter setupSpyFiltersAdapter(final HttpRequest originalRequest) {
        return new HttpFiltersAdapter(originalRequest) {
            @Override
            public HttpResponse requestPre(HttpObject httpObject) {
                if (httpObject instanceof DefaultHttpRequest) {
                    interceptedRequest = new CopiedHttpRequest((DefaultHttpRequest) httpObject);
                }

                if (httpObject instanceof LastHttpContent) {
                    LastHttpContent lastHttpContent = ((LastHttpContent) httpObject).copy();
                    interceptedRequest.setContent(lastHttpContent.content());
                }

                return null;
            }
        };
    }
}
