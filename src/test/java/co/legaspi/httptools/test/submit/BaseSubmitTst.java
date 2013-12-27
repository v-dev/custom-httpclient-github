package co.legaspi.httptools.test.submit;

import co.legaspi.httptools.CustomHttpRequestImpl;
import co.legaspi.httptools.model.KeyValuePair;
import co.legaspi.httptools.model.Protocol;
import co.legaspi.httptools.model.ProxyInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base Integration Test class.
 *
 * @author vl
 */
public class BaseSubmitTst {
    protected static final Logger LOG = LogManager.getLogger();

    protected static final String PROXY_HOST = "localhost";
    protected static final String TEST_PATH = "/test";
    protected static final int PROXY_PORT = 7777;
    protected static final int NON_EXISTENT_PORT = 4321;
    protected static final int SSL_PORT = 443;

    protected static final KeyValuePair TEST_KEYVALUEPAIR = new KeyValuePair("thekey", "thevalue");
    protected static final ProxyInfo PROXY_INFO = new ProxyInfo(Protocol.HTTP, PROXY_HOST, PROXY_PORT);

    protected CustomHttpRequestImpl request;
}
