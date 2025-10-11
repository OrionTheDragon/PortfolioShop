package Shop.Categories.BakeryProducts;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Confectionery extends Goods {

    private ArrayList<Confectionery> arrConfectionery = new ArrayList<>();

    public Confectionery(String SKU,
                         String productName,
                         String manufacturer,
                         String country,
                         Category categories,
                         SubCategory subCategories,
                         String type,
                         double price,
                         int quantity) {
        super(SKU, productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Confectionery> getArrConfectionery() {
        return arrConfectionery;
    }
    public void setArrConfectionery(ArrayList<Confectionery> arrConfectionery) {
        this.arrConfectionery = arrConfectionery;
    }
}
