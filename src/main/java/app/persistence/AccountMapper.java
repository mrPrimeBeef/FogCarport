package app.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;
import app.config.LoggerConfig;
import app.services.PasswordGenerator;

public class AccountMapper {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static ArrayList<Account> getAllCustomerAccounts(ConnectionPool connectionPool) throws AccountException {
        ArrayList<Account> accounts = new ArrayList<>();

        String sql = "SELECT email, name, address, zip_code, city, phone FROM account JOIN zip_code USING(zip_code) WHERE role = 'Kunde'";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String mail = rs.getString("email");
                String name = rs.getString("name");
                String address = rs.getString("address");
                int zipCode = rs.getInt("zip_code");
                String phone = rs.getString("phone");
                String city = rs.getString("city");

                accounts.add(new Account(name, address, zipCode, phone, mail, city));
            }
            return accounts;

        } catch (SQLException e) {
            LOGGER.severe("Error in getAllAccounts() connection. E message: " + e.getMessage());
            throw new AccountException("Der skete en fejl i at hente alle kunderne");
        }
    }

    public static Account login(String email, String password, ConnectionPool connectionPool) throws AccountException {
        Account account = null;
        String sql = "SELECT account_id, name, role, address, city, phone FROM account JOIN zip_code USING(zip_code) WHERE email=? AND password=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String phone = rs.getString("phone");

                if (role.equals("salesrep")) {
                    account = new Account(accountId, role);
                } else if (role.equals("Kunde")) {
                    account = new Account(accountId, email, name, role, address, city, phone);
                }
            }
            return account;
        } catch (SQLException e) {
            LOGGER.severe("Error in login() connection. E message: " + e.getMessage());
            throw new AccountException("Der skete en fejl i login.");
        }
    }

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
            LOGGER.severe("Error in getAllAccountEmails() connection. E message: " + e.getMessage());
            throw new DatabaseException("Der skete en fejl i at hente alle emails for kunderne");
        }
    }

    public static int getAccountIdFromEmail(String email, ConnectionPool connectionPool) throws AccountException {
        String sql = "SELECT account_id FROM account WHERE email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                return accountId;
            }
            LOGGER.severe("Error in getAccountIdFromEmail()");
            throw new AccountException("Fejl ved søgning efter account ID");

        } catch (SQLException e) {
            LOGGER.severe("Error in getAccountIdFromEmail() connection. E message: " + e.getMessage());
            throw new AccountException("Der skete en fejl ved søgning efter account ID");
        }

    }

    public static int createAccount(String name, String address, int zip, String phone, String email, ConnectionPool connectionPool) throws AccountException {

        String sql = "INSERT INTO account (email, password, name, role, address, zip_code, phone) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING account_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String password = PasswordGenerator.generatePassword();

            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, "Kunde");
            ps.setString(5, address);
            ps.setInt(6, zip);
            ps.setString(7, phone);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                return accountId;
            }
            LOGGER.severe("Error in createAccount()");
            throw new AccountException("Fejl i at oprette en konto");

        } catch (SQLException e) {
            LOGGER.severe("Error in createAccount() connection. E message: " + e.getMessage());
            throw new AccountException("Der skete en fejl i at oprette en konto");
        }

    }

    public static Account getAccountByEmail(String email, ConnectionPool connectionPool) throws AccountException {
        Account account = null;
        String sql = "SELECT account_id, email, role FROM account WHERE email = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("email"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getAccountByEmail() connection. E message: " + e.getMessage());
            throw new AccountException("Der skete en fejl i at hente en konto fra databasen");
        }
        return account;
    }

    public static Account getPasswordByEmail(String email, ConnectionPool connectionPool) throws AccountException {
        Account account = null;
        String sql = "SELECT account_id, password, email, role FROM account WHERE email = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getAccountByEmail() connection. E message: " + e.getMessage());
            throw new AccountException("Der skete en fejl i at hente adgangskoden fra databasen");
        }
        return account;
    }

    public static void updatePassword(String email, String newPassword, ConnectionPool connectionPool) throws AccountException {
        String sql = "UPDATE account SET password = ? WHERE email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                LOGGER.severe("Error in updatePassword()");
                throw new AccountException("Kunne ikke opdatere adgangskoden, da ingen konto blev fundet for den indtastet email.");
            }

        } catch (SQLException e) {
            LOGGER.severe("Error in updatePassword() connection. E message: " + e.getMessage());
            throw new AccountException("Der skete en fejl i at opdater adgangskoden");
        }
    }

    public static Account getPasswordAndEmail(int accountId, ConnectionPool connectionPool) throws AccountException {
        Account account = null;
        String sql = "SELECT email, password FROM account WHERE account_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String password = rs.getString("password");
                account = new Account(email, password);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getPasswordAndEmail() connection. E message: " + e.getMessage());
            throw new AccountException("Fejl i at hente brugeren fra databasen");
        }
        return account;
    }

    public static ArrayList<Integer> getAllZips(ConnectionPool connectionPool) {
        String sql = "SELECT zip_code FROM zip_code ORDER BY zip_code ASC";
        ArrayList<Integer> zips = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                zips.add(rs.getInt("zip_code"));
            }

        } catch (SQLException e) {
            LOGGER.severe("Error in getAllZips() connection. E message: " + e.getMessage());
        }
        return zips;
    }
}