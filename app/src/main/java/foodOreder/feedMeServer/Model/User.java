package foodOreder.feedMeServer.Model;

public class User {
    private String name,password,phone,isStaff;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getIsStaff() {
        return isStaff;
    }
}
