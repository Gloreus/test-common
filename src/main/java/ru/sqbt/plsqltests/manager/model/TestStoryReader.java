package ru.sqbt.plsqltests.manager.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
@Slf4j
public class TestStoryReader {
    private final ObjectMapper mapper;

    public TestStoryReader(@NonNull @Qualifier("YmlCaseMapper") ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public TestStories readYam(Path pathToYml) throws IOException {
        log.debug("Читаем {}", pathToYml);
        try (InputStream stream =  Files.newInputStream(pathToYml)) {
            return mapper.readValue(stream, TestStories.class);
        } catch (IOException e) {
            log.error("Не смог прочитать тесты из " + pathToYml.toString());
            throw new IOException("Не смог прочитать тесты из " + pathToYml.toString());
        }
    }
}
