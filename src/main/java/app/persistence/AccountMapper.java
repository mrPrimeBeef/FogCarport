package app.persistence;

import java.sql.*;
import java.util.ArrayList;

import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import app.services.PasswordGenerator;

public class AccountMapper {
    public static ArrayList<String> getAllAccountEmails(ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<String> emails = new ArrayList<>();

        String sql = "SELECT email FROM account";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
            return emails;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl i at hente emails", "Error in getAllEmailsFromAccount", e.getMessage());
        }
    }

    public static int getAccountIdFromEmail(String email, ConnectionPool connectionPool) throws AccountCreationException {
        int accountId = 0;
        String sql = "SELECT account_id FROM account WHERE email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                accountId = rs.getInt("account_id");
            }

        } catch (SQLException e) {
            throw new AccountCreationException("Fejl ved søgning efter account ID", "Error in getIdFromAccountEmail: " + email, e.getMessage());
        }

        return accountId;
    }

    public static int createAccount(String name, String adress, int zip, String phone, String email, ConnectionPool connectionPool) throws AccountCreationException {
        int accountId;
        String sql = "INSERT INTO account (email, password, name, role, address, zip_code, phone)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?) ";

        String password = PasswordGenerator.generatePassword();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, "Kunde");
            ps.setString(5, adress);
            ps.setInt(6, zip);
            ps.setString(7, phone);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new AccountCreationException("Fejl ved oprettelse af ny bruger.");
            } else {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    accountId = rs.getInt(1);
                } else {
                    throw new AccountCreationException("Kunne ikke hente det genererede account ID.");
                }
            }

        } catch (SQLException e) {
            throw new AccountCreationException("Fejl i at oprette en konto", "Error in createAccount", e.getMessage());
        }
        return accountId;
    }
}