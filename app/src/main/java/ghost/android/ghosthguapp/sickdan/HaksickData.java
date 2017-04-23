package ghost.android.ghosthguapp.sickdan;

public class HaksickData {
    private String menu;
    private String price;
    private String date;
    private String corner;
    private int type;

    public String getMenu() {
        return menu;
    }
    public void setMenu(String nMenu){
        this.menu = nMenu;
    }

    public String getPrice() { return price; }
    public void setPrice(String nPrice){
        this.price = nPrice;
    }

    public String getDate() { return date; }
    public void setDate(String nDate){
        this.date = nDate;
    }

    public int getType() { return type; }
    public void setType(int nType) { this.type = nType;}

    public String getCorner() { return corner; }
    public void setCorner(String nCorner){
        this.corner = nCorner;
    }
}
