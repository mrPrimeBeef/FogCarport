package app.persistence;

import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderMapper {
    public static boolean createOrder(int carportWidth, int carportLength, int shedWidth, int shedLength, Context ctx, ConnectionPool connectionPool) throws OrderCreationException, DatabaseException {
        boolean sucess = false;

        String sql = "INSERT INTO orderr carport_length_cm, carport_width_cm, carport_height_cm, shed_width_cm, shed_length_cm) " +
                " VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, carportWidth);
            ps.setInt(2, carportLength);
            ps.setInt(3, 200); // carport height 200 cm
            ps.setInt(4, shedWidth);
            ps.setInt(5, shedLength);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                sucess = true;
            } else {
                throw new OrderCreationException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder methode");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder methode", e.getMessage());
        }
        return sucess;
    }
}