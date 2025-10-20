package Data.Cabinet;

import Data.Card;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import Ui.Main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static Data.Card.PATH_CARD;
import static Util.CreateCard.createCard;
import static Util.Util.*;
import static Ui.Password.*;

/**
 * Экран настроек личного кабинета.
 * <p>
 * Позволяет: изменить имя/возраст/пароль, привязать карту, удалить карту,
 * вернуться в главное меню ЛК. В методах и обработчиках — перехват исключений.
 */
public class SettingsMenu {
    /** Кнопка возврата из меню настроек в главное меню ЛК. */
    private Button back = new Button("Назад");
    /** Кнопка привязки новой банковской карты к аккаунту пользователя. */
    private Button addCard = new Button("Привязать карту");
    /** Кнопка открытия экрана редактирования профиля (изменение имени, пароля, возраста и др.). */
    private Button changeData = new Button("Настроить профиль");
    private static List<Node> backupNodeSettings;

    public Button getBack() {
        return back;
    }
    public void setBack(Button back) {
        this.back = back;
    }
    public Button getAddCard() {
        return addCard;
    }
    public void setAddCard(Button addCard) {
        this.addCard = addCard;
    }
    public Button getChangeData() {
        return changeData;
    }
    public void setChangeData(Button changeData) {
        this.changeData = changeData;
    }
    public static List<Node> getBackupNodeSettings() {
        return backupNodeSettings;
    }
    public static void setBackupNodeSettings(List<Node> backupNodeSettings) {
        SettingsMenu.backupNodeSettings = backupNodeSettings;
    }

