package app.entities;

public class Account {

    private int accountId;
    private String name;
    private String address;
    private int zip;
    private String phone;
    private String email;
    private String role;

    public Account(int accountId, String name, String address, int zip, String phone, String email, String password, String role) {
        this.accountId = accountId;
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public Account(int accountId, String role) {
        this.accountId = accountId;
        this.role = role;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }


    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + accountId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", zip='" + zip + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}