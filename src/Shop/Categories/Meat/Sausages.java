package Shop.Categories.Meat;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Sausages extends Goods {

    private ArrayList<Sausages> arrSausages = new ArrayList<>();

    public Sausages(String productName,
                    String manufacturer,
                    String country,
                    Category categories,
                    SubCategory subCategories,
                    String type,
                    double price,
                    int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Sausages> getArrSausages() {
        return arrSausages;
    }
    public void setArrSausages(ArrayList<Sausages> arrSausages) {
        this.arrSausages = arrSausages;
    }
}
