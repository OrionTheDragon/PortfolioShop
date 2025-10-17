package Shop.Categories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import Data.Cart;
import Data.User;
import Shop.Category;
import Shop.Shop;
import Ui.DownloadBar;
import com.fasterxml.jackson.annotation.*;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;

import static Ui.Main.getShop;
import static Util.Util.*;

@JsonPropertyOrder({
        "SKU",
        "productName",
        "manufacturer",
        "country",
        "categories",
        "subCategories",
        "type",
        "price",
        "quantity"
})

@JsonIgnoreProperties({"allArrGoods", "shop", "back"})
public class Goods {
    // Продукты
    public static final String GOODS_PATH = "Goods.json";

    @JsonIgnore
    public static final String URL = "https://orionium.stardisk.xyz/Goods.json";

    // Параметры товаров
    @JsonIgnore
    private String SKU;
    private String productName;
    private String manufacturer;
    private String country;
    private Category categories;
    private SubCategory subCategories;
    private String type;
    private double price;
    private int quantity;

    //Прочее
    @JsonIgnore
    private static double pry = 0;

    // Кнопки
    @JsonIgnore
    private Button back = new Button("Назад");

    @JsonIgnore
    private static List<Goods> allArrGoods;

    @JsonIgnore
    private static List<Node> backupNodesGoods;



    static {
        File file = new File(GOODS_PATH);

        if (file.isFile() && file.length() > 0) {
            List<Goods> temp = safeReadList(GOODS_PATH, Goods.class);

            if (temp != null) {
                allArrGoods = temp;
                out("Shop/Categories/Goods.java: Локальный файл загружен (" + allArrGoods.size() + " товаров)");
            }
            else {
                allArrGoods = new ArrayList<>();
                out("Shop/Categories/Goods.java: Ошибка чтения локального файла, список пуст");
            }
        }
        else {
            out("Shop/Categories/Goods.java: Локальный файл не найден или пуст — грузим из интернета");
            List<Goods> temp = safeReadList(URL, Goods.class);
            allArrGoods = temp != null ? temp : new ArrayList<>();
        }
    }

    @JsonIgnore
    private HashMap<Category, HashMap<SubCategory, Goods[]>> goodList = new HashMap<>();

    private static final ObjectMapper M = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public Goods() {/*out("Shop/Categories/Goods.java: Загрузили Goods");*/}

    public Goods(String SKU,
                 String productName,
                 String manufacturer,
                 String country,
                 Category categories,
                 SubCategory subCategories,
                 String type,
                 double price,
                 int quantity) {
        setSKU(SKU);
        setProductName(productName);
        setManufacturer(manufacturer);
        setCountry(country);
        setCategories(categories);
        setSubCategories(subCategories);
        setType(type);
        setPrice(price);
        setQuantity(quantity);
    }

