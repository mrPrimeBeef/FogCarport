package app.persistence;

import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccountMapper {
    public static ArrayList<String> getAllEmailsFromAccount(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<String> emails = new ArrayList<>();

        String sql = "select email from account";

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

    public static boolean createAccount(String name, String adress, String zip, String mobil, String email, Context ctx, ConnectionPool connectionPool) throws AccountCreationException {
        boolean success = false;
        String sql = "INSERT INTO account (email, password, name, role, address, zip_code, mobil)" +
                     " VALUES (?, ?, ?, ?, ?, ?, ?) ";
        try (Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, email);
            ps.setString(2, "1234");
            ps.setString(3, name);
            ps.setString(4, "Kunde");
            ps.setString(5, adress);
            ps.setString(6, zip);
            ps.setString(7, mobil);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new AccountCreationException("Fejl ved oprettelse af ny bruger.");
            } else{
                success = true;
            }
        } catch (SQLException e){
            throw new AccountCreationException("Fejl i at oprette en konto", "Error in createAccount", e.getMessage());
        }
        return success;
    }

    }