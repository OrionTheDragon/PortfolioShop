package Shop;

public enum Category {
    MEAT("Мясо"),
    MILK("Молочные продукты и яйца"),
    VEGETABLES("Овощи, фрукты, ягоды"),
    BREAD("Х/Б изделия"),
    GROCERY("Бакалея"),
    DRINKS("Напитки");

    private final String display;

    Category(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}