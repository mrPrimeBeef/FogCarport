package app.persistence;

import app.entities.Account;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountMapper {

    public static Account login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM member WHERE email=? AND password=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String zip = rs.getString("zip");
                String phone = rs.getString("phone");
                String Email = rs.getString("email");
                String role = rs.getString("role");
                return new Account(accountId, name, address, zip, phone, Email, password, role);
            } else {
                throw new DatabaseException("Forkert email eller adgangskode.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("DB error in login.", e.getMessage());
        }
    }
}
