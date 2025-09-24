package Shop.Categories.Vegetables;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Fruits extends Goods {

    private ArrayList<Fruits> arrFruits = new ArrayList<>();

    public Fruits(String productName,
                  String manufacturer,
                  String country,
                  Category categories,
                  SubCategory subCategories,
                  String type,
                  double price,
                  int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Fruits> getArrFruits() {
        return arrFruits;
    }
    public void setArrFruits(ArrayList<Fruits> arrFruits) {
        this.arrFruits = arrFruits;
    }
}
