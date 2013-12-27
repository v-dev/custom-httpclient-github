package co.legaspi.httptools.test;

import co.legaspi.httptools.model.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Mostly copy of DefaultHttpRequest bean but with option to provide the content/entity body.  It's *assumed* to be String.
 *
 * @author v-dev
 */
public class CopiedHttpRequest extends DefaultHttpRequest {
    private HttpHeaders headers = new DefaultHttpHeaders();
    private DecoderResult decoderResult = DecoderResult.SUCCESS;
    private ByteBuf content;
    private String entity;

    /**
     * Creates a new instance.
     *
     * @param httpVersion the HTTP version of the request
     * @param method      the HTTP getMethod of the request
     * @param uri         the URI or path of the request
     */
    public CopiedHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        super(httpVersion, method, uri);
    }

    /**
     * Make a copy of the provided DefaultHttpRequest.
     *
     * @param copyFrom
     */
    public CopiedHttpRequest(DefaultHttpRequest copyFrom) {
        super(copyFrom.getProtocolVersion(), copyFrom.getMethod(), copyFrom.getUri());
        this.headers = copyFrom.headers();
        this.decoderResult = getDecoderResult();
    }

    /**
     * "Empty" generic HTTP 1.1 GET
     */
    public CopiedHttpRequest() {
        super(new HttpVersion(HttpVersion.HTTP_1_1.text(), false), HttpMethod.GET, "/");
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public void setContent(ByteBuf content) {
        this.content = content;
    }

    /**
     * Get the entity body within this HTTP request message.
     *
     * @return String representation of the entity body.
     */
    public String getEntity() {
        String entity = "";
        if ( content != null ) {
            entity = new String(content.array());
        }

        return entity;
    }

    /**
     * Copied from DefaultHttpRequest
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append(", decodeResult: ");
        buf.append(getDecoderResult());
        buf.append(')');

        buf.append(StringUtil.NEWLINE);
        buf.append(getMessage());

        return buf.toString();
    }

    /**
     * Get the message within this Http Request.  The message is the request line + headers + entity body.
     *
     * @return String representation of the message.
     */
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        buf.append(getMethod().toString());
        buf.append(' ');
        buf.append(getUri());
        buf.append(' ');
        buf.append(getProtocolVersion().text());

        buf.append(StringUtil.NEWLINE);
        buf.append(getHeaders());

        String body = getEntity();
        if (StringUtils.isNotEmpty(body)) {
            buf.append(StringUtil.NEWLINE);
            buf.append(body);
        }

        return buf.toString();
    }

    /**
     * Copied/modified from DefaultHttpMessage
     */
    private String getHeaders() {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> e : headers) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
        return buf.toString();
    }
}
