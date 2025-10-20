package Data;

import Shop.Categories.Goods;
import Ui.Main;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static Shop.Shop.tabPane;
import static Util.Util.*;

@JsonIgnoreProperties({"vBoxCart", "ui", "cartTab", "file"})
public class Cart {
    @JsonIgnore
    public static final String PATH_CART = "Cart.json";
    @JsonIgnore
    private VBox vBoxCart = new VBox(10);
    @JsonIgnore
    private Parent ui;
    @JsonIgnore
    private Tab cartTab;

    private int userID;

    @JsonIgnore
    private double allPrice = 0;

    @JsonIgnore
    private Goods goods;

    @JsonIgnore
    private List<Goods> goodsList = new ArrayList<>();

    private Map<String, Integer> qtyByName = new HashMap<>();

    public Cart() {}

    public Cart(int userID) {
        setUserID(userID);
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    @JsonIgnore
    public List<Goods> getGoodsList() {
        return goodsList;
    }
    @JsonIgnore
    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }
    @JsonIgnore
    public VBox getVBoxCart() {
        return vBoxCart;
    }
    public void setVBoxCart(VBox vBoxCart) {
        this.vBoxCart = vBoxCart;
    }
    @JsonIgnore
    public Parent getUi() {
        return ui;
    }
    public void setUi(Parent ui) {
        this.ui = ui;
    }
    @JsonIgnore
    public Tab getCartTab() {
        if (cartTab == null) {
            cartTab = new Tab("Корзина");
        }
        return cartTab;
    }
    public void setCartTab(Tab cartTab) {
        this.cartTab = cartTab;
    }
    public Goods getGoods() {
        return goods;
    }
    public void setGoods(Goods goods) {
        this.goods = goods;
    }
    public Map<String, Integer> getQtyByName() {
        return qtyByName;
    }
    public void setQtyByName(Map<String, Integer> qtyByName) {
        this.qtyByName = qtyByName;
    }
    public double getAllPrice() {
        return allPrice;
    }
    public void setAllPrice(double allPrice) {
        this.allPrice = allPrice;
    }

    public void loadCart(User u) {
        File file = new File(PATH_CART);
        boolean notEmpty = file.isFile() && file.length() > 0L;

        if (!notEmpty) {
            out("Data/Cart.java: Файл пустой");
            return;
        }

        List<Cart> carts = safeReadList(PATH_CART, Cart.class);
        if (carts == null || carts.isEmpty()) {
            out("Data/Cart.java: В файле нет корзин");
            return;
        }

        out("Data/Cart.java: Идёт загрузка корзины пользователя");

        for (Cart c : carts) {
            if (c.getUserID() == u.getUserID()) {
                setQtyByName(new HashMap<>(c.getQtyByName()));
                setUserID(c.getUserID());
                out("Data/Cart.java: Товары: " + getQtyByName());
                out("Data/Cart.java: ID Пользователя: " + getUserID());

                // Загружаем товары по SKU
                upsert();

                return;
            }
        }

        out("Data/Cart.java: Для пользователя " + u.getUserID() + " корзина не найдена");
    }

    public int qtyOf(Goods g) {
        return getQtyByName().getOrDefault(g.getSKU(), 0);
    }

    public void upsert(Goods g, int qty) {
        String key = g.getSKU();

        if (qty <= 0) {
            getQtyByName().remove(key);
            getGoodsList().removeIf(x -> Objects.equals(x.getSKU(), key));
            return;
        }

        getQtyByName().put(key, qty);

        boolean exists = getGoodsList().stream()
                .anyMatch(x -> Objects.equals(x.getSKU(), key));
        if (!exists) {
            getGoodsList().add(g);
        }
    }

    public void upsert() {
        Map<String, Integer> qtyMap = getQtyByName();
        if (qtyMap == null || qtyMap.isEmpty()) {
            out("Data/Cart.java: Нет SKU для загрузки товаров");
            return;
        }

        List<Goods> allGoods = Goods.getAllArrGoods();
        if (allGoods == null || allGoods.isEmpty()) {
            out("Data/Cart.java: Список всех товаров пуст");
            return;
        }

        getGoodsList().clear(); // очищаем старое

        // Проходим по SKU из корзины
        for (String sku : qtyMap.keySet()) {
            for (Goods g : allGoods) {
                if (g.getSKU().equals(sku)) {
                    getGoodsList().add(g);
                    break; // когда нашли нужный товар — выходим из внутреннего цикла
                }
            }
        }

        out("Data/Cart.java: Загружено товаров по SKU: " + getGoodsList().size());
    }

