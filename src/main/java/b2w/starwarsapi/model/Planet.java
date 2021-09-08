package b2w.starwarsapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "planets")
public class Planet {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String climate;
    private String terrain;
    private Integer filmsTimesAppeared;

    public Planet(String name, String climate, String terrain, Integer filmsTimesAppeared) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
        this.filmsTimesAppeared = filmsTimesAppeared;
    }
}



