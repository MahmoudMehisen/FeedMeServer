package foodOreder.feedMeServer.Model;

public class User {
    private String name,password,phone,isStaff, SecureCode,homeLat,homeLng;

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }

    public User(String name, String password, String SecureCode) {
        this.name = name;
        this.password = password;
        this.isStaff= "true";
        this.SecureCode = SecureCode;
        this.homeLat = "";
        this.homeLng = "";
    }

    public String getHomeLat() {
        return homeLat;
    }

    public void setHomeLat(String homeLat) {
        this.homeLat = homeLat;
    }

    public String getHomeLng() {
        return homeLng;
    }

    public void setHomeLng(String homeLng) {
        this.homeLng = homeLng;
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
