package Shop.Categories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import Data.User;

import Shop.Category;
import Shop.Shop;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import static Util.Util.*;

@JsonIgnoreProperties({"allArrGoods", "shop", "back"})
public class Goods {
    // Продукты
    public static final String GOODS_PATH = "Goods.json";

    // Параметры товаров
    private String productName;
    private String manufacturer;
    private String country;
    private Category categories;
    private SubCategory subCategories;
    private String type;
    private double price;
    private int quantity;

    // Кнопки
    @JsonIgnore
    private Button back = new Button("Назад");

    @JsonIgnore
    private static List<Goods> allArrGoods = safeReadList(GOODS_PATH, Goods.class);

    @JsonIgnore
    private HashMap<Category, HashMap<SubCategory, Goods[]>> goodList = new HashMap<>();

    @JsonIgnore
    private Shop shop;

//    @JsonIgnore
//    public Category getCategoryEnum() {
//        return parseCategory(categories);
//    }
//
//    @JsonIgnore
//    public SubCategory getSubCategoryEnum() {
//        return parseSubCategory(subCategories);
//    }

    public Goods() {out("Shop/Categories/Goods.java: Загрузили Goods");}

    public Goods(Shop shop) {
        this.shop = shop;
    }

    public Goods(String productName, String manufacturer, String country, Category categories, SubCategory subCategories, String type, double price, int quantity) {
        setProductName(productName);
        setManufacturer(manufacturer);
        setCountry(country);
        setCategories(categories);
        setSubCategories(subCategories);
        setType(type);
        setPrice(price);
        setQuantity(quantity);
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
    public List<Goods> getAllArrGoods() {
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
    public Shop getShop() {
        return shop;
    }
    public void setShop(Shop shop) {
        this.shop = shop;
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

    protected double pieceGoods(int steep) {
        double q = 0;
        if (steep > 0) {
            q = getPrice() * steep;
        }

        return q;
    }

//    private static Category parseCategory(String v) {
//        if (v == null) {
//            return null;
//        }
//        for (Category c : Category.values()) {
//            if (v.equalsIgnoreCase(c.name()) || v.equalsIgnoreCase(c.getDisplay())) return c;
//        }
//        String norm = v.trim().toUpperCase()
//                .replace(' ', '_').replace('-', '_').replace('/', '_');
//        for (Category c : Category.values()) {
//            if (norm.equals(c.name())) return c;
//        }
//        throw new IllegalArgumentException("Unknown Category: " + v);
//    }
//
//    private static SubCategory parseSubCategory(String v) {
//        if (v == null) {
//            return null;
//        }
//        for (SubCategory s : SubCategory.values()) {
//            if (v.equalsIgnoreCase(s.name()) || v.equalsIgnoreCase(s.getSubDisplay())) return s;
//        }
//        String norm = v.trim().toUpperCase()
//                .replace(' ', '_').replace('-', '_').replace('/', '_');
//        for (SubCategory s : SubCategory.values()) {
//            if (norm.equals(s.name())) return s;
//        }
//        throw new IllegalArgumentException("Unknown SubCategory: " + v);
//    }

//    public void AddingProductsToCategories() {
//        out("Shop/Categories/Goods.java: Началась привязка продуктов к категориям");
//        for (Goods allArrGood : getAllArrGoods()) {
//            switch (allArrGood.getSubCategories()) {
//                case "Х/б изделия" : {
//                    Bread bread = new Bread(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    bread.getArrBread().add(bread);
//                    out("Shop/Categories/Goods.java: Х/б изделие добавлено " + bread.getProductName());
//                    break;
//                }
//                case "Сахар, соль, специи" : {
//                    Sugar_Salt_Spices sss = new Sugar_Salt_Spices(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    sss.getArrSugar_Salt_Spices().add(sss);
//                    out("Shop/Categories/Goods.java: Сахар, соль, специи добавлено " + sss.getProductName());
//                    break;
//                }
//                case "Крупы, мука" : {
//                    Cereals_Flour cf = new Cereals_Flour(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    cf.getArrCereals_Flour().add(cf);
//                    out("Shop/Categories/Goods.java: Крупы, мука добавлены " + cf.getProductName());
//                    break;
//                }
//                case "Х/б кондитерские изделия" : {
//                    Confectionery confectionery = new Confectionery(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    confectionery.getArrConfectionery().add(confectionery);
//                    out("Shop/Categories/Goods.java: Х/б кондитерское изделие добавлено " + confectionery.getProductName());
//                    break;
//                }
//                case "Вода" : {
//                    Water water = new Water(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    water.getArrWater().add(water);
//                    out("Shop/Categories/Goods.java: Вода добавлена " + water.getProductName());
//                    break;
//                }
//                case "Консервы" : {
//                    CannedGoods canned = new CannedGoods(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    canned.getArrCannedGoods().add(canned);
//                    out("Shop/Categories/Goods.java: Консервы добавлена " + canned.getProductName());
//                    break;
//                }
//                case "Чай, кофе" : {
//                    Tea_Coffee teaCoffee = new Tea_Coffee(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    teaCoffee.getArrTea_Coffee().add(teaCoffee);
//                    out("Shop/Categories/Goods.java: Чай, кофе добавлен " + teaCoffee.getProductName());
//                    break;
//                }
//                case "Масло" : {
//                    VegetableOils oil = new VegetableOils(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    oil.getArrVegetableOils().add(oil);
//                    out("Shop/Categories/Goods.java: Масло добавлено " + oil.getProductName());
//                    break;
//                }
//                case "Безалкогольные напитки" : {
//                    NoAlcoholic noAlc = new NoAlcoholic(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    noAlc.getArrNoAlcoholic().add(noAlc);
//                    out("Shop/Categories/Goods.java: Без алк. напиток добавлен " + noAlc.getProductName());
//                    break;
//                }
//                case "Алкогольные напитки" : {
//                    Alcoholic alc = new Alcoholic(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    alc.getArrAlcoholic().add(alc);
//                    out("Shop/Categories/Goods.java: Алк. напиток добавлен " + alc.getProductName());
//                    break;
//                }
//                case "Мясо" : {
//                    Meat meat = new Meat(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    meat.getArrMeat().add(meat);
//                    out("Shop/Categories/Goods.java: Мясо добавлено " + meat.getProductName());
//                    break;
//                }
//                case "Колбасные изделия" : {
//                    Sausages sausages = new Sausages(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    sausages.getArrSausages().add(sausages);
//                    out("Shop/Categories/Goods.java: Колбаса добавлена " + sausages.getProductName());
//                    break;
//                }
//                case "Молочная продукция" : {
//                    DairyProducts dairy = new DairyProducts(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    dairy.getArrDairyProducts().add(dairy);
//                    out("Shop/Categories/Goods.java: Молочка добавлена " + dairy.getProductName());
//                    break;
//                }
//                case "Яйца" : {
//                    Eggs eggs = new Eggs(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    eggs.getArrEggs().add(eggs);
//                    out("Shop/Categories/Goods.java: Яйца добавлены " + eggs.getProductName());
//                    break;
//                }
//                case "Ягоды" : {
//                    Berries berries = new Berries(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    berries.getArrBerries().add(berries);
//                    out("Shop/Categories/Goods.java: Ягоды добавлены " + berries.getProductName());
//                    break;
//                }
//                case "Фрукты" : {
//                    Fruits fruits = new Fruits(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    fruits.getArrFruits().add(fruits);
//                    out("Shop/Categories/Goods.java: Фрукты добавлены " + fruits.getProductName());
//                    break;
//                }
//                case "Овощи" : {
//                    Vegetables vegetables = new Vegetables(
//                            allArrGood.getProductName(),
//                            allArrGood.getManufacturer(),
//                            allArrGood.getCountry(),
//                            allArrGood.getCategories(),
//                            allArrGood.getSubCategories(),
//                            allArrGood.getType(),
//                            allArrGood.getPrice(),
//                            allArrGood.getQuantity());
//
//                    vegetables.getArrVegetables().add(vegetables);
//                    out("Shop/Categories/Goods.java: Овощи добавлены " + vegetables.getProductName());
//                    break;
//                }
//            }
//        }
//    }

    public void addingProductsToCategories() {
        File file = new File(saveSortedGoods.PATH_HASH_ARR);
        List<HashMap<Category, HashMap<SubCategory, Goods[]>>> list = readList(saveSortedGoods.PATH_HASH_ARR, (Class) HashMap.class);
        if (file.isFile()) {
            setGoodList(list.get(0));
            out("Shop/Categories/Goods.java: Успешно загрузили отсортированный файл товаров " + getGoodList());
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
                        // НЕ перезаписываем категорию заново:
                        // getGoodList().put(cat, new HashMap(m)); // удалить
                        // НЕ добавляем всю goodList в список на каждой итерации:
                        // getHashMapArrayList().add(getGoodList()); // удалить
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

        clearRoot(root);

        root.getChildren().addAll(label, getBack());

        getBack().setOnAction(_ -> {
            backShop(user);
        });
    }

    public void backShop(User user) {
        if (getShop() != null) {
            getShop().showCategories(user);
        }
        else {
            throw new IllegalStateException("Shop/Categories/Goods.java: shop == null");
        }
    }

    public void showGoods() {
        for (Goods goods : getAllArrGoods()) {
            out("Shop/Categories/Goods.java: Продукт - " + goods.getProductName());
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
}