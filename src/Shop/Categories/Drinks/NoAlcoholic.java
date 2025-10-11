package Shop.Categories.Drinks;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class NoAlcoholic extends Goods {

    private ArrayList<NoAlcoholic> arrNoAlcoholic = new ArrayList<>();

    public NoAlcoholic(String SKU,
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

    public ArrayList<NoAlcoholic> getArrNoAlcoholic() {
        return arrNoAlcoholic;
    }
    public void setArrNoAlcoholic(ArrayList<NoAlcoholic> arrNoAlcoholic) {
        this.arrNoAlcoholic = arrNoAlcoholic;
    }
}
