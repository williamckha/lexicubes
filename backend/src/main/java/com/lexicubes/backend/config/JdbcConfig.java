package com.lexicubes.backend.config;

import com.lexicubes.backend.puzzle.converters.JsonToPuzzleCubesConverter;
import com.lexicubes.backend.puzzle.converters.PuzzleCubesToJsonConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    private final JsonToPuzzleCubesConverter jsonToPuzzleCubesConverter;
    private final PuzzleCubesToJsonConverter puzzleCubesToJsonConverter;

    public JdbcConfig(JsonToPuzzleCubesConverter jsonToPuzzleCubesConverter,
                      PuzzleCubesToJsonConverter puzzleCubesToJsonConverter) {
        this.jsonToPuzzleCubesConverter = jsonToPuzzleCubesConverter;
        this.puzzleCubesToJsonConverter = puzzleCubesToJsonConverter;
    }

    @Override
    protected @NotNull List<?> userConverters() {
        return Arrays.asList(jsonToPuzzleCubesConverter, puzzleCubesToJsonConverter);
    }
}
