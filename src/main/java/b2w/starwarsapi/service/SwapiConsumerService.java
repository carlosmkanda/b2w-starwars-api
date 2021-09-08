package b2w.starwarsapi.service;

import b2w.starwarsapi.exception.SwapiResponseException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class SwapiConsumerService {

    private static final String URI = "https://swapi.dev/api/planets/?search=";
    private static final String BLANK_SPACE_ENCODING = "%20";

    public HttpGet buildPlanetRequest(String planetName) {
        return new HttpGet(URI + correctBlankSpaceEncoding(planetName));
    }

    private String correctBlankSpaceEncoding(String planetName) {
        return planetName.replace(" ", BLANK_SPACE_ENCODING);
    }

    public JsonObject request(HttpGet getRequest) throws IOException {

        var httpClient = HttpClientBuilder.create().build();
        getRequest.addHeader("accept", "application/json");
        HttpResponse response = httpClient.execute(getRequest);

        validateResponse(response);

        var inputStreamReader = new InputStreamReader((response.getEntity().getContent()));
        var bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        var stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        JsonObject jsonObject = deserialize(stringBuilder.toString());
        bufferedReader.close();

        return jsonObject;
    }

    private void validateResponse(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new SwapiResponseException(String.format("Falha ao buscar planeta em SWAPI. HTTP error code : %s", response.getStatusLine().getStatusCode()));
        }
    }

    private JsonObject deserialize(String json) {
        var gson = new Gson();
        return gson.fromJson(json, JsonObject.class);
    }
}
