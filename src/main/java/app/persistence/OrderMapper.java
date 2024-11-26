package app.persistence;

import app.dto.OrderAccountDto;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class OrderMapper {
    public static ArrayList<OrderAccountDto> getAllOrderAccountDtos(ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<OrderAccountDto> orderAccountDtos = new ArrayList<>();

        String sql = "SELECT orderr_id, account_id, email, date_placed, date_paid, date_completed, sales_price, status FROM orderr JOIN account USING(account_id) ORDER BY status, date_placed";

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
                orderAccountDtos.add(new OrderAccountDto(orderId, accountId,email,datePlaced,datePaid, dateCompleted, salesPrice, status));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl til sælger", "Error in getAllOrderAccountDtos", e.getMessage());
        }
        return orderAccountDtos;
    }
}
