package b2w.starwarsapi.service;

import b2w.starwarsapi.exception.FilmsAppearanceUpdateException;
import b2w.starwarsapi.model.Planet;
import b2w.starwarsapi.repository.PlanetRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateFilmsAppearanceService {

    private final SwapiConsumerService swapiConsumerService;
    private final PlanetRepository planetRepository;

    public void execute() {
        List<Planet> planets = planetRepository.findAll();
        planets.stream().forEach(this::update);
    }

    private void update(Planet planet) {
        try {
            HttpGet httpGet = swapiConsumerService.buildPlanetRequest(planet.getName());
            JsonObject response = swapiConsumerService.request(httpGet);
            int filmsTimesAppeared = response.get("results").getAsJsonArray().get(0).getAsJsonObject().get("films").getAsJsonArray().size();
            planet.setFilmsTimesAppeared(filmsTimesAppeared);
            planetRepository.save(planet);
        } catch (IOException e) {
            throw new FilmsAppearanceUpdateException("Erro ao atualizar as aparições dos planetas em filmes.");
        }
    }
}
