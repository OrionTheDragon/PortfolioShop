package Shop.Categories.Milk;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class DairyProducts extends Goods {

    private ArrayList<DairyProducts> arrDairyProducts = new ArrayList<>();

    public DairyProducts(String SKU,
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

    public ArrayList<DairyProducts> getArrDairyProducts() {
        return arrDairyProducts;
    }
    public void setArrDairyProducts(ArrayList<DairyProducts> arrDairyProducts) {
        this.arrDairyProducts = arrDairyProducts;
    }
}
