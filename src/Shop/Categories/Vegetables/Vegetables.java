package Shop.Categories.Vegetables;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Vegetables extends Goods {

    private ArrayList<Vegetables> arrVegetables = new ArrayList<>();

    public Vegetables(String productName,
                      String manufacturer,
                      String country,
                      Category categories,
                      SubCategory subCategories,
                      String type,
                      double price,
                      int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Vegetables> getArrVegetables() {
        return arrVegetables;
    }
    public void setArrVegetables(ArrayList<Vegetables> arrVegetables) {
        this.arrVegetables = arrVegetables;
    }
}
