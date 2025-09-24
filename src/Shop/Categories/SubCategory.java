package Shop.Categories;

import Shop.Category;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public enum SubCategory {
    Bread("х/б изделия", Category.BREAD),
    Sugar_Salt_Spices("Сахар, соль, специи", Category.GROCERY),
    Cereals_Flour("Крупы, мука", Category.GROCERY),
    Confectionery("х/б кондитерские изделия", Category.BREAD),
    Water("Вода", Category.DRINKS),
    CannedGoods("Консервы", Category.GROCERY),
    Tea_Coffee("Чай, кофе", Category.GROCERY),
    VegetableOils("Масло", Category.GROCERY),
    NoAlcoholic("Безалкогольные напитки", Category.DRINKS),
    Alcoholic("Алкогольные напитки", Category.DRINKS),
    Meat("Мясо", Category.MEAT),
    Sausages("Колбасные изделия", Category.MEAT),
    DairyProducts("Молочная продукция", Category.MILK),
    Eggs("Яйца", Category.MILK),
    Berries("Ягоды", Category.VEGETABLES),
    Fruits("Фрукты", Category.VEGETABLES),
    Vegetables("Овощи", Category.VEGETABLES);

    private final String subDisplay;
    private final Category parent;

    SubCategory(String subDisplay, Category parent) {
        this.subDisplay = subDisplay;
        this.parent = parent;
    }
    public String getSubDisplay() { return subDisplay; }
    public Category getParent() { return parent; }

    // --- Автосборка «дерева» Category -> SubCategory ---
    private static final Map<Category, EnumSet<SubCategory>> BY_CATEGORY;
    static {
        Map<Category, EnumSet<SubCategory>> m = new EnumMap<>(Category.class);
        for (Category c : Category.values()) m.put(c, EnumSet.noneOf(SubCategory.class));
        for (SubCategory sc : values()) m.get(sc.parent).add(sc);
        BY_CATEGORY = Collections.unmodifiableMap(m);
    }

    public static EnumSet<SubCategory> childrenOf(Category c) {
        return BY_CATEGORY.getOrDefault(c, EnumSet.noneOf(SubCategory.class));
    }
}
