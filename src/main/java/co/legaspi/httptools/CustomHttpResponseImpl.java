package co.legaspi.httptools;


import co.legaspi.httptools.model.Headers;
import org.apache.http.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;

/**
 * Concrete implementation of {@link CustomHttpResponse}.
 *
 * @author vl
 */
public class CustomHttpResponseImpl implements CustomHttpResponse {
    private HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
    private Headers headers = new Headers();
    private Headers setCookies = new Headers();
    private String rawContent = "";
    private String entity = "";
    private String rawRequest = "";

    public CustomHttpResponseImpl(HttpResponse response) {
        this.response = response;
        initHttpEmptyEntityIfNull();
        extractHeadersAndCookies();
        convertContentToString();
        extractEntityFromRawContent();
    }

    public Headers getHeaders() {
        return headers;
    }

    public Headers getSetCookies() {
        return setCookies;
    }

    @Override
    public String getRaw() {
        return rawContent;
    }

    @Override
    public String getEntity() {
        return entity;
    }

    /**
     * Get the raw request that produced this response.  *NOTE: Not programmatically provided.
     * Must be supplied by the user of the API after the fact via {@link setRawRequest}
     *
     * @return
     */
    public String getRawRequest() {
        return rawRequest;
    }

    ;

    /**
     * Provide the raw request which produced this response.  *NOTE: Not programmatically provided.
     * Must be supplied by the user of the API after the fact.  See {@link getRawRequest}
     *
     * @param rawRequest
     */
    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }


    private void initHttpEmptyEntityIfNull() {
        if (response.getEntity() == null) {
            HttpEntity ent = new ByteArrayEntity("".getBytes());
            response.setEntity(ent);
        }
    }

    private void extractHeadersAndCookies() {
        Header[] heads = response.getAllHeaders();
        for (Header head : heads) {
            headers.add(new co.legaspi.httptools.model.Header(head.getName(), head.getValue()));
            extractSetCookiesFromHeader(head);
        }
    }

    private void extractSetCookiesFromHeader(Header header) {
        if (header.getName().equals("Set-Cookie")) {
            setCookies.add(new co.legaspi.httptools.model.Header("Cookie", header.getValue()));
        }
    }

    private void convertContentToString() {
        rawContent = Utils.parseHttpResponse(response);
    }

    private void extractEntityFromRawContent() {
        int blankLineLocation = 0;
        if (!rawContent.isEmpty()) {
            blankLineLocation = rawContent.indexOf("\n\n");
        }
        entity = rawContent.substring(blankLineLocation + 1);
    }


}
