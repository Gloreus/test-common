package ru.sovcombank.rbs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties("teststore")
@Component
@Data
public class TestStoreProperties {
    private String rootDir = Path.of("..", "TestStore").toString();
    private String profilesDir = "profiles";
    private String casesDir = "cases";
    private String oraTestsDir = "";

    public Path getProfilesFullPath() {
        return Paths.get(rootDir, profilesDir).toAbsolutePath().normalize();
    }

    public Path getCasesFullPath() {
        return Paths.get(rootDir, casesDir).toAbsolutePath().normalize();
    }
    public Path getOraTestsFullPath() {
        return Paths.get(rootDir, oraTestsDir).toAbsolutePath().normalize();
    }
}
