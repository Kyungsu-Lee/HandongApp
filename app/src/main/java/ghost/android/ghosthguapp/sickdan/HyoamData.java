package ghost.android.ghosthguapp.sickdan;

/**
 * Created by Administrator on 2015-01-03.
 */
public class HyoamData {
    private String name;
    private String price;
    private String date;
    private String corner;
    private int type;

    public String getName() {
        return name;
    }
    public void setName(String nName){
        this.name = nName;
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
