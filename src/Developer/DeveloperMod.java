package Developer;

import Data.Card;
import Data.User;
import Shop.Shop;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

import static Util.Util.*;
import static Shop.Shop.*;

/**
 * Экран «Режим разработчика» (DeveloperMod).
 * <p>
 * Дает возможность тестово редактировать:
 * <ul>
 *   <li>Наличные пользователя;</li>
 *   <li>Баланс на выбранной карте пользователя.</li>
 * </ul>
 * Во всех методах и обработчиках добавлены перехваты исключений.
 */
public class DeveloperMod {
    /** Вкладка режима разработчика (DevMode), добавляемая в общую TabPane. */
    private Tab devTab = new Tab("DevModeTab");

    /** Корневой контейнер для элементов интерфейса режима разработчика. */
    private VBox cheatRoot = new VBox(10);

    /** UI-содержимое вкладки разработчика — BorderPane с cheatRoot внутри. */
    private Parent ui = new BorderPane(settingVBox(getCheatRoot()));

    /** Метка для ввода и изменения количества наличных у пользователя. */
    private Label changeCashLabel = new Label("Изменить наличные");
    /** Метка для ввода и изменения суммы на выбранной карте. */
    private Label changeVirtualCashLabel = new Label("Изменить деньги на карте");

    /** Поле для ввода нового значения, наличных средств. */
    private TextArea changeCashText = new TextArea();
    /** Поле ввода нового значения виртуального баланса карты. */
    private TextArea changeVirtualCashText = new TextArea();

    /** Кнопка подтверждения изменения наличных средств. */
    private Button setCash = new Button("Принять");
    /** Кнопка подтверждения изменения виртуального баланса карты. */
    private Button setVirtualCash = new Button("Принять");

    public DeveloperMod() {}

    public Tab getDevTab() {
        return devTab;
    }
    public void setDevTab(Tab devTab) {
        this.devTab = devTab;
    }
    public Label getChangeCashLabel() {
        return changeCashLabel;
    }
    public void setChangeCashLabel(Label changeCashLabel) {
        this.changeCashLabel = changeCashLabel;
    }
    public Label getChangeVirtualCashLabel() {
        return changeVirtualCashLabel;
    }
    public void setChangeVirtualCashLabel(Label changeVirtualCashLabel) {
        this.changeVirtualCashLabel = changeVirtualCashLabel;
    }
    public TextArea getChangeCashText() {
        return changeCashText;
    }
    public void setChangeCashText(TextArea changeCashText) {
        this.changeCashText = changeCashText;
    }
    public TextArea getChangeVirtualCashText() {
        return changeVirtualCashText;
    }
    public void setChangeVirtualCashText(TextArea changeVirtualCashText) {
        this.changeVirtualCashText = changeVirtualCashText;
    }
    public Button getSetCash() {
        return setCash;
    }
    public void setSetCash(Button setCash) {
        this.setCash = setCash;
    }
    public Button getSetVirtualCash() {
        return setVirtualCash;
    }
    public void setSetVirtualCash(Button setVirtualCash) {
        this.setVirtualCash = setVirtualCash;
    }
    public VBox getCheatRoot() {
        return cheatRoot;
    }
    public void setCheatRoot(VBox cheatRoot) {
        this.cheatRoot = cheatRoot;
    }
    public Parent getUi() {
        return ui;
    }
    public void setUi(Parent ui) {
        this.ui = ui;
    }

    /**
     * Обеспечивает наличие вкладки DevModeTab в {@link Shop#getShopTab() getShopTab} и устанавливает в неё UI.
     */
    public void getTabPane() {
        try {
            tabPane.getTabs().addAll(getDevTab());
            getDevTab().setContent(getUi());
        }
        catch (Exception e) {
            out("Developer/DeveloperMod.java: Ошибка в getTabPane: " + e.getMessage());
        }
    }

