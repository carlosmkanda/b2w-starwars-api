package b2w.starwarsapi.repository;

import b2w.starwarsapi.model.Planet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlanetRepository extends MongoRepository<Planet, String> {

    Optional<Planet> findByName(String name);
}
