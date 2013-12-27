package co.legaspi.httptools.model;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

/**
 * Just a wrapper around Apache's HTTP Client, StringEntity
 */
public class CustomStringEntity extends StringEntity implements ICustomHttpEntity {

    private String content = "";

    public CustomStringEntity(String string) throws UnsupportedEncodingException {
        super(string);
        content = string;
    }

    @Override
    public String getContentAsString() {
        return content;
    }

}
