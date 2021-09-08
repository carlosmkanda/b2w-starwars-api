package config;

import com.sun.istack.NotNull;
import org.testcontainers.containers.GenericContainer;

public class MongoDbContainer extends GenericContainer<MongoDbContainer> {

    public static final int MONGODB_PORT = 27207;
    public static final String DEFAULT_IMAGE_AND_TAG = "mongo:3.2.4";

    public MongoDbContainer() {
        this(DEFAULT_IMAGE_AND_TAG);
    }
    public MongoDbContainer(@NotNull String image) {
        super(image);
        addExposedPort(MONGODB_PORT);
    }
    @NotNull
    public Integer getPort() {
        return getMappedPort(MONGODB_PORT);
    }
}