package Shop.Categories.Grocery;

import Shop.Categories.Goods;
import Shop.Categories.SubCategory;
import Shop.Category;

import java.util.ArrayList;

public class Sugar_Salt_Spices extends Goods {

    private ArrayList<Sugar_Salt_Spices> arrSugar_Salt_Spices = new ArrayList<>();

    public Sugar_Salt_Spices(String productName,
                             String manufacturer,
                             String country,
                             Category categories,
                             SubCategory subCategories,
                             String type,
                             double price,
                             int quantity) {
        super(productName, manufacturer, country, categories, subCategories, type, price, quantity);
    }

    public ArrayList<Sugar_Salt_Spices> getArrSugar_Salt_Spices() {
        return arrSugar_Salt_Spices;
    }
    public void setArrSugar_Salt_Spices(ArrayList<Sugar_Salt_Spices> arrSugar_Salt_Spices) {
        this.arrSugar_Salt_Spices = arrSugar_Salt_Spices;
    }
}
