package ghost.android.ghosthguapp.professor;

public class ProfessorData implements Comparable<ProfessorData> {
    private String name;
    private String phone;
    private String major;
    private String email;
    private String office;

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

    public String getMajor() {
        return major;
    }

    public void setMajor(String nMajor) {
        this.major = nMajor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String nEmail) {
        this.email = nEmail;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String nOffice) {
        this.office = nOffice;
    }
    @Override
    public int compareTo(ProfessorData o) {
        // TODO Auto-generated method stub
        return name.compareTo(o.name);
    }
}
