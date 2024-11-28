package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;
import app.exceptions.OrderException;

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

    public static Order showCustomerOrder(int account_id, ConnectionPool connectionPool) throws OrderException {
        Order order = null;
        String sql = "SELECT orderr_id, date_placed, date_paid, date_completed, sale_price, status FROM orderr WHERE account_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, account_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int orderr_id = rs.getInt("orderr_id");
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double saleprice = rs.getDouble("sale_price");
                String status = rs.getString("status");

                order = new Order(orderr_id, datePlaced, datePaid, dateCompleted, saleprice, status);
            }
            return order;
        } catch (SQLException e) {
            throw new OrderException("Der skete en fejl i at hente din ordre", "Error happen in: showCustomerOrder", e.getMessage());
        }
    }
}