package co.legaspi.httptools.test;

import co.legaspi.httptools.CustomHttpResponse;
import co.legaspi.httptools.CustomHttpResponseImpl;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author vl
 */
public class CustomResponseImplTest {
    private static final Logger LOG = LogManager.getLogger();

    @Test
    public void noContentHttpResponseCreatesNoContentCustomResponse() {
        StatusLine stat = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "No Content");
        HttpResponse resp = new BasicHttpResponse(stat);
        CustomHttpResponse response = new CustomHttpResponseImpl(resp);

        String rawResponse = response.getRaw();
        LOG.info("response.getRaw() = " + rawResponse);
        assertThat(rawResponse).isEqualTo("HTTP/1.1 204 No Content");
    }

    @Test
    public void notFoundHttpResponseCreatesNotFoundCustomResponse() {
        StatusLine stat = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NOT_FOUND, "Not Found");
        HttpResponse resp = new BasicHttpResponse(stat);
        CustomHttpResponse response = new CustomHttpResponseImpl(resp);

        String rawResponse = response.getRaw();
        LOG.info("response.getRaw() = " + rawResponse);
        assertThat(rawResponse).isEqualTo("HTTP/1.1 404 Not Found");
    }
}
