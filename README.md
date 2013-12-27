CustomHttpClient

Wrapper around Apache httpclient using Builder pattern.

Usage

````
CustomHttpRequest req = new CustomHttpRequestImpl.Builder(HOST, GET_PATH).
                                  addHeader("x-a-phone-number", MSISDN).
                                  proxy(proxy).
                                  build();

CustomHttpResponse response = req.submit();
String rawRes = response.getRaw();

LOG.info("\nres: " + rawRes);
````
