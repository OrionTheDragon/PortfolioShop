package Ui;

import Shop.Shop;
import Data.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static Util.Util.*;
import static Shop.Shop.tabPane;

/**
 * Главный класс JavaFX-приложения «Фейк магазин».
 *
 * Отвечает за старт UI, показ главного меню, регистрацию/вход пользователя и выход из приложения.
 * В методах и обработчиках событий добавлены перехваты исключений (try/catch) для безопасного UX.
 *
 * Примечание по изменениям: в файл были добавлены только комментарии и документация (Javadoc) к
 * сложным местам и к секциям с потенциальными точками отказа. Логика и порядок вызовов не изменялись.
 */
public class Main extends Application {

    /** Главная сцена приложения JavaFX; хранится для смены корня/сцен. */
    private static Stage primaryStage;

    /** Название/путь файла с сохранением пользователей. */
    public static final String PATH = "User.json";

    private static Shop shop;

    // Боксы
    /** Корневой контейнер текущего экрана (вертикальная колонка с отступом 10). */
    private VBox root = new VBox(10);
    /** Горизонтальный контейнер с кнопками (например, «Сохранить»/«Назад»). */
    private HBox button = new HBox(10);
    /** Комбобокс для выбора возраста пользователя. */
    private ComboBox<Byte> comboBoxAge = new ComboBox<>();

    // Этикетки
    /** Этикетка-подсказка для поля ввода имени. */
    private Label labelName = new Label("Введите ваше имя");
    /** Этикетка-подсказка для поля ввода пароля. */
    private Label labelPass = new Label("Введите пароль");
    /** Этикетка-подсказка для поля ввода наличных средств. */
    private Label labelCash = new Label("Введите количество наличных средств");
    /** Этикетка-подсказка для выбора возраста. */
    private Label labelAge = new Label("Сколько вам лет?");

    // Кнопки
    /** Кнопка запуска регистрации нового аккаунта. */
    private Button addAccount = new Button("Зарегистрироваться");
    /** Кнопка перехода на экран входа. */
    private Button loginAccount = new Button("Войти");
    /** Кнопка выхода из приложения. */
    private Button exit = new Button("Выйти");
    /** Кнопка сохранения введённых данных (на экране регистрации/редактирования). */
    private Button buttonSave = new Button("Сохранить");
    /** Кнопка возврата на предыдущий экран/меню. */
    private Button buttonBack = new Button("Назад");

    // Данные пользователя
    /** Текущее введённое имя пользователя. */
    private String name;
    /** Текущий хеш пароля пользователя */
    private String password;
    /** Текущий выбранный возраст пользователя. */
    private byte age;
    /** Текущее количество наличных средств пользователя. */
    private double cash;

    // Текст ареа
    /** Поле ввода имени. */
    private TextArea textName = new TextArea();
    /** Поле ввода пароля. */
    private TextArea textPass = new TextArea();
    /** Поле ввода суммы наличных. */
    private TextArea textCash = new TextArea();

    // Юзер
    /** Текущий пользователь (после регистрации/входа). */
    private User user;

    // Файлы
    /** Файл сохранения пользователей, созданный по пути {@link #PATH}. */
    private File saveFile = new File(PATH);

    /** Нода для возврата на начальный экран */
    private static List<Node> backupNodeMain;

    /** Кэш пользователей, считанный из {@link #PATH}. */
    private static List<User> userList = safeReadList(PATH, User.class);

    /** Глобальная ссылка на текущий экземпляр приложения для удобного доступа из других классов. */
    public static Main INSTANCE;

