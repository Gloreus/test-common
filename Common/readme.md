# TestStoreProperties

Конфигурационный класс для работы с путями к хранилищу тестовых данных (профили и кейсы).

## Конфигурация

Все настройки задаются в `application.yml` под префиксом `teststore`:

```yaml
teststore:
  root-dir: ../TestStore
  profiles-dir: profiles
  cases-dir: cases
```

## Поля

| Поле | Тип | По умолчанию | Описание |
|------|-----|-------------|----------|
| `rootDir` | `String` | `../TestStore` | Корневая директория хранилища. Относительная — вычисляется от рабочей директории процесса |
| `profilesDir` | `String` | `profiles` | Имя поддиректории с профилями |
| `casesDir` | `String` | `cases` | Имя поддиректории с кейсами |

## Методы

### `getProfilesFullPath()`

Возвращает абсолютный нормализованный путь к директории профилей.

```java
// ../TestStore/profiles → /home/user/project/TestStore/profiles
Path profiles = properties.getProfilesFullPath();
```

### `getCasesFullPath()`

Возвращает абсолютный нормализованный путь к директории кейсов.

```java
// ../TestStore/cases → /home/user/project/TestStore/cases
Path cases = properties.getCasesFullPath();
```

Оба метода склеивают `rootDir` с соответствующей поддиректорией, приводят путь к абсолютному и убирают избыточные элементы (`..`, `.`).

## Использование

Класс является Spring-бином — внедряется через конструктор:

```java
@Component
public class ProfileLoader {

    private final TestStoreProperties properties;

    public ProfileLoader(TestStoreProperties properties) {
        this.properties = properties;
    }

    public List<Path> loadProfiles() throws IOException {
        Path dir = properties.getProfilesFullPath();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{yaml,yml}")) {
            List<Path> files = new ArrayList<>();
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    files.add(entry);
                }
            }
            return files;
        }
    }
}
```

## Предостережения

- `rootDir` по умолчанию относительный (`../TestStore`). Результат зависит от рабочей директории процесса (`user.dir`). В production рекомендуется указывать абсолютный путь.
- Все поля доступны через геттеры и сеттеры (Lombok `@Data`). Базовые геттеры `getRootDir()`, `getProfilesDir()`, `getCasesDir()` возвращают `String` — как задано в конфиге. Методы `getProfilesFullPath()` и `getCasesFullPath()` возвращают готовый `Path`.
```