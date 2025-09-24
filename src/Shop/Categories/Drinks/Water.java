package Shop.Categories.Drinks;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Water extends Goods {

    private ArrayList arrWater = new ArrayList<>();

    public Water(String productName,
                 String manufacturer,
                 String country,
                 Category categories,
                 SubCategory subCategories,
                 String type,
                 double price,
                 int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList getArrWater() {
        return arrWater;
    }
    public void setArrWater(ArrayList arrWater) {
        this.arrWater = arrWater;
    }
}
