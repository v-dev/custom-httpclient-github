package co.legaspi.httptools;

/**
 * Custom HTTP Request wrapper API around Apache HTTP Client.
 *
 * @author vl
 */
public interface CustomHttpRequest {
    CustomHttpResponse submit();
}
