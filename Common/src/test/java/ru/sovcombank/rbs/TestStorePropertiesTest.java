package ru.sovcombank.rbs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
@Disabled
class TestStorePropertiesTest {

    @Test
    @DisplayName("Дефолтные значения полей корректны")
    void defaultValuesAreSet() {
        TestStoreProperties props = new TestStoreProperties();

        assertEquals(Path.of("..", "TestStore").toString(), props.getRootDir());
        assertEquals("profiles", props.getProfilesDir());
        assertEquals("cases", props.getCasesDir());
    }

    @Test
    @DisplayName("getProfilesDir возвращает абсолютный путь")
    void profilesDirIsAbsolute() {
        TestStoreProperties props = new TestStoreProperties();

        Path result = props.getProfilesFullPath();

        assertTrue(result.isAbsolute(), "Путь должен быть абсолютным");
    }

    @Test
    @DisplayName("getProfilesDir объединяет rootPath и profilesPath")
    void profilesDirCombinesRootAndProfiles() {
        TestStoreProperties props = new TestStoreProperties();

        Path result = props.getProfilesFullPath();

        Path expected = Path.of("..", "TestStore", "profiles")
                .toAbsolutePath()
                .normalize();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("getProfilesDir нормализует путь (без .. и .)")
    void profilesDirIsNormalized() {
        TestStoreProperties props = new TestStoreProperties();

        Path result = props.getProfilesFullPath();

        assertEquals(result.normalize(), result, "Путь не должен содержать избыточных элементов");
    }

    @Test
    @DisplayName("Сеттеры работают и влияют на getProfilesDir")
    void settersAffectProfilesDir() {
        TestStoreProperties props = new TestStoreProperties();
        props.setRootDir("/custom/root");
        props.setProfilesDir("my-profiles");

        Path result = props.getProfilesFullPath();

        assertEquals(Path.of("/custom/root", "my-profiles"), result);
    }

    @Test
    @DisplayName("getRootPath возвращает строку, а не Path")
    void rootPathIsString() {
        TestStoreProperties props = new TestStoreProperties();

        assertInstanceOf(String.class, props.getRootDir());
    }
}
