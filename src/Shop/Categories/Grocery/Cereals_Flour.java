package Shop.Categories.Grocery;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Cereals_Flour extends Goods {

    private ArrayList<Cereals_Flour> arrCereals_Flour = new ArrayList<>();

    public Cereals_Flour(String productName,
                         String manufacturer,
                         String country,
                         Category categories,
                         SubCategory subCategories,
                         String type,
                         double price,
                         int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Cereals_Flour> getArrCereals_Flour() {
        return arrCereals_Flour;
    }
    public void setArrCereals_Flour(ArrayList<Cereals_Flour> arrCereals_Flour) {
        this.arrCereals_Flour = arrCereals_Flour;
    }
}