    @JsonProperty("SKU")
    public String getSKU() {
        return SKU;
    }
    @JsonProperty("SKU")
    public void setSKU(String SKU) {
        this.SKU = SKU;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public Category getCategories() {
        return categories;
    }
    public void setCategories(Category categories) {
        this.categories = categories;
    }
    public SubCategory getSubCategories() {
        return subCategories;
    }
    public void setSubCategories(SubCategory subCategories) {
        this.subCategories = subCategories;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public static List<Goods> getAllArrGoods() {
        return allArrGoods;
    }
    public void setAllArrGoods(List<Goods> allArrGoods) {
        this.allArrGoods = allArrGoods;
    }
    public Button getBack() {
        return back;
    }
    public void setBack(Button back) {
        this.back = back;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public HashMap<Category, HashMap<SubCategory, Goods[]>> getGoodList() {
        return goodList;
    }
    public void setGoodList(HashMap<Category, HashMap<SubCategory, Goods[]>> goodList) {
        this.goodList = goodList;
    }
    public static double getPry() {
        return pry;
    }
    public static void setPry(double pry) {
        Goods.pry = pry;
    }
    public static List<Node> getBackupNodesGoods() {
        return backupNodesGoods;
    }
    public static void setBackupNodesGoods(List<Node> backupNodesGoods) {
        Goods.backupNodesGoods = backupNodesGoods;
    }

    public void startSQL() throws SQLException {
        out("Shop/Categories/Goods.java: Запустили startSQL");

        

        Connection conn = DriverManager.getConnection(url, user, password);

        addSQL(conn);
    }

    public double pieceGoods(Goods goods, int steep) {
        double q = 0;

        if (steep > 0) {
            q = goods.getPrice() * steep;
        }

        return q;
    }

    public void addingProductsToCategories() {
        out("Shop/Categories/Goods.java: Вошли в addingProductsToCategories");

        File file = new File(saveSortedGoods.PATH_HASH_ARR);
        if (file.isFile()) { // читаем сырые данные
            List<HashMap> list = readList(saveSortedGoods.PATH_HASH_ARR, HashMap.class);
            Map<?, ?> raw = list.get(0);

            HashMap<Category, HashMap<SubCategory, Goods[]>> fixed =
                    M.convertValue(raw, new TypeReference<HashMap<Category, HashMap<SubCategory, Goods[]>>>() {});

            setGoodList(fixed);
            out("Загружено и приведено: " + getGoodList().keySet());

            return;
        }
        else {
            getGoodList().clear();

            for (Category cat : getShop().getCats()) {
                HashMap<SubCategory, Goods[]> m = getGoodList().get(cat);
                if (m == null) {
                    m = new HashMap<>();
                }

                for (SubCategory sc : SubCategory.values()) {
                    if (sc.getParent() == cat) {
                        Goods[] gd = new Goods[getAllArrGoods().size()];
                        int i = 0;
                        for (int o = 0; o < getAllArrGoods().size(); o++) {
                            if (getAllArrGoods().get(o).getSubCategories() == sc) {
                                gd[i++] = getAllArrGoods().get(o);
                            }
                        }
                        Goods[] gdClean = Arrays.copyOf(gd, i);
                        m.put(sc, gdClean);
                    }
                }

                HashMap<Category, HashMap<SubCategory, Goods[]>> wrapper = new HashMap<>();
                getGoodList().put(cat, m);
            }

            debugDump();
            saveSortedGoods.saveList(getGoodList());
        }
    }


    public void selectedCategories(VBox root,
                                   Category category,
                                   User user) {
        if (category == null) {
            out("Shop/Categories/Goods.java: category == null, возвращаемся в Shop");
            getBack().fire();
        }

        out("Shop/Categories/Goods.java: Вошли в selectedCategories: " + category.name());
        Label label = makeLabel(new Label("Выбрана категория: " + category.getDisplay()));

        out("before clear: " + root.getChildren().size());
        clearRoot(root);
        out("after clear: " + root.getChildren().size());
        out("backup size: " + (getBackupNodesGoods() == null ? "null" : getBackupNodesGoods().size()));

        HBox subCategory = new HBox(10);
        subCategory.setAlignment(Pos.CENTER);

        EnumSet<SubCategory> subs = SubCategory.childrenOf(category);
        ArrayList<Button> subCatButton = new ArrayList<>();
        int countSubs = 0;
        for (SubCategory sub : subs) {
            Button btn = new Button(sub.getSubDisplay());
            subCatButton.add(btn);
            subCatButton.get(countSubs).setUserData(sub);
            countSubs++;

            btn.setOnAction(_ -> {
                SubCategory sc = (SubCategory) btn.getUserData();

                var bySub = getGoodList().get(category);

                Goods[] items = (bySub == null) ? null : bySub.get(sc);

                if (items == null || items.length == 0) {
                    out("Пусто: " + category.name() + " / " + sc.getSubDisplay());
                    return;
                }
                choiceGoods(root, category, items, user);
            });
        }

        for (int i = 0; i < subCatButton.size(); i++) {
            subCategory.getChildren().add(subCatButton.get(i));
        }

        root.getChildren().addAll(label, subCategory, getBack());

        setBackupNodesGoods(backupNode(root));

        getBack().setOnAction(_ -> {
            out("before clear: " + root.getChildren().size());
            clearRoot(root);
            out("after clear: " + root.getChildren().size());
            out("backup size: " + (getBackupNodesGoods() == null ? "null" : getBackupNodesGoods().size()));
            root.getChildren().setAll(Shop.getBackupNodesShop());
        });
    }

    public void choiceGoods(VBox root, Category category, Goods[] items, User user) {
        out("Shop/Categories/Goods.java: Вошли в choiceGoods");

        clearRoot(root);

        ArrayList<HBox> hBoxArrayList = new ArrayList<>();

        Button backSelectedCategories = new Button("Назад");

        Label pryLb = new Label();

        getShop().setCart(user.getItemsInCart());

        if (getShop().getCart() == null) {
            getShop().setCart(new Cart(user.getUserID()));
            user.setItemsInCart(getShop().getCart());
        }

        final Cart cartRef = getShop().getCart();

        final Map<String, Goods> priceIndex = new HashMap<>();

        Map<String, Integer> qtyMap = cartRef.getQtyByName();

        for (int i = 0; i < getAllArrGoods().size(); i++) {
            Goods g = getAllArrGoods().get(i);
            String sku = g.getSKU();
            if (sku != null) {
                priceIndex.put(sku, g);
                out("Shop/Categories/Goods.java: Подвязали SKU к товару: " + priceIndex.get(sku).toString());
            }
        }

        double grandTotal = 0.0;

        if (qtyMap != null && !qtyMap.isEmpty()) {
            out("Shop/Categories/Goods.java: Идёт подсчёт товаров в корзине пользователя ID: " + user.getUserID());
            for (Map.Entry<String, Integer> e : qtyMap.entrySet()) {
                String sku = e.getKey();
                out("Shop/Categories/Goods.java: SKU товара: " + sku);
                int qty = e.getValue() != null ? e.getValue() : 0;

                Goods g = priceIndex.get(sku);

                if (g == null || qty <= 0) {
                    continue;
                }

                double l = pieceGoods(g, qty);
                out("Shop/Categories/Goods.java: Цена товара: " + l + "₽");
                grandTotal += l;
            }
        }

        out("Shop/Categories/Goods.java: Стоимость товара в корзине пользователя ID: " + user.getUserID() + ", составляет: " + grandTotal + "₽");
        setPry(grandTotal);

        out(priceIndex.toString());

        out(getPry());

        pryLb.setText(String.format("Корзина: [%.2f ₽.]", getPry()));

        for (int i = 0; i < items.length; i++) {
            Goods item = items[i];

            Button minus = new Button("-");
            Button plus = new Button("+");

            String unit = item.getType().equals("Штучный") ? "шт." : "гр.";
            int step = item.getType().equals("Штучный") ? 1 : 100;
//            int max = item.getQuantity();

            Label name = new Label(item.getProductName());
            Label qtyLb = new Label();

            String sku = item.getSKU();

            int existing = cartRef.getQtyByName().getOrDefault(sku, 0);
            IntegerProperty qty = new SimpleIntegerProperty(existing);

            qtyLb.textProperty().bind(
                Bindings.createStringBinding(
                        () -> " [" + qty.get() + unit + "]",
                        qty
                )
            );

            HBox row = new HBox(8);

            row.getChildren().addAll(minus, name, qtyLb, plus);
            if (item.getType().equals("Штучный")) {
                row.getChildren().add(3, new Label(" [" + item.getPrice() + "₽." + "]"));
            }
            else {
                row.getChildren().add(3, new Label(" [" + (item.getPrice() * 1000) + "₽ КГ." + "]"));
            }

            hBoxArrayList.add(row);
            root.getChildren().add(row);

            minus.setUserData(new ButtonData(item.getProductName(),
                    item.getType(),
                    item.getQuantity(),
                    item.getPrice()));
            plus.setUserData(new ButtonData(item.getProductName(),
                    item.getType(),
                    item.getQuantity(),
                    item.getPrice()));

            final double[] lineTotal = new double[] {
                    pieceGoods(item, existing)
            };

            minus.setOnAction(e -> {
                ButtonData data = (ButtonData) minus.getUserData();
                out("Shop/Categories/Goods.java: Нажат минус :" + data.getName() + data.getType() + ", " + data.getQuantity() + ", " + data.getPrice());

                int nv = Math.max(0, qty.get() - step);
                out("Shop/Categories/Goods.java: steep" + nv);
                double old = lineTotal[0];
                out("Shop/Categories/Goods.java: Старая сумма: " + old);
                double neu = pieceGoods(item, nv);
                out("Shop/Categories/Goods.java: Новая сумма: " + neu);

                qty.set(nv);
                out("qty: " + qty.get());
                lineTotal[0] = neu;
                out("Shop/Categories/Goods.java: lineTotal: " + lineTotal[0]);

                setPry(getPry() + (neu - old));
                out("Shop/Categories/Goods.java: Pry: " + getPry());
                pryLb.setText(String.format("Корзина: [%.2f ₽.]", getPry()));

                cartRef.upsert(item, nv);

                try {
                    cartRef.saveToFile();
                    cartRef.primaryFields(user);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            plus.setOnAction(e -> {
                ButtonData data = (ButtonData) plus.getUserData();
                out("Shop/Categories/Goods.java: Нажат плюс " + data.getName() + data.getType() + ", " + data.getQuantity() + ", " + data.getPrice());

                int nv = Math.max(0, qty.get() + step);
                out("Shop/Categories/Goods.java: steep" + nv);
                double old = lineTotal[0];
                out("Shop/Categories/Goods.java: Старая сумма: " + old);
                double neu = pieceGoods(item, nv);
                out("Shop/Categories/Goods.java: Новая сумма: " + neu);

                qty.set(nv);
                out("Shop/Categories/Goods.java: qty: " + qty.get());
                lineTotal[0] = neu;
                out("Shop/Categories/Goods.java: lineTotal: " + lineTotal[0]);

                setPry(getPry() + (neu - old));
                out("Shop/Categories/Goods.java: Pry: " + getPry());
                pryLb.setText(String.format("Корзина: [%.2f Руб.]", getPry()));

                if (nv >= data.getQuantity()) {
                    errMess(root, "Достигнут лимит по количеству товара: " + data.getName());
                }

                cartRef.upsert(item, nv);

                try {
                    cartRef.saveToFile();
                    cartRef.primaryFields(user);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        root.getChildren().addAll(backSelectedCategories, pryLb);

        out("before clear: " + root.getChildren().size());
        out("backup size: " + (getBackupNodesGoods() == null ? "null" : getBackupNodesGoods().size()));

        getShop().getShopTab().selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected && root.getChildren().contains(backSelectedCategories) && root.getChildren().contains(pryLb)) {
                out("Shop/Categories/Goods.java: Повторно вошли во вкладку магазина, обновили окно choiceGoods");
                choiceGoods(root, category, items, user);
            }
        });

        backSelectedCategories.setOnAction(_-> {
            out("before clear: " + root.getChildren().size());
            clearRoot(root);
            out("after clear: " + root.getChildren().size());
            out("backup size: " + (getBackupNodesGoods() == null ? "null" : getBackupNodesGoods().size()));
            root.getChildren().setAll(getBackupNodesGoods());
        });
    }

    public class ButtonData {
        private final String name;
        private final String type;
        private final int quantity;
        private final double price;

        public ButtonData(String name, String type, int quantity, double price) {
            this.name = name;
            this.type = type;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }
        public int getQuantity() {
            return quantity;
        }
        public double getPrice() {
            return price;
        }
    }

    public void backShop(User user) {
        if (getShop() != null) {
            setCategories(null);
            getShop().showCategories(user);
        }
        else {
            throw new IllegalStateException("Shop/Categories/Goods.java: shop == null");
        }
    }

    public void showGoods() {
        for (Goods goods : getAllArrGoods()) {
            out("Shop/Categories/Goods.java: " +
                    "\nSKU - " + goods.getSKU() +
                    "\nПродукт - " + goods.getProductName() +
                    "\nКатегория - " + goods.getCategories() +
                    "\nПодкатегория - " + goods.getSubCategories() +
                    "\n===============");
        }
    }

    public void debugDump() {
        StringBuilder sb = new StringBuilder();
        for (var catEntry : getGoodList().entrySet()) {
            Category cat = catEntry.getKey();
            sb.append(cat.getDisplay()).append(":\n");
            for (var scEntry : catEntry.getValue().entrySet()) {
                SubCategory sc = scEntry.getKey();
                Goods[] arr = scEntry.getValue();
                String items = Arrays.stream(arr)
                        .map(g -> g.getProductName())
                        .collect(Collectors.joining(", "));
                sb.append("  - ").append(sc.getSubDisplay()).append(": ").append(items).append("\n");
            }
            sb.append("\n");
        }
        out(sb.toString());
    }

    public class saveSortedGoods extends Goods {
        private HashMap<Category, HashMap<SubCategory, Goods[]>> SaveHashMap = super.getGoodList();

        public static final String PATH_HASH_ARR = "SortedGoods.json";

        public saveSortedGoods() {}
        public saveSortedGoods(HashMap<Category, HashMap<SubCategory, Goods[]>> saveHashMap) {
            SaveHashMap = saveHashMap;
        }

        public HashMap<Category, HashMap<SubCategory, Goods[]>> getSaveHashMap() {
            return SaveHashMap;
        }
        public void setSaveHashMap(HashMap<Category, HashMap<SubCategory, Goods[]>> saveHashMap) {
            SaveHashMap = saveHashMap;
        }

        public static void saveList(HashMap<Category, HashMap<SubCategory, Goods[]>> data) {
            try {
                append(Path.of(PATH_HASH_ARR), data);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addSQL(Connection connection) throws SQLException {
        out("Shop/Categories/Goods.java: Выполняем SQL запросы...");
        new Thread(() -> {
            try {
                String sql = "INSERT INTO Goods (SKU, productName, manufacturer, country, categories, subCategories, type, price, quantity) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "productName = VALUES(productName), " +
                        "manufacturer = VALUES(manufacturer), " +
                        "country = VALUES(country), " +
                        "categories = VALUES(categories), " +
                        "subCategories = VALUES(subCategories), " +
                        "type = VALUES(type), " +
                        "price = VALUES(price), " +
                        "quantity = VALUES(quantity)";

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    for (Goods g : getAllArrGoods()) {
                        ps.setString(1, g.getSKU());
                        ps.setString(2, g.getProductName());
                        ps.setString(3, g.getManufacturer());
                        ps.setString(4, g.getCountry());
                        ps.setString(5, g.getCategories().name());
                        ps.setString(6, g.getSubCategories().name());
                        ps.setString(7, g.getType());
                        ps.setDouble(8, g.getPrice());
                        ps.setInt(9, g.getQuantity());

                        ps.executeUpdate();

                        DownloadBar.setDownloadScale(DownloadBar.getDownloadScale() + 1);
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            DownloadBar.flagProgress = true;
        }).start();
    }

    @Override
    public String toString() {
        return "Goods { " +
                "SKU = " + SKU + '\n' +
                "productName = " + productName + '\n' +
                "manufacturer = " + manufacturer + '\n' +
                "country = " + country + '\n' +
                "categories = " + categories + '\n' +
                "subCategories = " + subCategories + '\n' +
                "type = " + type + '\n' +
                "price = " + price + '\n' +
                "quantity = " + quantity + '\n' +
                "back = " + back + '\n' +
                "goodList = " + goodList +
                '}' + '\n' +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }
}