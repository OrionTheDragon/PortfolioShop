package Shop.Categories.Grocery;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class VegetableOils extends Goods {

    private ArrayList<VegetableOils> arrVegetableOils = new ArrayList<>();

    public VegetableOils(String productName,
                         String manufacturer,
                         String country,
                         Category categories,
                         SubCategory subCategories,
                         String type,
                         double price,
                         int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<VegetableOils> getArrVegetableOils() {
        return arrVegetableOils;
    }
    public void setArrVegetableOils(ArrayList<VegetableOils> arrVegetableOils) {
        this.arrVegetableOils = arrVegetableOils;
    }
}
