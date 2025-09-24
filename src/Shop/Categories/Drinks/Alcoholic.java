package Shop.Categories.Drinks;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Alcoholic extends Goods {

    private ArrayList<Alcoholic> arrAlcoholic = new ArrayList<>();

    public Alcoholic(String productName,
                     String manufacturer,
                     String country,
                     Category categories,
                     SubCategory subCategories,
                     String type,
                     double price,
                     int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Alcoholic> getArrAlcoholic() {
        return arrAlcoholic;
    }
    public void setArrAlcoholic(ArrayList<Alcoholic> arrAlcoholic) {
        this.arrAlcoholic = arrAlcoholic;
    }
}