    /**
     * Разворачивает блок изменения наличных пользователя.
     * @param user текущий пользователь
     */
    public void cheats(User user) {
        try {
            if (!tabPane.getTabs().contains(getDevTab())) {
                getTabPane();
            }

            getCheatRoot().getChildren().addAll(getChangeCashLabel(),
                    getChangeCashText(),
                    getSetCash());

            getChangeCashText().setPrefSize(250, 20);
            getChangeCashText().setWrapText(true);

            VBox.setVgrow(getChangeCashText(), Priority.NEVER);

            getSetCash().setOnAction(_ -> {
                try {
                    double douCash = Double.valueOf(getChangeCashText().getText().trim());
                    user.setCash(douCash);
                    out("Developer/DeveloperMod.java: Наличные изменены");
                }
                catch (Exception ex) {
                    out("Developer/DeveloperMod.java: Ошибка парсинга наличных: " + ex.getMessage());
                    errMess(getCheatRoot(), "Нужно число (пример: 1234.56)");
                }
            });
        }
        catch (Exception e) {
            out("Developer/DeveloperMod.java: Ошибка в cheats(User): " + e.getMessage());
        }
    }

    /**
     * Разворачивает блок изменения баланса на карте пользователя.
     *
     * @param card список карт (из ЛК)
     * @param user текущий пользователь
     */
    public void cheats(ArrayList<Card> card, User user) {
        try {
            if (!tabPane.getTabs().contains(getDevTab())) {
                getTabPane();
            }

            ComboBox<Integer> cardComboBox = new ComboBox<>();
            ArrayList<Integer> mapToCardIndex = new ArrayList<>();

            for (int i = 0; i < card.size(); i++) {
                if (card.get(i).getUserID() == user.getUserID()) {
                    mapToCardIndex.add(i);
                    cardComboBox.getItems().add(mapToCardIndex.size());
                }
            }

            cardComboBox.setVisibleRowCount(Math.max(3, Math.min(10, cardComboBox.getItems().size())));

            getCheatRoot().getChildren().addAll(
                    getChangeVirtualCashLabel(),
                    cardComboBox,
                    getChangeVirtualCashText(),
                    getSetVirtualCash()
            );

            getChangeVirtualCashText().setPrefSize(250, 20);
            getChangeVirtualCashText().setWrapText(true);
            VBox.setVgrow(getChangeVirtualCashText(), Priority.NEVER);

            getSetVirtualCash().setOnAction(_ -> {
                try {
                    Integer chosen = cardComboBox.getValue();
                    if (chosen == null) {
                        errMess(getCheatRoot(), "Выбери карту");
                        return;
                    }

                    double newCash;
                    try {
                        String raw = getChangeVirtualCashText().getText().trim().replace(',', '.');
                        newCash = Double.parseDouble(raw);
                    }
                    catch (Exception ex) {
                        errMess(getCheatRoot(), "Нужно число (пример: 1234.56)");
                        return;
                    }

                    int realIdx = mapToCardIndex.get(chosen - 1);
                    card.get(realIdx).setVirtualCash(newCash);

                    out("Developer/DeveloperMod.java: Деньги на карте №" + chosen + " изменены на " + newCash);
                }
                catch (Exception e) {
                    out("Developer/DeveloperMod.java: Ошибка применения нового баланса карты: " + e.getMessage());
                    errMess(getCheatRoot(), "Не удалось изменить баланс карты: " + e.getMessage());
                }
            });

            boolean hasAny = !cardComboBox.getItems().isEmpty();
            cardComboBox.setDisable(!hasAny);
            getChangeVirtualCashText().setDisable(!hasAny);
            getSetVirtualCash().setDisable(!hasAny);
            if (!hasAny) {
                errMess(getCheatRoot(), "У пользователя нет карт");
            }
        }
        catch (Exception e) {
            out("Developer/DeveloperMod.java: Ошибка в cheats(List<Card>, User): " + e.getMessage());
        }
    }
}
