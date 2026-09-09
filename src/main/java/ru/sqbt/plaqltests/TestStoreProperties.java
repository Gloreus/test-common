package ru.sqbt.plaqltests;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties("teststore")
@Component
@Data
public class TestStoreProperties {
    private String rootPath = "TestRootDir";
    private String profilesPath = "profiles";
    private String casesPath = "cases";

    public Path getProfilesPath() {
        return Paths.get(rootPath, profilesPath).toAbsolutePath().normalize();
    }
}
