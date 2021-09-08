package b2w.starwarsapi.controller;

import b2w.starwarsapi.model.Planet;
import b2w.starwarsapi.service.PlanetService;
import b2w.starwarsapi.service.UpdateFilmsAppearanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/planets")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlanetController {

    private final PlanetService planetService;
    private final UpdateFilmsAppearanceService updateFilmsAppearanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody Planet planet) throws IOException {
        return planetService.create(planet);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Planet> get(@RequestParam(value="name", required = false) String name) {
        return planetService.find(name);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Planet getById(@PathVariable("id") String id) {
        return planetService.findById(id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        planetService.delete(id);
    }

    @PatchMapping("/update/filmsAppearance")
    @ResponseStatus(HttpStatus.OK)
    public void updateAllPlanetsFilmsAppearance() {
        updateFilmsAppearanceService.execute();
    }
}
