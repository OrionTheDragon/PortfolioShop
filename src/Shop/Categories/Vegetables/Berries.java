package Shop.Categories.Vegetables;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Berries extends Goods {

    private ArrayList<Berries> arrBerries = new ArrayList<>();

    public Berries(String productName,
                   String manufacturer,
                   String country,
                   Category categories,
                   SubCategory subCategories,
                   String type,
                   double price,
                   int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Berries> getArrBerries() {
        return arrBerries;
    }
    public void setArrBerries(ArrayList<Berries> arrBerries) {
        this.arrBerries = arrBerries;
    }
}
