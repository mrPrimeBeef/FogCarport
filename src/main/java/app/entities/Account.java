package app.entities;

public class Account {

    private int accountId;
    private String name;
    private String email;
    private String password;
    private String role;

    public Account(int accountId, String name, String email, String password, String role) {
        this.accountId = accountId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getMemberId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }


    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + accountId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}