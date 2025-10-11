package Data.Cabinet;

import Developer.*;
import Data.Card;
import Data.User;
import Shop.Shop;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

import static Shop.Shop.tabPane;
import static Ui.Main.*;
import static Util.Util.*;

/**
 * Личный кабинет (PA) пользователя.
 * <p>
 * Отвечает за главное меню ЛК, отображение балансов наличных и карт,
 * навигацию в настройки, выход из аккаунта и периодическое обновление отображения.
 * В методах и обработчиках добавлены перехваты исключений для устойчивости UI.
 */
public class PA { // ЛК
    /** Корневой VBox для интерфейса личного кабинета (главное меню ЛК). */
    private VBox rootMainMenu = new VBox(10);
    /** Контейнер для списка карт и их балансов внутри ЛК. */
    private VBox cardVBox = new VBox(10);

    /** Флаг: находится ли пользователь сейчас в главном меню ЛК. Используется для управления логикой обновлений. */
    private boolean inMainMenu = false;
    /** Флаг: выполняется ли поток (timeline) обновления балансов — чтобы не запускать несколько экземпляров. */
    private boolean flowIsItWorking = false;

    /** JavaFX Timeline, периодически обновляющий отображение балансов наличных и карт. */
    private Timeline timeline = new Timeline();

    // Параметры ЛК
    /** Текущий пользователь, для которого открыт ЛК. */
    private User user;
    /** Список карт, связанных с текущим пользователем (локальная копия в ЛК). */
    private ArrayList<Card> card = new ArrayList<>();
    /** Меню настроек (SettingsMenu) для данного ЛК. */
    private SettingsMenu settingsMenu = new SettingsMenu();

    /** UI-контейнер для вкладки ЛК — BorderPane, внутри которого расположен оформленный VBox. */
    private Parent ui = new BorderPane(settingVBox(getRootMainMenu()));

    // Кнопки
    /** Кнопка открытия истории покупок пользователя. */
    private Button purchaseHistory = new Button("История покупок");
    /** Кнопка открытия меню настроек ЛК. */
    private Button settings = new Button("Настройки");
    /** Кнопка выхода из аккаунта (возврат в общее главное меню приложения). */
    private Button exit = new Button("Выйти из аккаунта");
    private Button openCart = new Button("Корзина");

    // Этикетки
    /** Метка для отображения имени пользователя в заголовке ЛК. */
    private Label name;
    /** Метка для отображения возраста пользователя в заголовке ЛК. */
    private Label age;
    /** Метка для отображения текущего наличного баланса пользователя. */
    private Label cashBalance;
    /** Список меток — по одной на каждую карту, отображающих баланс карты. */
    private ArrayList<Label> cardBalance = new ArrayList<>();

    // Вкладка(и)
    /** Вкладка «Личный кабинет», добавляемая в общую TabPane приложения. */
    private Tab personalAccount = new Tab("Личный кабинет");


    /**
     * Конструктор по умолчанию.
     */
    public PA() {}

    /**
     * Конструктор, подготавливающий базовые поля (имя, возраст, баланс) из переданного пользователя.
     * @param user пользователь, для которого создаётся ЛК
     */
    public PA(User user) {
        try {
            setUser(user);

            for (User u : getUserList()) {
                if (u.getUserID() == getUser().getUserID()) {
                    setName(new Label(getUser().getName()));
                    setAge(new Label(Byte.toString(getUser().getAge())));
                    setCashBalance(new Label(Double.toString(getUser().getCash())));
                    return;
                }
            }
            out("Data/Cabinet/PA.java: ЛК создан");
        }
        catch (Exception e) {
            out("Data/Cabinet/PA.java: Ошибка в конструкторе PA(User): " + e.getMessage());
        }
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public ArrayList<Card> getCard() {
        return card;
    }
    public void setCard(ArrayList<Card> card) {
        this.card.clear();
        if (card != null) this.card.addAll(card);
    }
    public Button getPurchaseHistory() {
        return purchaseHistory;
    }
    public void setPurchaseHistory(Button purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }
    public Button getSettings() {
        return settings;
    }
    public void setSettings(Button settings) {
        this.settings = settings;
    }
    public Label getName() {
        return name;
    }
    public void setName(Label name) {
        this.name = name;
    }
    public Label getAge() {
        return age;
    }
    public void setAge(Label age) {
        this.age = age;
    }
    public Label getCashBalance() {
        return cashBalance;
    }
    public void setCashBalance(Label cashBalance) {
        this.cashBalance = cashBalance;
    }
    public ArrayList<Label> getCardBalance() {
        return cardBalance;
    }
    public void setCardBalance(ArrayList<Label> cardBalance) {
        this.cardBalance = cardBalance;
    }
    public Tab getPersonalAccount() {
        return personalAccount;
    }
    public void setPersonalAccount(Tab personalAccount) {
        this.personalAccount = personalAccount;
    }
    public boolean isFlowIsItWorking() {
        return flowIsItWorking;
    }
    public void setFlowIsItWorking(boolean flowIsItWorking) {
        this.flowIsItWorking = flowIsItWorking;
    }
    public boolean isInMainMenu() {
        return inMainMenu;
    }
    public void setInMainMenu(boolean inMainMenu) {
        this.inMainMenu = inMainMenu;
    }
    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }
    public void setSettingsMenu(SettingsMenu settingsMenu) {
        this.settingsMenu = settingsMenu;
    }
    public Timeline getTimeline() {
        return timeline;
    }
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
    public Button getExit() {
        return exit;
    }
    public void setExit(Button exit) {
        this.exit = exit;
    }
    public VBox getRootMainMenu() {
        return rootMainMenu;
    }
    public void setRootMainMenu(VBox rootMainMenu) {
        this.rootMainMenu = rootMainMenu;
    }
    public Parent getUi() {
        return ui;
    }
    public void setUi(Parent ui) {
        this.ui = ui;
    }
    public VBox getCardVBox() {
        return cardVBox;
    }
    public void setCardVBox(VBox cardVBox) {
        this.cardVBox = cardVBox;
    }
    public Button getOpenCart() {
        return openCart;
    }
    public void setOpenCart(Button openCart) {
        this.openCart = openCart;
    }

