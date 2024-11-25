package app.entities;

public class Account {
    private int accountId;
    private String name;
    private String address;
    private int zip;
    private String phone;
    private String email;
    private String role;

    public Account(int accountId, String name, String address, int zip, String phone, String email, String role) {
        this.accountId = accountId;
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
