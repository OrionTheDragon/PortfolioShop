package Shop;

import Data.Card;
import Data.Cart;
import Data.User;
import Data.Cabinet.PA;
import Shop.Categories.Goods;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Ui.Main.*;
import static Util.Util.*;

/**
 * Контур магазина: управляет вкладкой «Магазин», создаёт личный кабинет (PA) и запускает главное меню.
 */
public class Shop {

    // Картинки
    private Image imageMeat;
    private Image imageMilk;
    private Image imageVegetables;
    private Image imageBread;
    private Image imageGrocery;
    private Image imageDrinks;

    // Прочее
    /** Личный кабинет пользователя (инициализируется при входе в аккаунт). */
    private PA pa;

    // ТабПанель
    /** Главная панель вкладок магазина. Хранится статически для доступа из других классов. */
    public static TabPane tabPane = new TabPane();

    // Вкладки
    /** Вкладка «Магазин» с основным содержимым интерфейса покупок. */
    private Tab shopTab = new Tab("Магазин");

    // HBox
    private HBox lineOne = new HBox(10);
    private HBox lineTwo = new HBox(10);

    // VBox
    private VBox rootShop = new VBox(10);
    // Обёртка vbox
    private Parent ui = settingVBox(getRootShop());

    // Кнопки
    private Button meatButton = new Button();
    private Button milkButton = new Button();
    private Button vegetablesButton = new Button();
    private Button breadButton = new Button();
    private Button groceryButton = new Button();
    private Button drinksButton = new Button();

    // Категории
    private Category[] cats = Category.values();

    private Goods goods = new Goods();

    private Cart cart = new Cart();

    private static List<Node> backupNodesShop;

