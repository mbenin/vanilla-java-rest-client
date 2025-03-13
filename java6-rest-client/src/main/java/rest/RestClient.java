package rest;

import java.io.IOException;
import java.util.Map;

public interface RestClient {

    public String get(String endpoint, Map<String, String> headers) throws IOException;
    public String post(String endpoint, String body, Map<String, String> headers);
    public String put(String endpoint, String body, Map<String, String> headers);
    public String delete(String endpoint, Map<String, String> headers);

}
