package co.legaspi.httptools.model;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Test for the KeyValuePair class.
 *
 * @author vl
 */
public class KeyValuePairTest {

    private KeyValuePair pair;
    private String value = "value";

    @Test
    public void testSetName() {

        String firstName = "first";
        String secondName = "second";

        pair = new KeyValuePair(firstName, value);
        assertThat(pair.getName()).isEqualTo(firstName);

        // execute test
        pair.setName(secondName);
        assertThat(pair.getName()).isEqualTo(secondName);
    }

    @Test
    public void setName_emptyKeyDefaultsToKey() {
        String empty = "";
        String expected = "key";

        // execute test
        pair = new KeyValuePair(empty, value);

        // validate
        assertThat(pair.getName()).isEqualTo(expected);
    }

    @Test
    public void setName_nullKeyDefaultsToKey() {
        String expected = "key";

        // execute test
        pair = new KeyValuePair(null, value);

        assertThat(pair.getName()).isEqualTo(expected);
    }

    @Test
    public void testToString() {
        String expected = "KeyValuePair [key=key, value=" + value + "]";
        String defaultKey = "";

        pair = new KeyValuePair(defaultKey, value);

        assertThat(pair.toString()).isEqualTo(expected);
    }

    @Test
    public void testGetValue() {
        String defaultKey = "";

        pair = new KeyValuePair(defaultKey, value);

        assertThat(pair.getValue()).isEqualTo(value);
    }
}
