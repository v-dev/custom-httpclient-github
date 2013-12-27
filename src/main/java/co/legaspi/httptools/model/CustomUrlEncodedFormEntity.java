package co.legaspi.httptools.model;

import co.legaspi.httptools.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Just an empty wrapper around Apache's HTTP Client, UrlEncodedFormEntity
 */
public class CustomUrlEncodedFormEntity extends UrlEncodedFormEntity implements ICustomHttpEntity {

    private String content = "";

    public CustomUrlEncodedFormEntity(List<? extends NameValuePair> parameters,
                                      String encoding) throws UnsupportedEncodingException {
        super(parameters, encoding);
        content = Utils.encodeQueryString(parameters);
    }

    @Override
    public String getContentAsString() {
        return content;
    }

}
