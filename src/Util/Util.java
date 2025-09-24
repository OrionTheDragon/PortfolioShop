package Util;

import Data.Card;
import Data.User;
import Shop.Shop;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Набор вспомогательных методов для UI, файлового ввода/вывода JSON и общих операций.
 * <p>Во всех методах добавлены безопасные перехваты исключений без изменения основной логики.
 */
public class Util {
    /** Ширина окна по умолчанию. */
    public static int horizontally = 800;
    /** Высота окна по умолчанию. */
    public static int vertically = 600;

    /** Общий ObjectMapper с авто-подключением модулей. */
    public static final ObjectMapper M = new ObjectMapper().findAndRegisterModules();

    /** Текст символа «звёздочка», используемого для обозначения обязательных полей. */
    private static final String STAR_TEXT = "*";
    /** Шрифт, применяемый к звёздочке обязательного поля (System, размер 20). */
    private static final Font STAR_FONT = Font.font("System", 20);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final ObjectWriter PRETTY = MAPPER.writer(new DefaultPrettyPrinter());

    /**
     * Создаёт визуальную «звёздочку» (обязательный индикатор) для форм.
     * @param s если "Y" — выравнивается влево, иначе по центру
     * @return контейнер VBox со звёздочкой
     */
    public static VBox star(String s) {
        try {
            // сама звездочка
            Label starLabel = new Label(STAR_TEXT);
            starLabel.setFont(STAR_FONT);
            starLabel.setTextFill(Color.RED);

            HBox starBox = new HBox(starLabel);
            starBox.setAlignment("Y".equals(s) ? Pos.CENTER_LEFT : Pos.CENTER);

            VBox box = new VBox(0); // внутренний маленький зазор (если нужен)
            box.setFillWidth(true);
            box.getChildren().add(starBox);

            box.parentProperty().addListener((obs, oldParent, newParent) -> {
                try {
                    if (newParent instanceof VBox v) {
                        double spacing = v.getSpacing();
                        // убираем spacing, добавляем свой микрозазор
                        VBox.setMargin(box, new Insets(0, 0, -(spacing + 13), 0));
                    }
                }
                catch (Exception e) {
                    out("Util/Util.java: Ошибка в обработчике parentProperty: " + e.getMessage());
                }
            });
//            debugBordersFull(box);

            return box;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в star(): " + e.getMessage());
            return new VBox();
        }
    }

    public static<T> void append(Path file, T value) throws IOException {
        ensureParentDir(file);

        String current = Files.exists(file) ? Files.readString(file, StandardCharsets.UTF_8).trim() : "";

        JsonNode rootNode;

        if (current.isEmpty() || current.equals("null")) {
            // новый массив с единственным элементом
            ArrayNode arr = MAPPER.createArrayNode();
            // Безопасно для "любых" классов:
            arr.addPOJO(value);
            writeAtomic(file, PRETTY.writeValueAsString(arr));
            return;
        }

        // Пытаемся распарсить текущее содержимое
        JsonNode parsed;
        try {
            parsed = MAPPER.readTree(current);
        }
        catch (Exception ex) {
            // Если мусор/невалидный JSON — начинаем заново массивом
            ArrayNode arr = MAPPER.createArrayNode();
            arr.addPOJO(value);
            writeAtomic(file, PRETTY.writeValueAsString(arr));
            return;
        }

        if (parsed.isArray()) {
            ArrayNode arr = (ArrayNode) parsed;
            arr.addPOJO(value);
            writeAtomic(file, PRETTY.writeValueAsString(arr));
        }
        else if (parsed.isObject()) {
            ArrayNode arr = MAPPER.createArrayNode();
            arr.add(parsed);       // старый объект
            arr.addPOJO(value);    // новый объект
            writeAtomic(file, PRETTY.writeValueAsString(arr));
        }
        else {
            // Корень не объект и не массив — создаём корректный массив заново
            ArrayNode arr = MAPPER.createArrayNode();
            arr.addPOJO(value);
            writeAtomic(file, PRETTY.writeValueAsString(arr));
        }
    }