    public void getInterfaceCart(User u) {
        out("cartTab == null? " + (getCartTab() == null));
        out("tabPane содержит cartTab? " + tabPane.getTabs().contains(getCartTab()));
        out("getCartTab().getContent() = " + getCartTab().getContent());
        out("getCartTab().getContent() instanceOf VBox? " + (getCartTab().getContent() instanceof VBox));

        out("getCartTab().getText(): " + getCartTab().getText());

        tabPane.getTabs().add(getCartTab());
        out("tabPane.getTabs().getLast().getText(): " + tabPane.getTabs().getLast().getText());

        setUi(settingVBox(getVBoxCart()));

        out("UI: " + getUi());

        getCartTab().setContent(getUi());

        out("getCartTab().getContent() = " + getCartTab().getContent());

        primaryFields(u);
    }

    public void primaryFields(User u) {
        clearRoot(getVBoxCart());
        loadCart(u);
        ArrayList<Label> cardB = new ArrayList<>();

        int countCard = 0;
        for (Label l : u.getPa().getCardBalance()) {
            cardB.set(countCard, new Label(l.getText()));
            editLabelPA(cardB.get(countCard));
            countCard++;
        }

        Label separator = makeLabel("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Label cashB = new Label("Наличные средства: " + u.getCash());
        editLabelPA(cashB);

        getVBoxCart().setAlignment(Pos.TOP_LEFT);

        getVBoxCart().getChildren().addAll(cardB);
        getVBoxCart().getChildren().addAll(cashB, separator);

        if (getGoodsList() != null && !getGoodsList().isEmpty()) {
            out("Data/Cart.java: Инициализируем продукты в корзине");
            interfaceCart(u);
        }
        else {
            out("Data/Cart.java: Продуктов в корзине нет");
        }
    }

    public void interfaceCart(User u) {
        Stage stage = new Stage();
        VBox delVbox = new VBox(10);

        Scene scene = new Scene(delVbox, 0, 0);

        setAllPrice(0);
        Button buy = new Button("Оформить заказ");
        Label labelAllPrice = new Label();
        editLabelPA(labelAllPrice);

        if (!getGoodsList().isEmpty()) {
            setGoods(getGoodsList().get(0));
        }

        for (Goods g : getGoodsList()) {
            int q = qtyOf(g);
            out("Data/Cart.java: Товар: " + g.getProductName() + "Кол-во в корзине: " + q);
            AtomicReference<Double> d = new AtomicReference<>(g.pieceGoods(g, q));
            out("Data/Cart.java: AtomicReference d: " + d.get());
            Image i = loadImage("/Img/img_0006_Корзина.png", 20, 20, true, true);

            setAllPrice(getAllPrice() + d.get());
            out("Data/Cart.java: allPrice: " + getAllPrice());

            labelAllPrice.setText("Всего к оплате: " +  String.format("%.2f", getAllPrice()) + " рублей.");

            Button del = new Button();
            del.setGraphic(new ImageView(i));

            HBox hBox = new HBox(8);
            hBox.setAlignment(Pos.TOP_LEFT);

            if (q <= 0) {
                continue;
            }

            Label gName = makeLabel("Товар: " + g.getProductName());
            hBox.getChildren().addAll(gName, del);

            Label gQty = makeLabel("Кол-во: " + q + (g.getType().equals("Штучный") ? " шт." : " гр."));
            Label gManufacturer = new Label("Производитель: " + g.getManufacturer());
            editLabelPA(gManufacturer);
            Label gCountry = new Label("Страна производителя: " + g.getCountry());
            editLabelPA(gCountry);
            Label gPrice = makeLabel("Цена: " + (g.getType().equals("Штучный") ? g.getPrice() + "₽ за шт." : g.getPrice() * 1000 + "₽ за кг."));
            Label separator = makeLabel("--------------------");

            getVBoxCart().getChildren().addAll(hBox, gQty, gPrice, gManufacturer, gCountry, separator);

            del.setOnAction(_ -> {
                Button yes = new Button("Да");
                Button no = new Button("Нет");

                out("Data/Cart.java: Окно удаления: " + stage.isShowing());

                int verStage = 120;
                if (!stage.isShowing()) {
                    HBox YNHbox = new HBox(8);
                    YNHbox.setAlignment(Pos.TOP_CENTER);
                    YNHbox.getChildren().addAll(yes, no);

                    Label delVboxLabel = makeLabel("Вы действительно хотите удалить товар из корзины: " + g.getProductName() + "?");

                    int horStage = delVboxLabel.getText().length() * 10;

                    delVbox.getChildren().setAll(delVboxLabel, YNHbox);
                    delVbox.setAlignment(Pos.TOP_CENTER);

                    stage.setWidth(horStage);
                    stage.setHeight(verStage);

                    stage.setScene(scene);
                    stage.setAlwaysOnTop(true);
                    stage.setResizable(false);
                    stage.setTitle("Удаление товара: " + g.getProductName());
                    stage.show();
                }
                else {
                    errMess(delVbox, "Закройте текущее окно");
                    stage.setHeight(verStage * 1.2);
                }

                yes.setOnAction(_ -> {
                    d.set(g.pieceGoods(g, q));
                    setAllPrice(getAllPrice() - d.get());
                    out("Data/Cart.java: allPrice: " + getAllPrice());
                    labelAllPrice.setText("Всего к оплате: " + String.format("%.2f", getAllPrice()) + "₽.");
                    getVBoxCart().getChildren().removeAll(hBox, gQty, gPrice, gManufacturer, gCountry, separator);

                    upsert(g, 0);
                    try {
                        saveToFile();
                        delVbox.getChildren().clear();
                        stage.close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                no.setOnAction(_ -> {
                    delVbox.getChildren().clear();
                    stage.close();
                });
            });
        }

        // Для отладки
        for (Goods gg : getGoodsList()) {
            out("Data/Cart.java: Товар в корзине: " + gg.getProductName() + "\nSKU: " + gg.getSKU());
        }

        buy.setOnAction(_ -> {
            out("Data/Cart.java: Нажали кнопку Оформления заказа");
            out("Data/Cart.java: getVBoxCart(): " + getVBoxCart().toString());
            out("Data/Cart.java: getGoodsList(): " + getGoodsList().toString());
            out("Data/Cart.java:  getAllPrice(): " + getAllPrice());
            Main.getShop().placingAnOrder(getVBoxCart(), getGoodsList(), getAllPrice(), u);
        });

        getVBoxCart().getChildren().addLast(labelAllPrice);
        getVBoxCart().getChildren().addLast(buy);
    }

    public void saveToFile() throws IOException {
        File f = new File(PATH_CART);

        List<Cart> carts = f.isFile()
                ? safeReadList(PATH_CART, Cart.class)
                : new ArrayList<>();

        if (carts == null) carts = new ArrayList<>();

        // легковесная копия: сохраняем только userID и qtyByName
        Cart snapshot = new Cart(getUserID());
        snapshot.setQtyByName(new HashMap<>(getQtyByName()));
//        snapshot.loadCart();

        // upsert по userID
        int idx = -1;
        for (int i = 0; i < carts.size(); i++) {
            if (carts.get(i).getUserID() == getUserID()) { idx = i; break; }
        }
        if (idx >= 0) carts.set(idx, snapshot);
        else carts.add(snapshot);

        // перезаписываем файл целиком
        new com.fasterxml.jackson.databind.ObjectMapper()
                .findAndRegisterModules()
                .writerWithDefaultPrettyPrinter()
                .writeValue(f, carts);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "vBoxCart=" + vBoxCart +
                ", ui=" + ui +
                ", cartTab=" + cartTab +
                ", userID=" + userID +
                ", allPrice=" + allPrice +
                ", goods=" + goods +
                ", goodsList=" + goodsList +
                ", qtyByName=" + qtyByName +
                '}';
    }
}
