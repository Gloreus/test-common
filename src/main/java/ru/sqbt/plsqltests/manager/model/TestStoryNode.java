package ru.sqbt.plsqltests.manager.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sovcombank.rbs.caseentity.TestCase;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class TestStoryNode {
    /// Файл с набором тестов
    @NonNull
    @Getter
    private final Path path;
    @Getter
    private Optional<TestStories> testStories;

    public TestStoryNode(@NonNull Path filePath, TestStories testStories) {
        this.path = filePath;
        this.testStories = Optional.ofNullable(testStories);
    }

    public TestStoryNode(@NonNull Path dirPath) {
        this.path = dirPath;
        this.testStories = Optional.empty();
    }

    public String getName() {
      return path.getFileName().toString();
    }

    public String getCaption() {
        return testStories.map(TestStories::getCaption).orElse("");
    }

    public String getComment() {
        return testStories.map(TestStories::getComment).orElse("");
    }
}
