package co.legaspi.httptools;

/**
 * Customized HttpResponse that contains a snapshot of the HttpResponse
 * which can then be referenced more than once, as opposed to Apache's
 * HttpClient HttpResponse which can only be used once and then the
 * input stream is empty.
 *
 * @author vl
 */
public interface CustomHttpResponse {
    /**
     * @return String representation of the raw HttpResponse
     */
    String getRaw();

    /**
     * @return String representation of the entity body
     */
    String getEntity();
}
