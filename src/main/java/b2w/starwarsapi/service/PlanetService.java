package b2w.starwarsapi.service;

import b2w.starwarsapi.exception.InvalidPlanetNameException;
import b2w.starwarsapi.exception.PlanetDeletionException;
import b2w.starwarsapi.exception.PlanetRegistrationException;
import b2w.starwarsapi.exception.PlanetNotFoundException;
import b2w.starwarsapi.model.Planet;
import b2w.starwarsapi.repository.PlanetRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlanetService {

    public static final String INVALID_PLANET_NAME_MESSAGE = "O nome do planeta informado não é válido. Por favor insira um existente nos filmes.";
    public static final int NOT_VALID_INDEX = -1;

    private final PlanetRepository planetRepository;
    private final SwapiConsumerService swapiConsumerService;

    public String create(Planet planet) throws IOException {
        validateDuplicateEntry(planet);

        JsonObject response = consultSwapi(planet);
        int planetIndex = findValidPlanetNameIndex(planet, response);
        int filmsTimesAppeared = response.get("results").getAsJsonArray().get(planetIndex).getAsJsonObject().get("films").getAsJsonArray().size();
        planet.setFilmsTimesAppeared(filmsTimesAppeared);
        var savedPlanet = planetRepository.save(planet);

        return String.format("id: %s", savedPlanet.getId());
    }

    public List<Planet> find(String name) {
        if(Objects.isNull(name)) {
            return planetRepository.findAll();
        }
        return Collections.singletonList(findByName(name));
    }

    public Planet findById(String id) {
        return planetRepository.findById(id).
                orElseThrow(() -> new PlanetNotFoundException("Nenhum planeta com o Id informado foi encontrado."));
    }

    public Planet findByName(String name) {
        return planetRepository.findByName(name).
                orElseThrow(() -> new PlanetNotFoundException(String.format("Planeta %s não encontrado.", name)));
    }

    public void delete(String id) {
        var planet = planetRepository.findById(id).orElseThrow(() -> new PlanetDeletionException("Não foi possível excluir o planeta, planeta não encontrado."));
        planetRepository.delete(planet);
    }

    private void validateDuplicateEntry(Planet planet) {
        var isAlreadyRegistered = planetRepository.findByName(planet.getName()).isPresent();
        if (isAlreadyRegistered) {
            throw new PlanetRegistrationException(String.format("O planeta %s já foi cadastrado.", planet.getName()));
        }
    }

    private JsonObject consultSwapi(Planet planet) throws IOException {
        HttpGet httpGet = swapiConsumerService.buildPlanetRequest(planet.getName());
        return swapiConsumerService.request(httpGet);
    }

    private int findValidPlanetNameIndex(Planet planet, JsonObject response) {
        int planetsQuantityFoundByName = response.get("count").getAsInt();
        if (planetsQuantityFoundByName <= 0) {
            throw new InvalidPlanetNameException(INVALID_PLANET_NAME_MESSAGE);
        }
        else {
            boolean isValidName = false;
            int index = NOT_VALID_INDEX;
            for (int i = 0; i < planetsQuantityFoundByName; i++) {
                String name = response.get("results").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                if(Objects.equals(planet.getName(), name)) {
                    index = i;
                    isValidName = true;
                    break;
                }
            }
            if (!isValidName) {
                throw new InvalidPlanetNameException(INVALID_PLANET_NAME_MESSAGE);
            }
            return index;
        }
    }
}
