package b2w.starwarsapi.controller;

import b2w.starwarsapi.model.Planet;
import b2w.starwarsapi.repository.PlanetRepository;
import com.sun.istack.NotNull;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PlanetControllerIntegrationTest.Initializer.class)
class PlanetControllerIntegrationTest {


    private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:4.2.8");

    private static final String URI = "/api/planets/";
    private static final String TATOOINE = "Tatooine";
    private static final String UPDATE_FILMS_APPEARANCE_URI = "/api/planets/update/filmsAppearance/";
    private static final String CREATE_PAYLOAD_BODY = "{ \"name\": \"Tatooine\"," +
            "\"climate\": \"Frio\"," +
            "\"terrain\": \"Savana\"}";
    private static final String INVALID_CREATE_PAYLOAD = "{ \"name\": \"Tatoo\"," +
            "\"climate\": \"Frio\"," +
            "\"terrain\": \"Savana\"}";
    private static final String ANOTHER_PAYLOAD_BODY = "{ \"name\": \"Alderaan\"," +
            "\"climate\": \"Frio\"," +
            "\"terrain\": \"Savana\"}";

    @LocalServerPort
    private int port;

    @Autowired
    private PlanetRepository planetRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void afterEach() {
        planetRepository.deleteAll();
    }

    @BeforeAll
    static void setUpAll() {
        MONGO_DB_CONTAINER.start();
    }

    @AfterAll
    static void tearDownAll() {
        if (!MONGO_DB_CONTAINER.isShouldBeReused()) {
            MONGO_DB_CONTAINER.stop();
        }
    }

    @Test
    void shouldCreatePlanetAndReturnHttpStatusCreated() {
        requestPostCreatePlanet(CREATE_PAYLOAD_BODY);

        Planet planet = planetRepository.findByName(TATOOINE).get();
        assertThat(planet.getName()).isEqualTo(TATOOINE);
        assertThat(planet.getClimate()).isEqualTo("Frio");
        assertThat(planet.getTerrain()).isEqualTo("Savana");
        assertThat(planet.getFilmsTimesAppeared()).isEqualTo(5);
        assertThat(planet.getId()).isNotBlank();
    }

    @Test
    void shouldNotRegisterDuplicatePlanetAndReturnHttpStatusConflicted() {
        requestPostCreatePlanet(CREATE_PAYLOAD_BODY);

        given()
                .contentType(ContentType.JSON)
                .body(CREATE_PAYLOAD_BODY)
                .when().post(URI)
                .then().assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(equalTo("O planeta " + TATOOINE + " já foi cadastrado."));

        List<Planet> planets = planetRepository.findAll();
        assertThat(planets.size()).isEqualTo(1);
    }

    @Test
    void shouldNotRegisterInvalidNamePlanetAndReturnHttpStatusBadRequest() {
        given()
                .contentType(ContentType.JSON)
                .body(INVALID_CREATE_PAYLOAD)
                .when().post(URI)
                .then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("O nome do planeta informado não é válido. Por favor insira um existente nos filmes."));
    }

    @Test
    void shouldUpdateFilmsTimesAppearedForAllCollectionFromSwapiAndReturnHttpStatusOk() {
        requestPostCreatePlanet(CREATE_PAYLOAD_BODY);

        var planet = planetRepository.findByName(TATOOINE).get();
        assertThat(planet.getFilmsTimesAppeared()).isEqualTo(5);

        planet.setFilmsTimesAppeared(1);
        planetRepository.save(planet);
        assertThat(planet.getFilmsTimesAppeared()).isEqualTo(1);

        when()
                .patch(UPDATE_FILMS_APPEARANCE_URI)
                .then().assertThat()
                .statusCode(HttpStatus.OK.value());

        planet = planetRepository.findByName(TATOOINE).get();
        assertThat(planet.getFilmsTimesAppeared()).isEqualTo(5);
    }

    @Test
    void shouldReturnAllRegisteredPlanetsAndHttpStatusOk() {
        requestPostCreatePlanet(CREATE_PAYLOAD_BODY);
        requestPostCreatePlanet(ANOTHER_PAYLOAD_BODY);

        given()
                .contentType(ContentType.JSON)
                .when().get(URI)
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(containsString(TATOOINE))
                .body(containsString("Alderaan"));
    }

    @Test
    void shouldReturnAllRegisteredPlanetsGivenNameParameterAndHttpStatusOk() {
        requestPostCreatePlanet(CREATE_PAYLOAD_BODY);
        requestPostCreatePlanet(ANOTHER_PAYLOAD_BODY);

        given()
                .contentType(ContentType.JSON)
                .queryParam("name", TATOOINE)
                .when().get(URI)
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(containsString(TATOOINE))
                .body(not(containsString("Alderaan")));
    }

    @Test
    void shouldReturnHttpStatusOkWhenThereIsNoRegisteredPlanetAndNoNameParameterIsInformed() {
        given()
                .contentType(ContentType.JSON)
                .when().get(URI)
                .then().assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnHttpStatusNoContentWhenThereIsNoRegisteredPlanetAndNameParameterIsInformed() {
        given()
                .contentType(ContentType.JSON)
                .queryParam("name", TATOOINE)
                .when().get(URI)
                .then().assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnHttpStatusOkWhenRegisteredPlanetIsFoundById() {
        requestPostCreatePlanet(CREATE_PAYLOAD_BODY);

        var planet = planetRepository.findByName(TATOOINE).get();
        String id = planet.getId();

        given()
                .contentType(ContentType.JSON)
                .when().get(URI + id)
                .then().assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(containsString(TATOOINE));
    }

    @Test
    void shouldReturnHttpStatusNoContentWhenNoRegisteredPlanetIsFoundById() {
        given()
                .contentType(ContentType.JSON)
                .when().get(URI + "anyId")
                .then().assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnHttpStatusNoContentWhenSpecifiedPlanetIsDeleted() {
        requestPostCreatePlanet(CREATE_PAYLOAD_BODY);

        var planet = planetRepository.findByName(TATOOINE).get();
        String id = planet.getId();

        given()
                .contentType(ContentType.JSON)
                .when().delete(URI + id)
                .then().assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnHttpStatusNotFoundWhenNoPlanetIsFoundToBeDeleted() {
        given()
                .when().delete(URI + "anyId")
                .then().assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void requestPostCreatePlanet(String createPayload) {
        given()
                .contentType(ContentType.JSON)
                .body(createPayload)
                .when().post(URI)
                .then().assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    String.format("spring.data.mongodb.uri: %s", MONGO_DB_CONTAINER.getReplicaSetUrl())
            ).applyTo(configurableApplicationContext);
        }
    }

}