    /**
     * Рисует меню настроек: «Настроить профиль», «Привязать карту», «Назад».
     *
     * @param root            корневой VBox ЛК
     * @param personalAccount вкладка «Личный кабинет»
     * @param pa              объект ЛК текущего пользователя
     */
    public void settingsMenu(VBox root,
                             Tab personalAccount,
                             PA pa) {

        try {
            setBackupNodeSettings(backupNode(root));

            clearRoot(root);

            root.getChildren().addAll(getChangeData(),
                    getAddCard(),
                    getBack());

            List<Node> backupNodesSettingsMenu = backupNode(root);

            getAddCard().setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Входим в createCard");
                    createCard(root, personalAccount, pa.getUser());
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка в createCard: " + e.getMessage());
                    errMess(root, "Не удалось открыть привязку карты: " + e.getMessage());
                }
            });

            getBack().setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Возвращаемся в mainMenu");
                    root.getChildren().setAll(getBackupNodeSettings());
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка возврата в mainMenu: " + e.getMessage());
                    errMess(root, "Не удалось вернуться в главное меню: " + e.getMessage());
                }
            });

            getChangeData().setOnAction(_ -> {
                try {
                    Label label = new Label("Введите пароль");
                    TextField textField = new TextField();

                    textField.setMaxSize(horizontally, 30);

                    Button ok = new Button("Продолжить");
                    Button back = new Button("Назад");

                    clearRoot(root);

                    root.getChildren().addAll(label,
                            textField,
                            ok,
                            back);

                    ok.setOnAction(__ -> {
                        try {
                            boolean b = examLogin(textField.getText().trim(), pa.getUser());

                            if (b) {
                                out("Data/Cabinet/SettingsMenu.java: Входим в editData");
                                editData(root, personalAccount, pa);
                            }
                            else {
                                errMess(root, "Пароль неверный");
                            }
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/SettingsMenu.java: Ошибка проверки пароля: " + e.getMessage());
                            errMess(root, "Не удалось проверить пароль: " + e.getMessage());
                        }
                    });

                    back.setOnAction(__ -> {
                        try {
                            root.getChildren().setAll(backupNodesSettingsMenu);
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/SettingsMenu.java: Ошибка возврата из проверки пароля: " + e.getMessage());
                        }
                    });
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка подготовки экрана ввода пароля: " + e.getMessage());
                    errMess(root, "Не удалось открыть изменение профиля: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            out("Data/Cabinet/SettingsMenu.java: Ошибка в settingsMenu: " + e.getMessage());
            try { errMess(root, "Ошибка меню настроек: " + e.getMessage()); } catch (Exception ignored) {}
        }
    }

    /**
     * Экран изменения данных пользователя.
     *
     * <p><b>Кнопки:</b>
     * <ul>
     *   <li><b>Изменить имя</b> — открывает форму ввода нового имени и применяет изменение через «Принять».</li>
     *   <li><b>Изменить возраст</b> — показывает ComboBox с возрастом; сохранение через «Принять».</li>
     *   <li><b>Изменить пароль</b> — открывает поле ввода нового пароля; сохранение через «Принять».</li>
     *   <li><b>Удалить карту</b> — позволяет выбрать карту текущего пользователя и удалить её из файла и UI.</li>
     *   <li><b>Назад</b> — возвращает в меню настроек.</li>
     *   <li><b>Принять</b> — контекстная кнопка подтверждения в подэкранах (имя/возраст/пароль/удаление карты).</li>
     *   <li><b>Назад</b> (в подэкранах) — возвращает из подэкрана (имя/возраст/пароль/удаление карты) в список действий данного экрана.</li>
     * </ul>
     *
     * @param root            корневой VBox текущего экрана
     * @param personalAccount вкладка «Личный кабинет» (для навигации)
     * @param pa              объект ЛК текущего пользователя
     */
    public void editData(VBox root,
                         Tab personalAccount,
                         PA pa) {

        try {
            // Кнопка: открыть подэкран изменения имени
            Button editName = new Button("Изменить имя");
            // Кнопка: открыть подэкран изменения возраста
            Button editAge = new Button("Изменить возраст");
            // Кнопка: открыть подэкран изменения пароля
            Button editPass = new Button("Изменить пароль");
            // Кнопка: открыть подэкран удаления карты
            Button delCard = new Button("Удалить карту");
            // Кнопка: вернуться в меню настроек (из списка действий этого экрана)
            Button back = new Button("Назад");

            // Кнопка «Принять»: подтверждение действия в подэкранах (имя/возраст/пароль/удаление карты)
            Button ok = new Button("Принять");
            // Кнопка «Назад» (локальная для подэкранов): вернуться из подэкрана в список действий этого экрана
            Button backward = new Button("Назад");

            List<Node> backupNodes = backupNode(root);

            clearRoot(root);

            root.getChildren().addAll(editName,
                    editAge,
                    editPass,
                    delCard,
                    back);

            List<Node> backupNodesEditData = backupNode(root);

            out("Data/Cabinet/SettingsMenu.java: Поле editData создано");

            editName.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Входим в editName");
                    Label label = new Label("Введите новое имя");
                    TextField textField = new TextField();

                    textField.setMaxSize(horizontally, 30);

                    clearRoot(root);

                    root.getChildren().addAll(label,
                            textField,
                            ok,
                            backward);

                    // «Принять» в подэкране изменения имени: валидирует и сохраняет имя пользователя
                    ok.setOnAction(__ -> {
                        try {
                            if (!textField.getText().trim().isEmpty()
                                    && !textField.getText().trim().equals(pa.getUser().getName())) {
                                String newNameUser = textField.getText().trim();
                                pa.getUser().setName(newNameUser);
                                append(Path.of(Main.PATH), pa.getUser());
                                pa.setTittle();
                                out("Data/Cabinet/SettingsMenu.java: Успешно изменили имя");
                                backward.fire(); // возвращаемся к списку действий
                            }
                            else {
                                errMess(root, "Имя пустое или совпадает с текущем");
                            }
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/SettingsMenu.java: Ошибка изменения имени: " + e.getMessage());
                            errMess(root, "Не удалось изменить имя: " + e.getMessage());
                        }
                    });
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка подготовки editName: " + e.getMessage());
                }
            });

            editAge.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Входим в editAge");
                    Label label = new Label("Возраст");

                    ComboBox<Byte> comboBox = new ComboBox<>();

                    initializationAge(comboBox);

                    clearRoot(root);

                    root.getChildren().addAll(label,
                            comboBox,
                            ok,
                            backward);

                    // «Принять» в подэкране изменения возраста: применяет выбранный возраст и обновляет профиль
                    ok.setOnAction(__ -> {
                        try {
                            byte b = 0;
                            if (comboBox.getValue() != null) {
                                out("Data/Cabinet/SettingsMenu.java: Поле не пустое");
                                b = comboBox.getValue();
                            }
                            else {
                                errMess(root, "Поле пустое!");
                            }
                            if (b != 0) {
                                pa.getUser().setAge(b);
                                append(Path.of(Main.PATH), pa.getUser());
                                pa.setTittle();
                                out("Data/Cabinet/SettingsMenu.java: Успешно изменили возраст");
                                backward.fire(); // возвращаемся к списку действий
                            }
                            else {
                                errMess(root, "Неизвестная ошибка!");
                            }
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/SettingsMenu.java: Ошибка изменения возраста: " + e.getMessage());
                            errMess(root, "Не удалось изменить возраст: " + e.getMessage());
                        }
                    });
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка подготовки editAge: " + e.getMessage());
                }
            });

            editPass.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Входим в editPass");
                    Label label = new Label("Введите новый пароль");
                    TextField textField = new TextField();

                    textField.setMaxSize(horizontally, 30);

                    clearRoot(root);

                    root.getChildren().addAll(label,
                            textField,
                            ok,
                            backward);

                    // «Принять» в подэкране изменения пароля: хэширует и сохраняет новый пароль
                    ok.setOnAction(__ -> {
                        try {
                            String string = textField.getText().trim();
                            String stringHash = hashPassword(textField.getText().trim());
                            if (!string.isEmpty() && !stringHash.equals(pa.getUser().getPassword())) {
                                string = null;
                                pa.getUser().setPassword(stringHash);
                                append(Path.of(Main.PATH), pa.getUser());
                                pa.setTittle();
                                out("Data/Cabinet/SettingsMenu.java: Успешно изменили пароль");
                                backward.fire(); // возвращаемся к списку действий
                            }
                            else {
                                errMess(root, "Новый пароль пустой или совпадает с текущем");
                            }
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/SettingsMenu.java: Ошибка изменения пароля: " + e.getMessage());
                            errMess(root, "Не удалось изменить пароль: " + e.getMessage());
                        }
                    });
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка подготовки editPass: " + e.getMessage());
                }
            });

            delCard.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Входим в delCard");
                    Label label = new Label("Выберите какую карту удалить");

                    ComboBox<Integer> cardComboBox = new ComboBox<>();
                    ArrayList<Integer> mapToCardIndex = new ArrayList<>();

                    // наполняем список только картами текущего пользователя
                    for (int i = 0; i < pa.getCard().size(); i++) {
                        Card c = pa.getCard().get(i);
                        if (c.getUserID() == pa.getUser().getUserID()) {
                            mapToCardIndex.add(i);
                            cardComboBox.getItems().add(mapToCardIndex.size());
                        }
                    }
                    if (mapToCardIndex.isEmpty()) {
                        errMess(root, "Карты не найдены");
                        return;
                    }

                    clearRoot(root);

                    cardComboBox.setVisibleRowCount(Math.max(3, Math.min(10, cardComboBox.getItems().size())));

                    root.getChildren().addAll(label, cardComboBox, ok, backward);

                    // «Принять» в подэкране удаления карты: удаляет выбранную карту из JSON и UI
                    ok.setOnAction(___ -> {
                        try {
                            Integer chosen = cardComboBox.getValue();
                            if (chosen == null) {
                                errMess(root, "Выберите карту");
                                return;
                            }

                            // реальный индекс в pa.getCard()
                            int realIdx = mapToCardIndex.get(chosen - 1);
                            Card target = pa.getCard().get(realIdx);

                            // Удаляем из JSON только выбранную карту
                            boolean success = removeFromFile(
                                    PATH_CARD,
                                    Card.class,
                                    c -> c.getUserID() == pa.getUser().getUserID()
                                            && c.getIndexValid() == target.getIndexValid()
                            );

                            if (success) {
                                // синхронизируем состояние в памяти и в UI
                                pa.getCard().remove(realIdx);
                                pa.rebuildCardsBox();         // перерисовать VBox с балансами
                                out("Data/Cabinet/SettingsMenu.java: Карта успешно удалена");
                                backward.fire(); // возвращаемся к списку действий
                            }
                            else {
                                out("Data/Cabinet/SettingsMenu.java: Карта не найдена для удаления");
                                errMess(root, "Карта не была обнаружена");
                            }
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/SettingsMenu.java: Ошибка удаления карты: " + e.getMessage());
                            errMess(root, "Не удалось удалить карту: " + e.getMessage());
                        }
                    });
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка подготовки delCard: " + e.getMessage());
                }
            });

            // «Назад» (локальный в подэкранах): возвращает к списку действий этого экрана
            backward.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Возвращаемся в editData...");
                    root.getChildren().setAll(backupNodesEditData);
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка возврата в editData: " + e.getMessage());
                }
            });

            // «Назад» (основной): возвращает в меню настроек
            back.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/SettingsMenu.java: Возвращаемся в settingsMenu");
                    root.getChildren().setAll(backupNodes);
                }
                catch (Exception e) {
                    out("Data/Cabinet/SettingsMenu.java: Ошибка возврата в settingsMenu: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            out("Data/Cabinet/SettingsMenu.java: Ошибка в editData: " + e.getMessage());
            try { errMess(root, "Ошибка экрана изменения данных: " + e.getMessage()); } catch (Exception ignored) {}
        }
    }
}