    // Блок get и set ------->
    public VBox getRoot() { return root; }
    public void setRoot(VBox root) { this.root = root; }
    public ComboBox<Byte> getComboBoxAge() { return comboBoxAge; }
    public Label getLabelName() { return labelName; }
    public void setLabelName(Label labelName) { this.labelName = labelName; }
    public Label getLabelCash() { return labelCash; }
    public void setLabelCash(Label labelCash) { this.labelCash = labelCash; }
    public Button getButtonSave() { return buttonSave; }
    public void setButtonSave(Button buttonSave) { this.buttonSave = buttonSave; }
    public Button getButtonBack() { return buttonBack; }
    public void setButtonBack(Button buttonBack) { this.buttonBack = buttonBack; }
    public Button getAddAccount() { return addAccount; }
    public void setAddAccount(Button addAccount) { this.addAccount = addAccount; }
    public Button getLoginAccount() { return loginAccount; }
    public void setLoginAccount(Button loginAccount) { this.loginAccount = loginAccount; }
    public Button getExit() { return exit; }
    public void setExit(Button exit) { this.exit = exit; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public byte getAge() { return age; }
    public void setAge(byte age) { this.age = age; }
    public double getCash() { return cash; }
    public void setCash(double cash) { this.cash = cash; }
    public HBox getButton() { return button; }
    public void setButton(HBox button) { this.button = button; }
    public TextArea getTextName() { return textName; }
    public void setTextName(TextArea textName) { this.textName = textName; }
    public TextArea getTextCash() { return textCash; }
    public void setTextCash(TextArea textCash) { this.textCash = textCash; }
    public Label getLabelAge() { return labelAge; }
    public void setLabelAge(Label labelAge) { this.labelAge = labelAge; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Label getLabelPass() { return labelPass; }
    public void setLabelPass(Label labelPass) { this.labelPass = labelPass; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public TextArea getTextPass() { return textPass; }
    public void setTextPass(TextArea textPass) { this.textPass = textPass; }
    public File getSaveFile() { return saveFile; }
    public static List<User> getUserList() { return userList; }
    public static void setUserList(List<User> userList) { Main.userList = userList; }
    public static Shop getShop() { return shop; }
    public static void setShop(Shop shop) { Main.shop = shop; }
    public static List<Node> getBackupNodeMain() { return backupNodeMain; }
    public static void setBackupNodeMain(List<Node> backupNodeMain) { Main.backupNodeMain = backupNodeMain; }
    // <------- Конец блока

    // Блок запуска приложения ------->
    /**
     * Точка входа JavaFX-приложения.
     *
     * Этот метод отвечает за инициализацию UI: подготовку меток, сцены и запуск главного меню.
     * Любые ошибки стартового процесса логируются и показывают пользователю краткое сообщение.
     */
    @Override
    public void start(Stage stage) {
        try {
            out("Ui/Main.java: Приложение запустилось");

            INSTANCE = this;
            primaryStage = stage;

            // Настройки Этикеток — отдельная точка, где внешний util форматирует Label'ы
            setLabelName(makeLabel(getLabelName()));
            setLabelPass(makeLabel(getLabelPass()));
            setLabelAge(makeLabel(getLabelAge()));
            setLabelCash(makeLabel(getLabelCash()));

            Parent ui = settingVBox(getRoot());
            Scene scene = new Scene(ui, horizontally, vertically);
            stage.setScene(scene);
            stage.setTitle("Фейк магазин");
            stage.show();

            // Показать главное меню. menuLog содержит собственные перехваты.
            menuLog();
        }
        catch (Exception e) {
            // Здесь — единственная безопасная попытка показать сообщение об ошибке во время старта.
            out("Ui/Main.java: Ошибка при старте приложения: " + e.getMessage());
            try {
                errMess(getRoot(), "Произошла ошибка запуска: " + e.getMessage());
            }
            catch (Exception ignored) {
                // errMess может упасть, если UI не инициализирован — в этом случае просто логируем.
                out("Приложение не готово: " + ignored.getMessage());
            }
        }
    }
    // <------- Конец блока запуска приложения

    // Блок главного меню, инициализирует кнопки: "Зарегистрироваться", "Войти", "Выход". ------->
    /**
     * Отрисовывает главное меню (регистрация/вход/выход).
     * Обновляет сцену, выставляет обработчики.
     *
     * Комментарий по ошибкам: здесь важно ловить любые исключения при взаимодействии с tabPane и root,
     * так как они могут быть модифицированы из других частей приложения. Вся критичная логика окружена
     * try/catch, чтобы UI не упал полностью.
     */
    public void menuLog() {
        try {
            tabPane.getTabs().clear();
            getRoot().getChildren().clear();
            out("Ui/Main.java: Очистили tabPane: " + tabPane.getTabs().toString());

            getRoot().setAlignment(Pos.TOP_CENTER);

            Parent ui = settingVBox(getRoot());

            Scene sc = primaryStage.getScene();
            if (sc == null) {
                primaryStage.setScene(new Scene(ui, horizontally, vertically));
            }
            else {
                sc.setRoot(ui);
            }

            // Центруем кнопки
            getAddAccount().setAlignment(Pos.CENTER);
            getLoginAccount().setAlignment(Pos.CENTER);
            getExit().setAlignment(Pos.CENTER);

            getRoot().getChildren().addAll(getAddAccount(),
                    getLoginAccount(),
                    getExit());

            // Сохраняем точку возврата — backupNode служит для восстановления интерфейса
            setBackupNodeMain(backupNode(getRoot()));

            // Обработчики кнопок: в обработчиках дополнительно ловим исключения, т.к. они могут возникать
            // как при логике экрана, так и при взаимодействии с файловой системой/Shop.
            getAddAccount().setOnAction(_ -> {
                try {
                    addsAccount();
                }
                catch (Exception e) {
                    out("Ui/Main.java: Ошибка в обработчике addAccount: " + e.getMessage());
                    errMess(getRoot(), "Не удалось открыть регистрацию: " + e.getMessage());
                }
            });

            getLoginAccount().setOnAction(_ -> {
                try {
                    loginsAccount();
                }
                catch (Exception e) {
                    out("Ui/Main.java: Ошибка в обработчике loginAccount: " + e.getMessage());
                    errMess(getRoot(), "Не удалось открыть вход: " + e.getMessage());
                }
            });

            getExit().setOnAction(_ -> {
                try {
                    exits();
                }
                catch (Exception e) {
                    out("Ui/Main.java: Ошибка в обработчике exit: " + e.getMessage());
                    errMess(getRoot(), "Не удалось открыть окно выхода: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            // Общий обработчик для menuLog: защищает сборку главного меню от непредвиденных сбоев.
            out("Ui/Main.java: Ошибка при формировании главного меню: " + e.getMessage());
            errMess(getRoot(), "Ошибка главного меню: " + e.getMessage());
        }
    }
    // <------- Конец блока с главным меню

    // Блок инициализация действий кнопки входа в аккаунт. ------->
    /**
     * Экран входа: проверяет наличие файла сохранения, показывает поля логина/пароля.
     * При успешной проверке открывает магазин.
     *
     *     Подробности и уязвимые точки:
     * - Чтение списка пользователей происходит из кэша {@link #userList}. При повреждении файла возможен
     *   NPE или пустой список — это обрабатывается и отображается пользователю.
     * - Обработчик нажатия кнопки входа содержит в себе логику поиска пользователя и проверки пароля;
     *   ошибки в этой логике безопасно перехватываются и выводятся в UI.
     */
    public void loginsAccount() {
        try {
            if (getSaveFile().exists()) {
                clearRoot(getRoot());
                out("Ui/Main.java: Файл сохранения найден: " + getSaveFile());

                List<User> users = getUserList();
                if (users == null || users.isEmpty()) {
                    errMess(getRoot(), "Нет ни одного пользователя в сохранении");
                    return;
                }
                for (User u : users) {
                    out("Data/User.java: Юзер успешно загружен: " + u.getName() + ", индекс: " + u.getUserID());
                }

                Label[] labelExam = { makeLabel("Введите имя"), makeLabel("Введите пароль") };
                TextArea loginField = new TextArea();
                TextArea passField  = new TextArea(); // лучше PasswordField, см. примечание ниже
                loginField.setPrefSize(horizontally, 20);
                passField.setPrefSize(horizontally, 20);

                Button login = new Button("Войти");

                getRoot().getChildren().addAll(
                        labelExam[0], loginField,
                        labelExam[1], passField,
                        login, getButtonBack()
                );

                out("Ui/Main.java: Создалось окно входа в аккаунт");

                // Обработчик кнопки входа.
                login.setOnAction(_ -> {
                    try {
                        String name = loginField.getText().trim();
                        String pass = passField.getText().trim();

                        if (name.isEmpty() || pass.isEmpty()) {
                            errMess(getRoot(), "Введите логин и пароль");
                            return;
                        }

                        User found = null;
                        for (User u : getUserList()) {
                            if (name.equals(u.getName())) {
                                found = u;
                                break;
                            }
                        }
                        if (found == null) {
                            errMess(getRoot(), "Пользователь не найден");
                            return;
                        }

                        boolean ok = Password.examLogin(name, pass);
                        if (!ok) {
                            errMess(getRoot(), "Неверный логин или пароль");
                            return;
                        }

                        getRoot().getChildren().clear();
                        setShop(new Shop());
                        getShop().shop(getRoot(), found);
                    }
                    catch (Exception e) {
                        // Здесь ключевой момент — отлов исключений в процессе проверки и инициирования сессии.
                        out("Ui/Main.java: Ошибка при входе: " + e);
                        e.printStackTrace(); // на время дебага — видеть стек
                        errMess(getRoot(), "Не удалось выполнить вход: " + (e.getMessage() == null ? e.toString() : e.getMessage()));
                    }
                });

            }
            else {
                out("Ui/Main.java: Файл повреждён или отсутствует: " + getSaveFile());
                errMess(getRoot(), "Не найден файл сохранения");
            }

            getButtonBack().setOnAction(_ -> {
                try {
                    out("Ui/Main.java: Возвращаемся");
                    getRoot().getChildren().setAll(getBackupNodeMain());
                }
                catch (Exception e) {
                    out("Ui/Main.java: Ошибка при возврате из входа: " + e);
                    errMess(getRoot(), "Не удалось вернуться: " + (e.getMessage() == null ? e.toString() : e.getMessage()));
                }
            });

        }
        catch (Exception e) {
            // Защищаем создание экрана входа от неожиданных ошибок (например, доступ к первичным ресурсам).
            out("Ui/Main.java: Ошибка экрана входа: " + e);
            e.printStackTrace(); // добавь стек для реальной причины
            errMess(getRoot(), "Ошибка экрана входа: " + (e.getMessage() == null ? e.toString() : e.getMessage()));
        }
    }
    // <------- Конец блока входа в аккаунт

    // Блок инициализации действия кнопки добавить аккаунт. ------->
    /**
     * Экран регистрации: собирает имя/пароль/возраст/сумму, проверяет ввод, сохраняет пользователя.
     *
     * Критические моменты и пояснения:
     * - Подсчёт нового ID пользователя выполняется через {@link AtomicInteger} — это простая эмпирическая
     *   реализация для последовательного присвоения ID при старте приложения.
     * - При парсинге строки суммы возможен {@link NumberFormatException} — это ловится и аккуратно
     *   показывается пользователю как сообщение об ошибке.
     * - Операция append(Path.of(PATH), getUser()) взаимодействует с файловой системой; возможны
     *   IOException и другие низкоуровневые ошибки — они логируются, но не ломают UI.
     */
    public void addsAccount() {
        try {
            AtomicInteger indexUser = new AtomicInteger();
            for (User u : getUserList()) {
                if (u != null) {
                    out("Ui/Main.java: Перебираем ID пользователей: " + u.getUserID());
                    indexUser.incrementAndGet();
                }
                else {
                    out("Ui/Main.java: Пользователей нет");
                }
            }

            out("Ui/Main.java: Создаем окно регистрации...");
            clearRoot(getRoot());
            initializationAge(getComboBoxAge());

            if (!getButton().getChildren().contains(getButtonSave()) && !getButton().getChildren().contains(getButtonBack())) {
                out("Ui/Main.java: Кнопки <Сохранить>, <Назад> успешно добавлены");
                getButton().getChildren().addAll(getButtonSave(), getButtonBack());
            }
            else {
                out("Ui/Main.java: Кнопки уже существуют, добавлять не нужно");
            }

            getButton().setAlignment(Pos.CENTER);
            getTextName().setPromptText("Введите ваше имя");
            getTextCash().setPromptText("Введите количество наличных средств");

            getRoot().getChildren().addAll(getLabelName(),
                    star("Y"),
                    getTextName(),
                    getLabelPass(),
                    star("Y"),
                    getTextPass(),
                    getLabelAge(),
                    star(""),
                    getComboBoxAge(),
                    getLabelCash(),
                    getTextCash(),
                    getButton());

            out("Ui/Main.java: VBox - root заполнен");

            // Обработчик сохранения нового пользователя.
            getButtonSave().setOnAction(e -> {
                try {
                    out("Ui/Main.java: Начался процесс сохранения");
                    String nameText = getTextName().getText().trim();

                    for (User u : getUserList()) {
                        if (u.getName().equals(nameText)) {
                            errMess(getRoot(), "Пользователь с таким именем уже существует");
                            return;
                        }
                    }

                    String passText = getTextPass().getText().trim();
                    String cashText = getTextCash().getText().trim();
                    Byte ageValue = getComboBoxAge().getValue();

                    StringBuilder err = new StringBuilder();
                    if (nameText.isEmpty() || passText.isEmpty() || ageValue == null || ageValue == 0) {
                        err.append("Одно из полей не заполнено");
                    }

                    double cashValue = 0.0;
                    if (!cashText.isEmpty()) {
                        try {
                            cashValue = Double.parseDouble(cashText.replace(',', '.'));
                            if (!err.isEmpty()) {
                                err.replace(25, err.length(), "");
                            }
                        }
                        catch (NumberFormatException ex) {
                            // Явная обработка некорректного ввода числа — пользователь увидит сообщение.
                            err.append("/Сумма наличных должна быть числом\n");
                        }
                    }

                    Label existing = (Label) getRoot().lookup("#errorLabel");
                    if (err.length() > 0) {
                        if (existing == null) {
                            Label label = new Label(err.toString());
                            label.setId("errorLabel"); // помечаем один раз
                            label.setFont(Font.font(String.valueOf(FontWeight.BOLD), 16));
                            label.setTextFill(Color.RED);
                            label.setAlignment(Pos.CENTER);
                            label.setMaxWidth(Double.MAX_VALUE);

                            getRoot().getChildren().addFirst(label);
                            out("Ui/Main.java: Поле ошибки добавлено");
                        }
                        else {
                            existing.setText(err.toString());
                            existing.setVisible(true);
                            existing.setManaged(true);
                            out("Ui/Main.java: Поле ошибки обновлено");
                        }
                        return;
                    }
                    else {
                        if (existing != null) {
                            existing.setText("");
                            existing.setVisible(false);
                            existing.setManaged(false);
                            out("Ui/Main.java: Ошибки не обнаружены, очищаем");
                        }
                    }

                    setName(nameText);
                    setPassword(Password.hashPassword(passText));
                    setAge(ageValue);
                    setCash(cashValue);

                    out("ID нового пользователя: №" + indexUser.get());

                    setUser(new User(indexUser.get(), getName(), getPassword(), getAge(), getCash()));

                    out("Ui/Main.java: Вызвали appendUser");
                    // Операция записи в файл: возможны исключения, которые логируются
                    append(Path.of(PATH), getUser());

                    try {
                        // Перечитываем актуальный список пользователей после записи
                        setUserList(safeReadList(PATH, User.class));
                    }
                    catch (Exception re) {
                        out("Ui/Main.java: Не удалось перечитать список пользователей: " + re.getMessage());
                    }

                    out("Ui/Main.java: Закончили процесс сохранения: " + getUser().toString());
                    clearRoot(getRoot());
                    setShop(new Shop());
                    getShop().shop(getRoot(), getUser());
                }
                catch (Exception ex) {
                    // Лог и пользовательское сообщение при общей ошибке сохранения.
                    out("Ui/Main.java: Ошибка при сохранении пользователя: " + ex.getMessage());
                    errMess(getRoot(), "Не удалось сохранить пользователя: " + ex.getMessage());
                }
            });

            // Обработчик кнопки "Назад" — восстанавливает сохранённый UI
            getButtonBack().setOnAction(_ -> {
                try {
                    out("Ui/Main.java: Возвращаемся");
                    getRoot().getChildren().setAll(getBackupNodeMain());
                }
                catch (Exception ex) {
                    out("Ui/Main.java: Ошибка при возврате из регистрации: " + ex.getMessage());
                    errMess(getRoot(), "Не удалось вернуться: " + ex.getMessage());
                }
            });

//            debugBordersFull(getTextName());
//            debugBordersFull(getTextPass());
//            debugBordersFull(getTextCash());
        }
        catch (Exception e) {
            out("Ui/Main.java: Ошибка экрана регистрации: " + e.getMessage());
            errMess(getRoot(), "Ошибка экрана регистрации: " + e.getMessage());
        }
    }
    // <------- Конец блока входа в аккаунт

    // Блок инициализации действий кнопки выйти. ------->
    /**
     * Показывает подтверждение выхода и закрывает приложение по нажатию «Да».
     *
     * Примечание: Platform.exit() вызывает завершение JavaFX-приложения. Любые исключения в этом
     * блоке логируются, но приложение пытается корректно завершиться.
     */
    public void exits() {
        try {
            Label label = makeLabel("Вы точно хотите выйти?");

            Button yes = new Button("Да");
            Button no = new Button("Нет");

            clearRoot(getRoot());

            getRoot().getChildren().addAll(label, yes, no);

            yes.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/PA.java: Закрываем приложение");
                    Platform.exit();
                }
                catch (Exception e) {
                    out("Ui/Main.java: Ошибка при закрытии: " + e.getMessage());
                    errMess(getRoot(), "Не удалось закрыть приложение: " + e.getMessage());
                }
            });

            no.setOnAction(_ -> {
                try {
                    out("Data/Cabinet/PA.java: Не закрываем приложение, возвращаемся в start");
                    getRoot().getChildren().setAll(getBackupNodeMain());
                }
                catch (Exception e) {
                    out("Ui/Main.java: Ошибка при возврате из выхода: " + e.getMessage());
                    errMess(getRoot(), "Не удалось вернуться: " + e.getMessage());
                }
            });
        }
        catch (Exception e) {
            out("Ui/Main.java: Ошибка окна выхода: " + e.getMessage());
            errMess(getRoot(), "Ошибка окна выхода: " + e.getMessage());
        }
    }
    // <------- Конец блока выхода

    /**
     * Традиционный вход: делегирует запуск JavaFX.
     * Добавлен перехват исключений на случай проблем запуска.
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        try {
            launch();
        }
        catch (Exception e) {
            out("Ui/Main.java: Критическая ошибка в main: " + e.getMessage());
            // На этом этапе UI может быть неинициализирован, поэтому только лог.
        }
    }
}
