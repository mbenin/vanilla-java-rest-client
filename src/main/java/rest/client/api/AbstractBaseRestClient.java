package rest.client.api;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public abstract class AbstractBaseRestClient implements RestClient {

    protected String baseUrl;
    protected String username;
    protected String password;

    public AbstractBaseRestClient(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public String get(String endpoint, Map<String, String> headers) throws IOException {
        return sendRequest("GET", endpoint, null, headers);
    }

    @Override
    public String post(String endpoint, String body, Map<String, String> headers) throws IOException {
        return sendRequest("POST", endpoint, body, headers);
    }

    @Override
    public String put(String endpoint, String body, Map<String, String> headers) throws IOException {
        return sendRequest("PUT", endpoint, body, headers);
    }

    @Override
    public String delete(String endpoint, Map<String, String> headers) throws IOException {
        return sendRequest("PUT", endpoint, null, headers);
    }

    private String sendRequest(String method, String endpoint, String body, Map<String, String> headers) {
        try {
            String endpointUrl = baseUrl + endpoint;
            URL url = new URL(endpointUrl);
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            String auth = username + ":" + password;
            String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes("UTF-8"));

            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if(headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (body != null && (method.equals("POST") || method.equals("PUT"))) {
                connection.setDoOutput(true);
                outputStream = connection.getOutputStream();
                byte[] input = body.getBytes("UTF-8");
                outputStream.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            inputStream = (responseCode >= 200 && responseCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            return getResponse(inputStream);


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    private String getResponse(InputStream inputStream) throws IOException {
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = buffer.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                   //Log errors
                }
            }
        }
    }
}

