package Shop.Categories.Grocery;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class CannedGoods extends Goods {

    private ArrayList<CannedGoods> arrCannedGoods = new ArrayList<>();

    public CannedGoods(String SKU,
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

    public ArrayList<CannedGoods> getArrCannedGoods() {
        return arrCannedGoods;
    }
    public void setArrCannedGoods(ArrayList<CannedGoods> arrCannedGoods) {
        this.arrCannedGoods = arrCannedGoods;
    }
}
