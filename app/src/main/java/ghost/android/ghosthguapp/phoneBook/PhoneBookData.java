package ghost.android.ghosthguapp.phoneBook;

public class PhoneBookData implements Comparable<PhoneBookData> {
    private String name;
    private String phone;
    private String category;

    public String getName() {
        return name;
    }

    public void setName(String nName) {
        this.name = nName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String nPhone) {
        this.phone = nPhone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String nCategory) {
        this.category = nCategory;
    }

    @Override
    public int compareTo(PhoneBookData o) {
        // TODO Auto-generated method stub
        return name.compareTo(o.name);
    }
}
