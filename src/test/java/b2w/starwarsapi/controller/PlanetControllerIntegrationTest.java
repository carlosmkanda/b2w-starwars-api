package b2w.starwarsapi.controller;

import b2w.starwarsapi.repository.PlanetRepository;
import config.MongoDbContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PlanetControllerIntegrationTest.MongoDbInitializer.class)
class PlanetControllerIntegrationTest {

    private static MongoDbContainer mongoDbContainer;

    @LocalServerPort
    private int port;

    @Autowired
    private PlanetRepository planetRepository;


    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        mongoDbContainer = new MongoDbContainer();
        mongoDbContainer.start();
    }

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
    }

    @AfterEach
    void afterEach() {
        planetRepository.deleteAll();
    }

    @Disabled
    @Test
    void shouldCreatePlanetAndReturnHttpStatusCreated() {
        given()
                .body("{ \"name\": \"Tatooine\"," +
                        "\"climate\": \"Frio\"," +
                        "\"terrain\": \"Savana\"}")
                .when().post("/api/planets/")
                .then().assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    public static class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongoDbContainer.getContainerIpAddress(),
                    "spring.data.mongodb.port=" + mongoDbContainer.getPort()

            );
            values.applyTo(configurableApplicationContext);
        }
    }
}