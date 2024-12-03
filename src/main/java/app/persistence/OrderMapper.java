package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import app.dto.DetailOrderAccountDto;
import app.dto.OverviewOrderAccountDto;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;

public class OrderMapper {

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
            throw new DatabaseException("Fejl til sælger", "Error in getAllOrderAccountDtos", e.getMessage());
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
            ps.setString(2, "In progress");
            ps.setInt(3, carportLength);
            ps.setInt(4, carportWidth);
            ps.setInt(5, 210); // carport height 200 cm
            ps.setInt(6, shedWidth);
            ps.setInt(7, shedLength);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                success = true;
            } else {
                throw new OrderException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder method");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder method", e.getMessage());
        }
        return success;
    }


    // TODO: Husk at bede om at "sale_price" bliver lavet om til "margin_percentage" i databasen
    public static DetailOrderAccountDto getDetailOrderAccountDtoByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT orderr_id, account_id, email, name, phone, zip_code, city, date_placed, date_paid, date_completed, sale_price, status, carport_length_cm, carport_width_cm, carport_height_cm FROM orderr JOIN account USING(account_id) JOIN zip_code USING(zip_code) WHERE orderr_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int accountId = rs.getInt("account_id");
                String email = rs.getString("email");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                int zip = rs.getInt("zip_code");
                String city = rs.getString("city");
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double marginPercentage = rs.getDouble("sale_price");
                String status = rs.getString("status");
                int carportLengthCm = rs.getInt("carport_length_cm");
                int carportWidthCm = rs.getInt("carport_width_cm");
                int carportHeightCm = rs.getInt("carport_height_cm");

                return new DetailOrderAccountDto(orderId, accountId, email, name, phone, zip, city, datePlaced, datePaid, dateCompleted, marginPercentage, status, carportLengthCm, carportWidthCm, carportHeightCm);

            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl til sælger", "Error in getDetailOrderAccountDtoByOrderId for orderId: " + orderId, e.getMessage());
        }
        return null;
    }

    // TODO: Husk at bede om at "sale_price" bliver lavet om til "margin_percentage" i databasen
    public static void updateMarginPercentage(int orderId, double marginPercentage, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "UPDATE orderr SET sale_price = ? WHERE orderr_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, marginPercentage);
            ps.setInt(2, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl til sælger", "Error updating margin percentage for orderId: " + orderId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl til sælger", "Error updating margin percentage for orderId: " + orderId, e.getMessage());
        }
    }

}