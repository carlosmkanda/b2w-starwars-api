package b2w.starwarsapi.service;

import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpGet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class SwapiConsumerServiceTest {

    private static final String TATOOINE = "Tatooine";

    @Test
    void shouldReturnValidValuesForTatooinePlanet() throws IOException {
        var swapiConsumerService = new SwapiConsumerService();
        HttpGet httpGet = swapiConsumerService.buildPlanetRequest(TATOOINE);
        JsonObject response = swapiConsumerService.request(httpGet);

        Assertions.assertThat(response.get("results").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString()).isEqualTo(TATOOINE);
        Assertions.assertThat(response.get("results").getAsJsonArray().get(0).getAsJsonObject().get("films").getAsJsonArray().size()).isEqualTo(5);
    }
}