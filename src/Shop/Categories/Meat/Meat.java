package Shop.Categories.Meat;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Meat extends Goods {

    private ArrayList<Meat> arrMeat = new ArrayList<>();

    public Meat(String productName,
                String manufacturer,
                String country,
                Category categories,
                SubCategory subCategories,
                String type,
                double price,
                int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Meat> getArrMeat() {
        return arrMeat;
    }
    public void setArrMeat(ArrayList<Meat> arrMeat) {
        this.arrMeat = arrMeat;
    }
}
