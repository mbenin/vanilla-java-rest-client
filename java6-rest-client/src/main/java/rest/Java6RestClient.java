package rest;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Java6RestClient implements RestClient{

    String baseUrl;
    String username;
    String password;

    public Java6RestClient(String url, String username, String password) {
        this.baseUrl = url;
        this.password = password;
        this.username = username;
    }

    public String get(String endpoint, Map<String, String> headers) throws IOException {
        return sendRequest("GET",endpoint,null,headers);
    }

    public String post(String endpoint, String body, Map<String, String> headers) {
        return "";
    }

    public String put(String endpoint, String body, Map<String, String> headers) {
        return "";
    }

    public String delete(String endpoint, Map<String, String> headers) {
        return "";
    }

    private String sendRequest(String method, String endpoint, String body, Map<String, String> headers) throws IOException{
        URL url = new URL(baseUrl+endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String auth = username + ":" + password;
        String encodedAuth = DatatypeConverter.printBase64Binary(auth.getBytes("UTF-8"));

        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        if(headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (body != null && (method.equals("POST") || method.equals("PUT"))) {
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            byte[] input = body.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        InputStream inputStream;

        if(responseCode >= 200 && responseCode < 300){
            inputStream = connection.getInputStream();
        }else{
            inputStream = connection.getErrorStream();
        }

        String responseBody = getResponse(inputStream);
        connection.disconnect();

        return  responseBody;
    }

    private String getResponse(InputStream inputStream) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;

        while((line = buffer.readLine()) != null) {
            response.append(line);
        }

        buffer.close();
        return response.toString();
    }


    public static void main(String[] args) throws IOException {
        Java6RestClient restClient = new Java6RestClient("https://cat-fact.herokuapp.com/facts","admin","admin");
        String response = restClient.get("/facts",null);
        System.out.println(response);
    }
}
