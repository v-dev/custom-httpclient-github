package co.legaspi.httptools;

import co.legaspi.httptools.model.Pair;
import co.legaspi.httptools.model.Parameters;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility methods mostly for helping with Http responses.
 *
 * @author tsunami
 */
public final class Utils {

    private static final Logger LOG = LogManager.getLogger();

    private Utils() {
        // ISSUE-4, Utility class shouldn't have public/default constructor.
    }

    /**
     * Generate a random unique ID
     *
     * @return
     */
    public static String generateUid() {
        return UUID.randomUUID().toString();
    }

    public static String encodeQueryString(Parameters queryParams) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Pair entry : queryParams.getAll()) {
            nvps.add(new BasicNameValuePair(entry.getName(), entry.getValue()));
        }
        return URLEncodedUtils.format(nvps, "utf-8");
    }

    public static String encodeQueryString(List<? extends NameValuePair> queryParams) {
        return URLEncodedUtils.format(queryParams, "utf-8");
    }


    /**
     * Parse an Apache Http Client response into a String
     *
     * @param resp
     * @return
     */
    public static String parseHttpResponse(HttpResponse resp) {
        long entityContentLength = resp.getEntity().getContentLength();

        StringBuilder sb = new StringBuilder();
        sb.append(resp.getStatusLine());
        if (entityContentLength != 0) {
            sb.append("\n");
            sb.append(buildHeadersFromResponse(resp));
            sb.append("\n");
            sb.append(buildEntityFromResponse(resp));
        }
        return sb.toString();
    }

    private static String buildHeadersFromResponse(HttpResponse resp) {
        StringBuilder headerBuilder = new StringBuilder();
        for (Header h : resp.getAllHeaders()) {
            headerBuilder.append(h.getName());
            headerBuilder.append(": ");
            headerBuilder.append(h.getValue());
            headerBuilder.append("\n");
        }
        return headerBuilder.toString();
    }

    private static String buildEntityFromResponse(HttpResponse resp) {
        StringBuilder entityBuilder = new StringBuilder();
        try {
            entityBuilder.append(EntityUtils.toString(resp.getEntity()));
        } catch (ParseException e) {
            LOG.error("ParseException occurred: ", e);
        } catch (IOException e) {
            LOG.error("IOException occurred: " , e);
        }
        return entityBuilder.toString();
    }

}
