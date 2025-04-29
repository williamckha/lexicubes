package com.lexicubes.backend;

import com.lexicubes.backend.score.Score;
import com.lexicubes.backend.puzzle.Puzzle;
import com.lexicubes.backend.user.User;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jdbc.core.mapping.schema.DefaultSqlTypeMapping;
import org.springframework.data.jdbc.core.mapping.schema.LiquibaseChangeSetWriter;
import org.springframework.data.jdbc.core.mapping.schema.SqlTypeMapping;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("dev")
public class SchemaGenerationTest {

    @Autowired
    private RelationalMappingContext context;

    @Autowired
    private DataSource dataSource;

    @Test
    public void generateSchemaChangeLog() throws IOException {
        final Path changeLogFilePath = getChangeLogFilePath();
        Files.deleteIfExists(changeLogFilePath);

        context.setInitialEntitySet(Set.of(Puzzle.class, User.class, Score.class));
        final LiquibaseChangeSetWriter writer = new LiquibaseChangeSetWriter(context);

        writer.setSqlTypeMapping(((SqlTypeMapping) property -> {
            if (property.getType().equals(Puzzle.Cubes.class)) {
                return "JSON";
            }
            return null;
        }).and(new DefaultSqlTypeMapping()));

        try (Database database = new MySQLDatabase()) {
            database.setConnection(new JdbcConnection(dataSource.getConnection()));
            writer.writeChangeSet(new FileSystemResource(changeLogFilePath), database);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.enabled", () -> false);
    }

    private Path getChangeLogFilePath() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-A");
        final String fileName = formatter.format(LocalDateTime.now()) + "_changelog.yaml";
        return Paths.get("src", "main", "resources", "db", "changelog", fileName);
    }
}
