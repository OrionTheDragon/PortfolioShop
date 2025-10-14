package Shop.Categories;

import Shop.Category;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Перечисление подкатегорий товаров.
 * <p>Каждая подкатегория относится к одной из основных категорий {@link Category}.</p>
 * <p>Используется для построения дерева «Категория → Подкатегории».</p>
 */
public enum SubCategory {
    /** Хлебобулочные изделия */
    Bread("х/б изделия", Category.BREAD),
    /** Сахар, соль, специи */
    Sugar_Salt_Spices("Сахар, соль, специи", Category.GROCERY),
    /** Крупы и мука */
    Cereals_Flour("Крупы, мука", Category.GROCERY),
    /** Хлебобулочные кондитерские изделия */
    Confectionery("х/б кондитерские изделия", Category.BREAD),
    /** Вода */
    Water("Вода", Category.DRINKS),
    /** Консервы */
    CannedGoods("Консервы", Category.GROCERY),
    /** Чай и кофе */
    Tea_Coffee("Чай, кофе", Category.GROCERY),
    /** Масло растительное */
    VegetableOils("Масло", Category.GROCERY),
    /** Безалкогольные напитки */
    NoAlcoholic("Безалкогольные напитки", Category.DRINKS),
    /** Алкогольные напитки */
    Alcoholic("Алкогольные напитки", Category.DRINKS),
    /** Мясо */
    Meat("Мясо", Category.MEAT),
    /** Колбасные изделия */
    Sausages("Колбасные изделия", Category.MEAT),
    /** Молочные продукты */
    DairyProducts("Молочная продукция", Category.MILK),
    /** Яйца */
    Eggs("Яйца", Category.MILK),
    /** Ягоды */
    Berries("Ягоды", Category.VEGETABLES),
    /** Фрукты */
    Fruits("Фрукты", Category.VEGETABLES),
    /** Овощи */
    Vegetables("Овощи", Category.VEGETABLES);

    /** Отображаемое имя подкатегории. */
    private final String subDisplay;
    /** Родительская категория, к которой относится подкатегория. */
    private final Category parent;

    /**
     * Конструктор подкатегории.
     *
     * @param subDisplay отображаемое имя подкатегории
     * @param parent родительская категория {@link Category}
     */
    SubCategory(String subDisplay, Category parent) {
        this.subDisplay = subDisplay;
        this.parent = parent;
    }

    /**
     * Возвращает отображаемое имя подкатегории.
     *
     * @return локализованное название подкатегории
     */
    public String getSubDisplay() {
        return subDisplay;
    }

    /**
     * Возвращает родительскую категорию подкатегории.
     *
     * @return родительская категория {@link Category}
     */
    public Category getParent() {
        return parent;
    }

    // --- Автосборка «дерева» Category → SubCategory ---

    /** Карта «Категория → набор подкатегорий», неизменяемая. */
    private static final Map<Category, EnumSet<SubCategory>> BY_CATEGORY;

    static {
        // Создаём карту с пустыми наборами подкатегорий для каждой категории
        Map<Category, EnumSet<SubCategory>> m = new EnumMap<>(Category.class);
        for (Category c : Category.values()) {
            m.put(c, EnumSet.noneOf(SubCategory.class));
        }

        // Добавляем каждую подкатегорию в соответствующую категорию
        for (SubCategory sc : values()) {
            m.get(sc.parent).add(sc);
        }

        // Делаем карту неизменяемой для безопасности многопоточности
        BY_CATEGORY = Collections.unmodifiableMap(m);
    }

    /**
     * Возвращает набор подкатегорий для заданной категории.
     *
     * @param c категория {@link Category}
     * @return набор подкатегорий; если категория не найдена, возвращается пустой {@link EnumSet}
     */
    public static EnumSet<SubCategory> childrenOf(Category c) {
        return BY_CATEGORY.getOrDefault(c, EnumSet.noneOf(SubCategory.class));
    }
}
