package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;

public class OrderMapper {
    public static boolean createOrder(int accountId, int carportWidth, int carportLength, int shedWidth, int shedLength, ConnectionPool connectionPool) throws OrderCreationException, DatabaseException {
        boolean success = false;

        String sql = "INSERT INTO orderr (account_id, status, carport_length_cm, carport_width_cm, carport_height_cm, shed_width_cm, shed_length_cm) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, accountId);
            ps.setString(2, "In progress");
            ps.setInt(3, carportLength);
            ps.setInt(4, carportWidth);
            ps.setInt(5, 200); // carport height 200 cm
            ps.setInt(6, shedWidth);
            ps.setInt(7, shedLength);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                success = true;
            } else {
                throw new OrderCreationException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder method");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder method", e.getMessage());
        }
        return success;
    }
}