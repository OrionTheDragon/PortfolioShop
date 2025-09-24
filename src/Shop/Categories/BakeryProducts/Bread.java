package Shop.Categories.BakeryProducts;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Bread extends Goods {

    private ArrayList<Bread> arrBread = new ArrayList<>();

    public Bread(String productName,
                 String manufacturer,
                 String country,
                 Category categories,
                 SubCategory subCategories,
                 String type,
                 double price,
                 int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Bread> getArrBread() {
        return arrBread;
    }
    public void setArrBread(ArrayList<Bread> arrBread) {
        this.arrBread = arrBread;
    }
}
