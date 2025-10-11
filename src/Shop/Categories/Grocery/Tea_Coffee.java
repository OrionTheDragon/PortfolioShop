package Shop.Categories.Grocery;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Tea_Coffee extends Goods {

    private ArrayList<Tea_Coffee> arrTea_Coffee = new ArrayList<>();

    public Tea_Coffee(String SKU,
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

    public ArrayList<Tea_Coffee> getArrTea_Coffee() {
        return arrTea_Coffee;
    }
    public void setArrTea_Coffee(ArrayList<Tea_Coffee> arrTea_Coffee) {
        this.arrTea_Coffee = arrTea_Coffee;
    }
}
