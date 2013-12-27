# CustomHttpClient #

Wrapper around Apache httpclient using Builder pattern.

## Usage ##

````
private static final ProxyInfo PROXY_INFO = new ProxyInfo(Protocol.HTTP, PROXY_HOST, PROXY_PORT);

CustomHttpRequest req = new CustomHttpRequestImpl.Builder(HOST, GET_PATH)
    .addHeader("x-custom-header", "some_value")
    .proxy(PROXY_INFO)
    .build();

CustomHttpResponse response = req.submit();
String rawRes = response.getRaw();

LOG.info("\nres: " + rawRes);
````
