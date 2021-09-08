package b2w.starwarsapi.service;

import b2w.starwarsapi.exception.PlanetDeletionException;
import b2w.starwarsapi.exception.PlanetNotFoundException;
import b2w.starwarsapi.exception.PlanetRegistrationException;
import b2w.starwarsapi.model.Planet;
import b2w.starwarsapi.repository.PlanetRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {

    public static final String ANY_STRING = "anyString";
    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    @Mock
    private SwapiConsumerService swapiConsumerService;

    private void mockPlanets() {
    }

    @Test
    void shouldCallFindAllPlanetsWhenNameParameterIsNull() {
        planetService.find(null);

        verify(planetRepository).findAll();
        verify(planetRepository, times(0)).findByName(ANY_STRING);
    }

    @Test
    void shouldCallFindByNameWhenNameParameterIsNotNull() {
        var planet = new Planet();
        planet.setName(ANY_STRING);
        when(planetRepository.findByName(ANY_STRING)).thenReturn(Optional.of(planet));
        planetService.find(ANY_STRING);

        verify(planetRepository, times(0)).findAll();
        verify(planetRepository).findByName(ANY_STRING);
    }

    @Test
    void shouldThrowPlanetNotFoundExceptionWhenPlanetIdNotFound() {
        when(planetRepository.findById(ANY_STRING)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
            planetService.findById(ANY_STRING))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessage("Nenhum planeta com o Id informado foi encontrado.");
    }

    @Test
    void shouldThrowPlanetNotFoundExceptionWhenPlanetNameNotFound() {
        when(planetRepository.findByName(ANY_STRING)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() ->
                        planetService.findByName(ANY_STRING))
                .isInstanceOf(PlanetNotFoundException.class)
                .hasMessage(String.format("Planeta %s não encontrado.", ANY_STRING));
    }

    @Test
    void shouldCallDeleteWhenIdIsNotNull() {
        var planet = new Planet();
        when(planetRepository.findById(ANY_STRING)).thenReturn(Optional.of(planet));

        planetService.delete(ANY_STRING);

        verify(planetRepository).findById(ANY_STRING);
        verify(planetRepository).delete(planet);
    }

    @Test
    void shouldThrowPlanetDeletionExceptionWhenPlanetIdNotFound() {
        when(planetRepository.findById(ANY_STRING)).thenReturn(Optional.empty());

        verify(planetRepository, times(0)).delete(any(Planet.class));

        Assertions.assertThatThrownBy(() ->
                        planetService.delete(ANY_STRING))
                .isInstanceOf(PlanetDeletionException.class)
                .hasMessage("Não foi possível excluir o planeta, planeta não encontrado.");
    }

    @Test
    void shouldThrowPlanetRegistrationExceptionWhenNameIsAlreadyRegistered() {
        var planet = new Planet();
        planet.setName(ANY_STRING);

        when(planetRepository.findByName(ANY_STRING)).thenReturn(Optional.of(planet));

        Assertions.assertThatThrownBy(() ->
                        planetService.create(planet))
                .isInstanceOf(PlanetRegistrationException.class)
                .hasMessage(String.format("O planeta %s já foi cadastrado.", planet.getName()));
    }

}