package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import app.dto.OverviewOrderAccountDto;
import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;

public class OrderMapper {

    public static ArrayList<OverviewOrderAccountDto> getOverviewOrderAccountDtos(ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = new ArrayList<>();

        String sql = "SELECT orderr_id, account_id, email, date_placed, date_paid, date_completed, sale_price, status FROM orderr JOIN account USING(account_id) ORDER BY status, date_placed";

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
}


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











