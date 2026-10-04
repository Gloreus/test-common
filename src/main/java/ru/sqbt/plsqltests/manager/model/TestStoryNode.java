package ru.sqbt.plsqltests.manager.model;

import lombok.Getter;
import lombok.NonNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

///  Узел дерева тестов
public class TestStoryNode {
    @Getter
    private final boolean isFolder;
    /// Файл с набором тестов
    @NonNull
    @Getter
    /// Путь к файлу с тестами
    private final Path path;
    @Getter
    private Optional<TestStories> testStories;
    @Getter
    /// Вложенные узлы
    private final List<TestStoryNode> children = new ArrayList<>();

    public void addChild(TestStoryNode child) {
        children.add(child);
    }

    public TestStoryNode(@NonNull Path filePath, TestStories testStories) {
        this.isFolder = false;
        this.path = filePath;
        this.testStories = Optional.ofNullable(testStories);
    }

    public TestStoryNode(@NonNull Path dirPath) {
        this.isFolder = true;
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
