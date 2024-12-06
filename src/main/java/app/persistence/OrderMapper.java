package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ArrayList;
import java.util.logging.Logger;

import app.entities.Order;
import app.dto.OverviewOrderAccountDto;
import app.exceptions.DatabaseException;
import app.config.LoggerConfig;
import app.exceptions.OrderException;

public class OrderMapper {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static ArrayList<OverviewOrderAccountDto> getOverviewOrderAccountDtos(ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = new ArrayList<>();

        String sql = "SELECT orderr_id, account_id, email, date_placed, date_paid, date_completed, sale_price, status FROM orderr JOIN account USING(account_id) ORDER BY status DESC , date_placed DESC";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("orderr_id");
                int accountId = rs.getInt("account_id");
                String email = rs.getString("email");
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double salesPrice = rs.getDouble("sale_price");
                String status = rs.getString("status");
                OverviewOrderAccountDtos.add(new OverviewOrderAccountDto(orderId, accountId, email, datePlaced, datePaid, dateCompleted, salesPrice, status));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getOverviewOrderAccountDtos() connection. E message: " + e.getMessage());
            throw new DatabaseException("Fejl til sælger", "Error in getAllOrderAccountDtos()", e.getMessage());
        }
        return OverviewOrderAccountDtos;
    }

    public static boolean createOrder(int accountId, int carportWidth, int carportLength, int shedWidth, int shedLength, ConnectionPool connectionPool) throws OrderException, DatabaseException {
        boolean success = false;

        String sql = "INSERT INTO orderr (account_id, status, carport_length_cm, carport_width_cm, carport_height_cm, shed_width_cm, shed_length_cm) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, accountId);
            ps.setString(2, "Henvendelse");
            ps.setInt(3, carportLength);
            ps.setInt(4, carportWidth);
            ps.setInt(5, 210); // carport height 210 cm //TODO opdater faldttag.html
            ps.setInt(6, shedWidth);
            ps.setInt(7, shedLength);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                success = true;
            } else {
                LOGGER.severe("Error in createOrder() SQL query was not a sucessful execution");
                throw new OrderException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder()");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in createOrder() connection. E message: " + e.getMessage());
            throw new DatabaseException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder()", e.getMessage());
        }
        return success;
    }

    public static ArrayList<Order> getOrdersFromAccountId(int account_id, ConnectionPool connectionPool) throws OrderException {
        ArrayList<Order> orders = new ArrayList<>();
        String sql = "SELECT orderr_id, date_placed, date_paid, date_completed, sale_price, status FROM orderr WHERE account_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, account_id);

            ResultSet rs = ps.executeQuery();


            while (rs.next()) {
                int orderId = rs.getInt("orderr_id");
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double salePrice = rs.getDouble("sale_price");
                String status = rs.getString("status");

                orders.add(new Order(orderId, datePlaced, datePaid, dateCompleted, salePrice, status)) ;
            }
            return orders;
        } catch (SQLException e) {
            throw new OrderException("Der skete en fejl i at hente din ordre", "Error happen in showCustomerOrder()", e.getMessage());
        }
    }
    public static Order getOrder(int orderId, ConnectionPool connectionPool) throws OrderException {
        Order order = null;
        String sql = "SELECT date_placed, date_paid, date_completed, sale_price, status FROM orderr WHERE orderr_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double salePrice = rs.getDouble("sale_price");
                String status = rs.getString("status");
                order = new Order(orderId, datePlaced, datePaid, dateCompleted, salePrice, status);
            }
            return order;

        } catch (SQLException e) {
            throw new OrderException("Der skete en fejl i at hente din ordre", "Error happen in: showCustomerOrder", e.getMessage());
        }
    }
}