    /**
     * Инициализирует заголовочные поля ЛК и перестраивает блок с картами.
     */
    public void setTittle() {
        try {
            editLabelPA(getName());
            editLabelPA(getAge());
            editLabelPA(getCashBalance());

            out("Data/Cabinet/PA.java: Создаются первичные поля");

            getName().setText("Имя: " + getUser().getName());
            getAge().setText("Возраст: " + getUser().getAge());
            getCashBalance().setText("Наличные средства: " + getUser().getCash());

            rebuildCardsBox();
        }
        catch (Exception e) {
            out("Data/Cabinet/PA.java: Ошибка в setTittle: " + e.getMessage());
        }
    }

    /**
     * Строит главное меню ЛК, навешивает обработчики, подключает вкладку в {@link Shop#getShopTab() getShopTab}.
     */
    public void mainMenu() {
        try {
            out("Data/Cabinet/PA.java: Вошли в mainMenu ЛК");
            Button chetMenu = new Button("Режим разработчика");

            clearRoot(getRootMainMenu());
            setInMainMenu(true);

            getCardVBox().setAlignment(Pos.CENTER);

            getRootMainMenu().getChildren().addAll(getName(),
                    getAge(),
                    getCashBalance(),
                    getCardVBox(),
                    getOpenCart(),
                    getPurchaseHistory(),
                    getSettings(),
                    getExit(),
                    chetMenu);

            if (!isFlowIsItWorking() ) {
                balanceMoneyAll();
            }

            getOpenCart().setOnAction(_ -> {
                getUser().getItemsInCart().getInterfaceCart(getUser());
            });

            getSettings().setOnAction(_ -> {
                try {
                    setInMainMenu(false);
                    out("Data/Cabinet/PA.java: Входим в settingsMenu");
                    getSettingsMenu().settingsMenu(getRootMainMenu(), getPersonalAccount(), getUser().getPa());
                }
                catch (Exception e) {
                    out("Data/Cabinet/PA.java: Ошибка открытия settingsMenu: " + e.getMessage());
                }
            });

            chetMenu.setOnAction(_ -> {
                try {
                    DeveloperMod developerMod = new DeveloperMod();
                    out("Data/Cabinet/PA.java: Входим в developerMod");

                    if (getUser().getCash() != 0) {
                        developerMod.cheats(getUser());
                    }
                    if (getCard() != null) {
                        developerMod.cheats(getCard(), getUser());
                    }
                }
                catch (Exception e) {
                    out("Data/Cabinet/PA.java: Ошибка в developerMod: " + e.getMessage());
                }
            });

            getExit().setOnAction(_ -> {
                try {
                    setInMainMenu(false);
                    Label label = makeLabel("Вы точно хотите выйти из аккаунта?");

                    Button yes = new Button("Да");
                    Button no = new Button("Нет");

                    clearRoot(getRootMainMenu());

                    getRootMainMenu().getChildren().addAll(label, yes, no);

                    yes.setOnAction(__ -> {
                        try {
                            getTimeline().stop();
                            out("Data/Cabinet/PA.java: Выходим в общее главное меню");
                            INSTANCE.menuLog();
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/PA.java: Ошибка выхода в главное меню: " + e.getMessage());
                        }
                    });

                    no.setOnAction(__ -> {
                        try {
                            out("Data/Cabinet/PA.java: Не выходим, возвращаемся в mainMenu");
                            mainMenu();
                        }
                        catch (Exception e) {
                            out("Data/Cabinet/PA.java: Ошибка возврата в mainMenu: " + e.getMessage());
                        }
                    });
                }
                catch (Exception e) {
                    out("Data/Cabinet/PA.java: Ошибка подготовки окна выхода из аккаунта: " + e.getMessage());
                }
            });

            if (!tabPane.getTabs().contains(getPersonalAccount())) {
                try {
                    tabPane.getTabs().add(getPersonalAccount());
                }
                catch (Exception e) {
                    out("Data/Cabinet/PA.java: Ошибка добавления вкладки ЛК: " + e.getMessage());
                }
            }
            if (getPersonalAccount().getContent() != getUi()) {
                try {
                    getPersonalAccount().setContent(getUi());
                }
                catch (Exception e) {
                    out("Data/Cabinet/PA.java: Ошибка установки контента вкладки ЛК: " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            out("Data/Cabinet/PA.java: Ошибка в mainMenu: " + e.getMessage());
        }
    }

    /**
     * Перестраивает блок с балансами карт, сортируя карты по {@code indexValid} и выводя
     * только карты текущего пользователя.
     */
    public void rebuildCardsBox() {
        try {
            if (!javafx.application.Platform.isFxApplicationThread()) {
                javafx.application.Platform.runLater(this::rebuildCardsBox);
                return;
            }

            List<Card> cards = getCard();
            getCardVBox().getChildren().clear();

            if (getCard() == null || getCard().isEmpty()) {
                out("Data/Cabinet/PA.java: Карт нет");
                return;
            }

            int uid = getUser().getUserID();

            try {
                cards.sort((a, b) -> {
                    // если indexValid окажется null
                    int ia = (a == null) ? 0 : a.getIndexValid();
                    int ib = (b == null) ? 0 : b.getIndexValid();
                    return Integer.compare(ia, ib);
                });
            }
            catch (Exception sortEx) {
                out("Data/Cabinet/PA.java: Ошибка сортировки: " + sortEx);
            }

            getCardBalance().clear();

            int j = 0;
            for (int i = 0; i < cards.size(); i++) {
                Card c = cards.get(i);
                if (c == null) {
                    continue;
                }
                if (c.getUserID() != uid) {
                    continue;
                }

                out("Data/Cabinet/PA.java: Карта: " + c.getUserID() + ", " + c.getIndexValid());
                Label lb = new Label("Баланс на карте №" + (j + 1) + ": " + c.getVirtualCash());
                editLabelPA(lb);
                getCardBalance().add(lb);
                j++;
            }

            if (j == 0) {
                out("Data/Cabinet/PA.java: Карт для пользователя " + uid + " нет");
                return;
            }

            getCardVBox().getChildren().setAll(getCardBalance());
        }
        catch (Exception e) {
            e.printStackTrace();
            out("Data/Cabinet/PA.java: Ошибка в rebuildCardsBox: " + e);
        }
    }

    /**
     * Запускает периодическое (каждые 10 секунд) обновление видимых балансов наличных и карт.
     */
    public void balanceMoneyAll() {
        try {
            setFlowIsItWorking(true);
            getTimeline().getKeyFrames().setAll(new KeyFrame(Duration.seconds(10), event -> {
                try {
                    boolean shouldCardUpdate = getCard() != null && getUser() != null && isInMainMenu();
                    boolean shouldCashUpdate = getUser().getCash() > 0 && getUser() != null && isInMainMenu();

                    if (!isInMainMenu()) {
                        out("Data/Cabinet/PA.java: Не в главном окне ЛК!");
                        getTimeline().stop();
                        out("Data/Cabinet/PA.java: Остановка");
                        setFlowIsItWorking(false);
                        return;
                    }

                    out("Data/Cabinet/PA.java: Проверка наличия наличности: " + shouldCashUpdate);
                    out("Data/Cabinet/PA.java: Проверка наличия карт: " + shouldCardUpdate);

                    if (shouldCashUpdate) {
                        Platform.runLater(() -> {
                            try {
                                getCashBalance().setText("Наличные средства: " + getUser().getCash());
                                out("Data/Cabinet/PA.java: Обновили текст баланса наличности");
                            }
                            catch (Exception e1) {
                                out("Data/Cabinet/PA.java: Ошибка обновления наличных: " + e1.getMessage());
                            }
                        });
                    }

                    if (shouldCardUpdate) {
                        Platform.runLater(() -> {
                            try {
                                for (int i = 0; i < getCardBalance().size(); i++) {
                                    if (getCard().get(i).getUserID() == getUser().getUserID()) {
                                        getCardBalance().get(i).setText("Баланс на карте №" + (i + 1) + ": " + getCard().get(i).getVirtualCash());
                                        out("Data/Cabinet/PA.java: Обновили текст баланса карты: " + getCard().get(i).getOwner() +
                                                ", ID Карты: " + getCard().get(i).getIndexValid() + ", ID Держателя карты: " + getCard().get(i).getUserID());
                                    }
                                }
                            }
                            catch (Exception e2) {
                                out("Data/Cabinet/PA.java: Ошибка обновления балансов карт: " + e2.getMessage());
                            }
                        });
                    }
                }
                catch (Exception e0) {
                    out("Data/Cabinet/PA.java: Ошибка tick Timeline: " + e0.getMessage());
                }
            }));

            getTimeline().setCycleCount(Timeline.INDEFINITE);
            getTimeline().setDelay(Duration.seconds(0)); // запускаем сразу
            getTimeline().play();
        }
        catch (Exception e) {
            out("Data/Cabinet/PA.java: Ошибка запуска balanceMoneyAll: " + e.getMessage());
        }
    }
}