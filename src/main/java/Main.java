import rest.client.api.MyRestClient;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
      String url = "https://dummyjson.com";
      String username = "emilys";
      String password = "emilyspass";

      MyRestClient restClient = new MyRestClient(url, username, password);

      String response = restClient.post("/auth/login", "{\"username\":\"emilys\",\"password\":\"emilyspass\",\"expiresInMins\":30}", null);
      System.out.println(response);

    }

}