    public PA getPa() {
        return pa;
    }
    public void setPa(PA pa) {
        this.pa = pa;
    }
    public Tab getShopTab() {
        return shopTab;
    }
    public void setShopTab(Tab shopTab) {
        this.shopTab = shopTab;
    }
    public HBox getLineOne() {
        return lineOne;
    }
    public void setLineOne(HBox lineOne) {
        this.lineOne = lineOne;
    }
    public HBox getLineTwo() {
        return lineTwo;
    }
    public void setLineTwo(HBox lineTwo) {
        this.lineTwo = lineTwo;
    }
    public VBox getRootShop() {
        return rootShop;
    }
    public void setRootShop(VBox rootShop) {
        this.rootShop = rootShop;
    }
    public Button getMeatButton() {
        return meatButton;
    }
    public void setMeatButton(Button meatButton) {
        this.meatButton = meatButton;
    }
    public Button getMilkButton() {
        return milkButton;
    }
    public void setMilkButton(Button milkButton) {
        this.milkButton = milkButton;
    }
    public Button getVegetablesButton() {
        return vegetablesButton;
    }
    public void setVegetablesButton(Button vegetablesButton) {
        this.vegetablesButton = vegetablesButton;
    }
    public Button getBreadButton() {
        return breadButton;
    }
    public void setBreadButton(Button breadButton) {
        this.breadButton = breadButton;
    }
    public Button getGroceryButton() {
        return groceryButton;
    }
    public void setGroceryButton(Button groceryButton) {
        this.groceryButton = groceryButton;
    }
    public Button getDrinksButton() {
        return drinksButton;
    }
    public void setDrinksButton(Button drinksButton) {
        this.drinksButton = drinksButton;
    }
    public Image getImageMeat() {
        return imageMeat;
    }
    public void setImageMeat(Image imageMeat) {
        this.imageMeat = imageMeat;
    }
    public Image getImageMilk() {
        return imageMilk;
    }
    public void setImageMilk(Image imageMilk) {
        this.imageMilk = imageMilk;
    }
    public Image getImageVegetables() {
        return imageVegetables;
    }
    public void setImageVegetables(Image imageVegetables) {
        this.imageVegetables = imageVegetables;
    }
    public Image getImageBread() {
        return imageBread;
    }
    public void setImageBread(Image imageBread) {
        this.imageBread = imageBread;
    }
    public Image getImageGrocery() {
        return imageGrocery;
    }
    public void setImageGrocery(Image imageGrocery) {
        this.imageGrocery = imageGrocery;
    }
    public Image getImageDrinks() {
        return imageDrinks;
    }
    public void setImageDrinks(Image imageDrinks) {
        this.imageDrinks = imageDrinks;
    }
    public Parent getUi() {
        return ui;
    }
    public void setUi(Parent ui) {
        this.ui = ui;
    }
    public Category[] getCats() {
        return cats;
    }
    public void setCats(Category[] cats) {
        this.cats = cats;
    }
    public Goods getGoods() {
        return goods;
    }
    public void setGoods(Goods goods) {
        this.goods = goods;
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    public static List<Node> getBackupNodesShop() {
        return backupNodesShop;
    }
    public static void setBackupNodesShop(List<Node> backupNodesShop) {
        Shop.backupNodesShop = backupNodesShop;
    }

    public Shop() {
        // грузим картинки
        setImageMeat(loadImage("/Img/img_0000_Мясо.png", 150, 150, true, true));
        setImageMilk(loadImage("/Img/img_0001_Молоко&Яйца.png", 150, 150, true, true));
        setImageVegetables(loadImage("/Img/img_0002_Овощи&Фрукты.png", 150, 150, true, true));
        setImageBread(loadImage("/Img/img_0003_Х_Б_Изделия.png", 150, 150, true, true));
        setImageGrocery(loadImage("/Img/img_0004_Бакалея.png", 150, 150, true, true));
        setImageDrinks(loadImage("/Img/img_0005_Напитки.png", 150, 150, true, true));
    }

    /**
     * Инициализирует кнопки магазина по категориям: Мясо, Молоко, Овощи и фрукты, Х/Б изделия, Бакалея, Напитки. <br>
     * <br>
     * Использует кнопки: <br>
     * {@link #getMeatButton() Кнопка категории мясо} <br>
     * {@link #getMilkButton() Кнопка категории молочка} <br>
     * {@link #getVegetablesButton() Кнопка категории овощи и фрукты} <br>
     * {@link #getBreadButton() Кнопка категории Х/Б изделия} <br>
     * {@link #getGroceryButton() Кнопка категории бакалея} <br>
     * {@link #getDrinksButton() Кнопка категории напитки} <br>
     * <br>
     * Использует картинки: <br>
     * {@link #getImageMeat() Картинка категории мясо} <br>
     * {@link #getImageMilk() Картинка категории молочка} <br>
     * {@link #getImageVegetables() Картинка категории овощи и фрукты} <br>
     * {@link #getImageBread() Картинка категории Х/Б изделия} <br>
     * {@link #getImageGrocery() Картинка категории бакалея} <br>
     * {@link #getImageDrinks() Картинка категории напитки}
     */
    private void attachIconsToButtons(User user) {
        // Массив с кнопками
        Button[] arrButton = {getMeatButton(),
                getMilkButton(),
                getVegetablesButton(),
                getBreadButton(),
                getGroceryButton(),
                getDrinksButton()};

        // Массив с картинками
        Image[] arrImage = {getImageMeat(),
                getImageMilk(),
                getImageVegetables(),
                getImageBread(),
                getImageGrocery(),
                getImageDrinks()};

        /*
         * Добавление кнопкам картинку по категории,
         * если кнопка = null делаем отбивку в консоль
         */
        for (int i = 0; i < arrButton.length; i++) {
            Button b = arrButton[i];

            if (b == null) {
                out("Кнопка #" + i + " = null");
                continue;
            }

            b.setGraphic(new ImageView(arrImage[i]));
            final Category c = getCats()[i];
            b.setOnAction(e -> getGoods().selectedCategories(getRootShop(), c, user));
        }
    }

    public void showCategories(User user) {
        attachIconsToButtons(user);

        getLineOne().getChildren().setAll(getMeatButton(), getMilkButton(), getVegetablesButton());
        getLineTwo().getChildren().setAll(getBreadButton(), getGroceryButton(), getDrinksButton());

        getLineOne().setAlignment(Pos.TOP_CENTER);
        getLineTwo().setAlignment(Pos.TOP_CENTER);

        clearRoot(getRootShop());

        getRootShop().getChildren().addAll(getLineOne(), getLineTwo());

        setBackupNodesShop(backupNode(getRootShop()));
    }

    /**
     * Инициализирует UI магазина: очищает корень, добавляет {@link #tabPane}, создаёт PA и открывает главное меню. <br>
     * <br>
     * @param root корневой контейнер сцены <br>
     * @param user текущий пользователь
     */
    public void shop(VBox root, User user) {
        try {
            out("Shop/Shop.java: Вошли в shop");

            attachIconsToButtons(user);

            clearRoot(root);

            if (tabPane.getParent() != null && tabPane.getParent() != root) {
                ((Pane) tabPane.getParent()).getChildren().remove(tabPane);
            }
            if (!root.getChildren().contains(tabPane)) {
                root.getChildren().add(tabPane);
            }

            if (!tabPane.getTabs().contains(getShopTab())) {
                tabPane.getTabs().add(getShopTab());
            }

            getShopTab().setClosable(false);

            getLineOne().getChildren().clear();
            getLineTwo().getChildren().clear();
            getRootShop().getChildren().clear();

            showCategories(user);

            setUi(settingVBox(getRootShop()));
            getShopTab().setContent(getUi());

            createPA(user);
            getPa().mainMenu();

            getGoods().showGoods();
            getGoods().addingProductsToCategories();
        }
        catch (Exception e) {
            out("Shop/Shop.java: Ошибка в shop: " + (e.getMessage() == null ? e.toString() : e.getMessage()));
            try {
                errMess(root, "Ошибка открытия магазина: " + (e.getMessage() == null ? e.toString() : e.getMessage()));
            }
            catch (Exception ignored) {
                out("Shop/Shop.java: Непредвиденная ошибка в методе shop: " + ignored.toString());
            }
        }
    }

    /**
     * Создаёт/подцепляет личный кабинет {@link PA} для пользователя и загружает его карты. <br>
     * <p> <br>
     * Логика поиска: пытается найти пользователя с тем же userID в кеше ({@code getList()}). <br>
     * Если не найден — продолжает с переданным объектом, затем инициализирует PA. <br>
     * <br>
     * @param user пользователь, для которого создаётся PA <br>
     * @throws IllegalArgumentException если {@code user == null}
     */
    public void createPA(User user) {
        try {
            if (user == null) {
                out("Shop/Shop.java: " + user + " → некорректный вызов.");
                throw new IllegalArgumentException("Shop.createPA: user is null");
            }
            else {
                out("Shop/Shop.java: Вызов корректный, пользователь : №" + user.getUserID());
            }

            // ищем по indexValid/userID
            User fromStore = null;
            for (User u : getUserList()) {
                if (u.getUserID() == user.getUserID()) {
                    fromStore = u;
                    break;
                }
            }

            if (fromStore == null) {
                out("Shop/Shop.java: в файле не найден пользователь с indexValid=" + user.getUserID() + ", используем переданный объект.");
                fromStore = user;
            }

            for (User u : getUserList()) {
                if (u.getUserID() == user.getUserID()) {
                    setPa(new PA(u));
                    u.setPa(getPa());
                    u.loadCards(u);
                    getPa().setCard(u.getCard());
                    getPa().setTittle();
                    return;
                }
            }
            out("Shop/Shop.java: Создали ЛК");
        }
        catch (Exception e) {
            out("Shop/Shop.java: Ошибка в createPA: " + e.getMessage());
            throw e; // сохраняем исходное поведение
        }
    }

    public void placingAnOrder(VBox root, List<Goods> arrGoods, double allPrice, User u) {
        out("Shop/Categories/Goods.java: Вошли в placingAnOrder");

        List<Node> backupNodes = backupNode(root);

        final Cart cartRef = getShop().getCart();

        out(u.getName());

        clearRoot(root);

        ArrayList<Card> cards = u.getCard();

        ComboBox<Integer> comboBoxes = new ComboBox<>();

        ArrayList<Label> cardAllBalance = u.getPa().getCardBalance();
        Label cashBalance = new Label("Наличные средства: " + u.getCash());

        Button design = new Button("Заказать");
        Button cancel = new Button("Отменить");

        HBox DCHbox = new HBox(8);
        DCHbox.getChildren().addAll(design, cancel);
        DCHbox.setAlignment(Pos.TOP_CENTER);

        Label placingAnOrderLabel = new Label("Оформление заказа");
        Label separator = makeLabel("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        editLabelPA(placingAnOrderLabel);

        root.getChildren().addAll(placingAnOrderLabel, separator);

        for (Goods arrGood : arrGoods) {
            root.getChildren().add(makeLabel("Наименование позиции: " + arrGood.getProductName()));
        }

        for (Card c : cards) {
            
        }

        root.getChildren().addAll(makeLabel("Всего к оплате: " + allPrice + "₽"), DCHbox);

        cancel.setOnAction(_ -> {
            out("Нажали кнопку возврата в getInterfaceCart");
            out("getShop(): " + getShop().toString());
            out("getCart(): " + getShop().getCart().toString());
            root.getChildren().setAll(backupNodes);
        });
    }

    @Override
    public String toString() {
        return "Shop{" +
                "imageMeat=" + imageMeat +
                ", imageMilk=" + imageMilk +
                ", imageVegetables=" + imageVegetables +
                ", imageBread=" + imageBread +
                ", imageGrocery=" + imageGrocery +
                ", imageDrinks=" + imageDrinks +
                ", pa=" + pa +
                ", shopTab=" + shopTab +
                ", lineOne=" + lineOne +
                ", lineTwo=" + lineTwo +
                ", rootShop=" + rootShop +
                ", ui=" + ui +
                ", meatButton=" + meatButton +
                ", milkButton=" + milkButton +
                ", vegetablesButton=" + vegetablesButton +
                ", breadButton=" + breadButton +
                ", groceryButton=" + groceryButton +
                ", drinksButton=" + drinksButton +
                ", cats=" + Arrays.toString(cats) +
                ", goods=" + goods +
                ", cart=" + cart +
                '}';
    }
}