    private static void ensureParentDir(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static void writeAtomic(Path file, String content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(content.length() + 64);
        baos.write(content.getBytes(StandardCharsets.UTF_8));
        Path tmp = Files.createTempFile(
                file.getParent() != null ? file.getParent() : Path.of("."),
                file.getFileName().toString(), ".tmp"
        );
        Files.write(tmp, baos.toByteArray());
        try {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (Exception e) {
            // На некоторых FS ATOMIC_MOVE не поддерживается — используем обычный replace
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

//    /** Старая функция, не используется, оставлена в качестве демонстрации
//     * Добавляет карту в JSON-файл (append) с проставлением следующего {@link  Card#getIndexValid() getIndexValid}.
//     * @param newCard  добавляемая карта
//     * @param fileName путь к JSON
//     */
//    public static void appendCard(Card newCard, String fileName) {
//        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
//        File f = new File(fileName);
//        List<Card> cards = new ArrayList<>();
//
//        try {
//            if (f.exists() && f.length() > 0) {
//                JsonNode root = mapper.readTree(f);
//                if (root.isArray()) {
//                    cards = mapper.readValue(f, new TypeReference<List<Card>>() {});
//                }
//                else if (root.isObject()) {
//                    cards.add(mapper.treeToValue(root, Card.class));
//                }
//            }
//        }
//        catch (IOException ignore) {
//            // битый файл — начнём с пустого списка
//        }
//
//        // Проставим индекс (если используешь indexValid)
//        int nextIndex = cards.stream()
//                .mapToInt(Card::getIndexValid)
//                .max()
//                .orElse(-1) + 1;
//        newCard.setIndexValid(nextIndex);
//
//        cards.add(newCard);
//
//        writeListAtomic(fileName, cards);
//
//        try {
//            mapper.writerWithDefaultPrettyPrinter().writeValue(f, cards);
//            out("Карта добавлена.");
//        }
//        catch (IOException e) {
//            out("Ошибка при сохранении: " + e.getMessage());
//        }
//    }

//    /** Старая функция, не используется, оставлена в качестве демонстрации
//     * Добавляет пользователя в JSON-файл (append) с назначением нового {@link User#getUserID() getUserID}.
//     * @param newUser  пользователь
//     * @param fileName путь к JSON
//     */
//    public static void appendUser(User newUser, String fileName) {
//        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
//        File f = new File(fileName);
//        List<User> users = new ArrayList<>();
//
//        try {
//            if (f.exists() && f.length() > 0) {
//                JsonNode root = mapper.readTree(f);
//                if (root.isArray()) {
//                    users = mapper.readValue(f, new TypeReference<List<User>>() {});
//                }
//                else if (root.isObject()) {
//                    users.add(mapper.treeToValue(root, User.class));
//                }
//            }
//        }
//        catch (IOException ignore) {
//            // битый файл — начнём с пустого списка
//        }
//
//        // Проставим индекс (если используешь indexValid)
//        int nextIndex = users.stream()
//                .mapToInt(User::getUserID)
//                .max()
//                .orElse(-1) + 1;
//        newUser.setUserID(nextIndex);
//
//        users.add(newUser);
//
//        writeListAtomic(fileName, users);
//
//        try {
//            mapper.writerWithDefaultPrettyPrinter().writeValue(f, users);
//            out("Пользователь добавлен.");
//        }
//        catch (IOException e) {
//            out("Ошибка при сохранении: " + e.getMessage());
//        }
//    }

//    /** Старая функция, не используется, оставлена в качестве демонстрации
//     * Обновляет пользователя в JSON по {@link User#getUserID() getUserID} или добавляет, если не найден.
//     * @param updated  пользователь для upsert
//     * @param fileName путь к JSON
//     */
//    public static void upsertUser(User updated, String fileName) {
//        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
//        File f = new File(fileName);
//        List<User> users = new ArrayList<>();
//
//        try {
//            if (f.exists() && f.length() > 0) {
//                JsonNode root = mapper.readTree(f);
//                if (root.isArray()) {
//                    users = mapper.readValue(f, new TypeReference<List<User>>() {});
//                }
//                else if (root.isObject()) {
//                    users.add(mapper.treeToValue(root, User.class));
//                }
//            }
//        }
//        catch (IOException ignore) { /* битый/пустой файл */ }
//
//        // ищем пользователя по indexValid
//        int pos = -1;
//        for (int i = 0; i < users.size(); i++) {
//            if (users.get(i).getUserID() == updated.getUserID()) {
//                pos = i;
//                break;
//            }
//        }
//
//        if (pos >= 0) {
//            users.set(pos, updated); // обновляем существующего
//        }
//        else {
//            // если вдруг новый (indexValid не проставлен)
//            int nextIndex = users.stream()
//                    .mapToInt(User::getUserID)
//                    .max()
//                    .orElse(-1) + 1;
//            updated.setUserID(nextIndex);
//            users.add(updated);
//        }
//
//        // сохраняем список обратно в файл
//        try {
//            mapper.writerWithDefaultPrettyPrinter().writeValue(f, users);
//            out("Пользователь сохранён (upsert).");
//        }
//        catch (IOException e) {
//            out("Ошибка при сохранении: " + e.getMessage());
//        }
//    }

//    /** Старая функция, не используется, оставлена в качестве демонстрации
//     * Безопасная атомарная запись списка в файл: сперва во временный, затем атомарный move.
//     * @param fileName целевой файл
//     * @param data     данные
//     */
//    public static void writeListAtomic(String fileName, List<?> data) {
//        ObjectMapper m = new ObjectMapper().findAndRegisterModules();
//        Path target = Path.of(fileName);
//        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
//
//        try {
//            // JSON с переносами
//            m.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), data);
//
//            // атомарная подмена (по возможности ОС)
//            try {
//                java.nio.file.Files.move(tmp, target,
//                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
//                        java.nio.file.StandardCopyOption.ATOMIC_MOVE);
//            }
//            catch (AtomicMoveNotSupportedException e) {
//                java.nio.file.Files.move(tmp, target,
//                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//            }
//        }
//        catch (IOException e) {
//            out("Ошибка записи файла: " + e.getMessage());
//            try { java.nio.file.Files.deleteIfExists(tmp); } catch (IOException ignore) {}
//        }
//    }

    /**
     * Читает JSON в список объектов указанного класса.
     * Прямой вызов возвращает пустой список, если файл не существует/пустой/битый.
     * Работает в связке {@link Util#safeReadList(String, Class) safeReadList}
     *
     * @param fileName путь к JSON
     * @param clazz    класс элементов
     * @param <T>      тип элементов
     * @return список элементов (возможно пустой)
     */
    public static <T> List<T> readList(String fileName, Class<T> clazz) {
        ObjectMapper m = new ObjectMapper().findAndRegisterModules();
        File f = new File(fileName);
        List<T> items = new ArrayList<>();
        if (!f.exists() || f.length() == 0) return items;

        try {
            JsonNode root = m.readTree(f);
            if (root.isArray()) {
                return m.readValue(
                        f,
                        m.getTypeFactory().constructCollectionType(List.class, clazz)
                );
            }
            else if (root.isObject()) {
                items.add(m.treeToValue(root, clazz));
                return items;
            }
            else {
                // неожиданный тип корня — вернём пустой
                return items;
            }
        }
        catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            looksLikeTruncatedArray(f);
            // файл битый → переименуем и начнём с пустого
            File bak = new File(fileName + ".corrupt." + System.currentTimeMillis());
            //noinspection ResultOfMethodCallIgnored
            f.renameTo(bak);
            out("Ошибка чтения файла: " + e.getMessage());
            out("Файл помечен как повреждён: " + bak.getName());
            return new ArrayList<>();
        }
        catch (IOException e) {
            out("Ошибка чтения файла: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Никогда не вернёт null: либо список из файла, либо пустой список.
     * Она нужна, несмотря на то, что есть функция Util/Util.java.readList()
     *
     * @param path путь к JSON
     * @param cls  класс элементов
     * @param <T>  тип элементов
     * @return список (возможно пустой), но не null
     */
    public static <T> List<T> safeReadList(String path, Class<T> cls) {
        try {
            File f = new File(path);
            if (!f.exists() || f.length() == 0) return Collections.emptyList();
            List<T> list = readList(path, cls);
            return (list != null) ? list : Collections.emptyList();
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в safeReadList: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Эвристика: похоже ли содержимое на обрезанный JSON-массив.
     * @param f файл
     * @return true, если начинается с '[' и не заканчивается ']' (после тримминга)
     */
    static boolean looksLikeTruncatedArray(File f) {
        try {
            String s = java.nio.file.Files.readString(f.toPath());
            return s.stripLeading().startsWith("[") && !s.stripTrailing().endsWith("]");
        }
        catch (IOException e) {
            return false;
        }
    }

    /**
     * Удаляет из JSON сущности, удовлетворяющие фильтру.
     *
     * @param fileName путь к JSON
     * @param clazz    класс сущности
     * @param filter   предикат удаления
     * @param <T>      тип сущности
     * @return true, если что-то удалили; false — если изменений нет или ошибка
     */
    public static <T> boolean removeFromFile(String fileName, Class<T> clazz, Predicate<T> filter) {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        File f = new File(fileName);

        try {
            List<T> all = new ArrayList<>();

            if (f.exists() && f.length() > 0) {
                JsonNode root = mapper.readTree(f);
                if (root.isArray()) {
                    all = mapper.readValue(f, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
                } else if (root.isObject()) {
                    all.add(mapper.treeToValue(root, clazz));
                }
            }

            // фильтруем: оставляем только те, что НЕ подходят под условие
            List<T> updated = all.stream()
                    .filter(item -> !filter.test(item))
                    .collect(Collectors.toList());

            // если ничего не изменилось → вернём false
            if (all.size() == updated.size()) {
                return false;
            }

            // перезаписываем JSON
            mapper.writerWithDefaultPrettyPrinter().writeValue(f, updated);
            return true;
        }
        catch (IOException e) {
            out("Ошибка при удалении: " + e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, есть ли в файле хотя бы один элемент, удовлетворяющий условию.
     *
     * @param fileName путь к JSON
     * @param clazz    класс элементов
     * @param condition предикат проверки
     * @param <T> тип
     * @return true, если найден хотя бы один; иначе false
     */
    public static <T> boolean checkInFile(String fileName, Class<T> clazz, Predicate<T> condition) {
        try {
            List<T> items = readList(fileName, clazz);
            return items.stream().anyMatch(condition);
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в checkInFile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Делает label крупнее/жирнее для ЛК.
     * @param label метка
     * @return та же метка (для чейнинга)
     */
    public static Label editLabelPA(Label label) {
        try {
            label.setFont(Font.font(String.valueOf(FontWeight.BOLD), 16));
            return label;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в editLabelPA: " + e.getMessage());
            return label;
        }
    }

    /**
     * Очищает VBox, оставляя только TabPane (если есть).
     * @param root контейнер
     * @return тот же контейнер
     */
    public static VBox clearRoot(VBox root) {
        try {
            root.getChildren().removeIf(node -> !(node instanceof TabPane));
            return root;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в clearRoot: " + e.getMessage());
            return root;
        }
    }

    /**
     * Оборачивает переданный VBox в ScrollPane с нужными настройками.
     * @param content содержимое
     * @return родитель для помещения в сцену
     */
    public static Parent settingVBox(VBox content) {
        try {
            content.setSpacing(10);
            content.setPadding(new Insets(15));
            content.setAlignment(Pos.TOP_CENTER);
            content.setFillWidth(true);

            ScrollPane sp = new ScrollPane(content);
            sp.setFitToWidth(true);
            sp.setFitToHeight(true);
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setPannable(true);

            sp.viewportBoundsProperty().addListener((obs, o, b) -> {
                try {
                    content.setPrefWidth(b.getWidth());
                }
                catch (Exception e) {
                    out("Util/Util.java: Ошибка в viewportBounds listener: " + e.getMessage());
                }
            });

            sp.setMaxWidth(Double.MAX_VALUE);
            sp.setPrefSize(horizontally, vertically);

            return sp;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в settingVBox: " + e.getMessage());
            return content;
        }
    }

    /**
     * Базовая настройка текстового поля для «циферок» (узкое поле 1 символ).
     * @param textField поле
     * @return то же поле (для чейнинга)
     */
    public static TextField editTextFiled(TextField textField) {
        try {
            textField.setPrefColumnCount(1);
            textField.setMaxWidth(30);
            textField.setMaxHeight(25);
            return textField;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в editTextFiled: " + e.getMessage());
            return textField;
        }
    }

    /**
     * Навешивает фильтры и навигацию по массиву полей (цифровой ввод, автопереход).
     *
     * @param text      текущее поле
     * @param allFields массив полей
     * @param index     индекс текущего поля
     */
    public static void eventFilter(TextField text, TextField[] allFields, int index) {
        try {
            text.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                try {
                    String character = event.getCharacter();
                    if (!character.matches("\\d") || text.getText().length() >= 1) {
                        event.consume();
                    }
                }
                catch (Exception e) {
                    out("Util/Util.java: Ошибка в KEY_TYPED handler: " + e.getMessage());
                }
            });

            text.setOnKeyReleased(event -> {
                try {
                    if (!text.getText().isEmpty() && event.getCode() != KeyCode.BACK_SPACE && index < allFields.length - 1) {
                        TextField next = allFields[index + 1];
                        if (next != null) {
                            next.requestFocus();
                        }
                    }
                }
                catch (Exception e) {
                    out("Util/Util.java: Ошибка в onKeyReleased: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в eventFilter: " + e.getMessage());
        }
    }

    /**
     * Показывает/обновляет блок ошибки вверху контейнера.
     * @param root   корневой VBox
     * @param msg текст ошибки
     */
    public static void errMess(VBox root, String msg) {
        javafx.application.Platform.runLater(() -> {
            try {
                Label err = (Label) root.lookup("#errorLabel");
                if (err == null) {
                    err = new Label(msg);
                    err.setId("errorLabel");
                    err.setFont(Font.font("System", FontWeight.BOLD, 16));
                    err.setTextFill(Color.RED);
                    err.setAlignment(Pos.CENTER);
                    err.setMaxWidth(Double.MAX_VALUE);
                    root.getChildren().add(0, err); // добавить один раз
                }
                else {
                    err.setText(msg);          // <-- обновляем текст
                    err.setVisible(true);
                    err.setManaged(true);
                }
            }
            catch (Exception e) {
                out("Util/Util.java: Ошибка в errMess: " + e.getMessage());
                e.printStackTrace(); // если хочешь видеть стек при дебаге
            }
        });
    }


    /**
     * Проверяет, что все поля массива непустые.
     * @param textField массив полей
     * @return true, если все заполнены
     */
    public static boolean checkText(TextField[] textField) {
        try {
            for (TextField text : textField) {
                if (text == null || text.getText().trim().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в checkText[]: " + e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, что поле непустое.
     * @param textField поле
     * @return true, если непустое
     */
    public static boolean checkText(TextField textField) {
        try {
            if (textField.getText().trim().isEmpty()) {
                return false;
            }
            return true;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в checkText: " + e.getMessage());
            return false;
        }
    }

    /**
     * Собирает символы из массива полей ввода в массив char (по первому символу каждого поля).
     * @param textFields поля ввода
     * @return массив символов
     */
    public static char[] getCharArrayFromTextFields(TextField[] textFields) {
        try {
            char[] chars = new char[textFields.length];
            for (int i = 0; i < textFields.length; i++) {
                String input = textFields[i].getText().trim();
                chars[i] = !input.isEmpty() ? input.charAt(0) : ' ';
            }
            return chars;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в getCharArrayFromTextFields: " + e.getMessage());
            return new char[0];
        }
    }

    /**
     * Создаёт жирный Label с заданным текстом.
     * @param text текст
     * @return сконфигурированный Label
     */
    public static Label makeLabel(String text) {
        try {
            Label label = new Label(text);
            label.setFont(Font.font(null, FontWeight.BOLD, 16));
            return label;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в makeLabel(text): " + e.getMessage());
            return new Label(text);
        }
    }

    /**
     * Применяет жирное начертание к уже существующему Label.
     * @param label метка
     * @return та же метка
     */
    public static Label makeLabel(Label label) {
        try {
            label.setFont(Font.font(null, FontWeight.BOLD, 16));
            return label;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в makeLabel(label): " + e.getMessage());
            return label;
        }
    }

    /**
     * Инициализирует комбобокс возрастов значениями 12..100.
     * @param comboBox целевой ComboBox
     * @return тот же ComboBox
     */
    public static ComboBox initializationAge(ComboBox<Byte> comboBox) {
        try {
            out("Ui/Main.java: Инициализация бокса с возрастом");
            ObservableList<Byte> list = FXCollections.observableArrayList();
            for (byte i = 12; i <= 100; i++) {
                list.add(i);
            }
            comboBox.setItems(list);
            return comboBox;
        }
        catch (Exception e) {
            out("Util/Util.java: Ошибка в initializationAge: " + e.getMessage());
            return comboBox;
        }
    }

    public static Image loadImage(String path, double width, double height,
                                  boolean preserveRatio, boolean smooth) {
        var url = Shop.class.getResource(path);
        if (url == null) {
            out("Shop/Shop.java: ресурс не найден: " + path);
            return null;
        }
        // Создаём Image с нужными размерами
        return new Image(url.toExternalForm(), width, height, preserveRatio, smooth);
    }

    /**
     * Простой логгер на System.out.
     * @param msg сообщение
     * @param <T> тип сообщения
     */
    public static<T> void out(T msg) {
        System.out.println(msg);
    }
}