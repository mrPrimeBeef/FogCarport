package app.persistence;

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

        } catch (DatabaseException e) {
            throw new DatabaseException("", "From getAllEmailsFromAccount", e.getMessage());
        }
    }
}
