package Shop.Categories.Milk;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Eggs extends Goods {

    private ArrayList<Eggs> arrEggs = new ArrayList<>();

    public Eggs(String productName,
                String manufacturer,
                String country,
                Category categories,
                SubCategory subCategories,
                String type,
                double price,
                int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Eggs> getArrEggs() {
        return arrEggs;
    }
    public void setArrEggs(ArrayList<Eggs> arrEggs) {
        this.arrEggs = arrEggs;
    }
}
