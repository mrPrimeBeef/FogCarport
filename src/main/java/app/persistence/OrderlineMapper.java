package app.persistence;

import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.services.StructureCalculationEngine.Entities.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;


public class OrderlineMapper {

    public static void addOrderlines(Map<Material, Integer> orderParts, int orderr_id, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO orderline (item_id, quantity, orderr_id, cost_price)" + "VALUES(?, ?, ?, ?) ";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for(Map.Entry<Material, Integer> parts : orderParts.entrySet()) {
                Material material = parts.getKey();
                int quantity = parts.getValue();

                ps.setInt(1, material.getMaterialId());
                ps.setInt(2, quantity);
                ps.setInt(3, orderr_id);
                ps.setFloat(4, material.getCostPrice() * quantity);

                // adds every query to a batch of queries
                ps.addBatch();
            }

            // Executes all queries at once
            ps.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl: Der skete en fejl i at oprette din ordre", "Error in addOrderLine() in OrderLineMapper", e.getMessage());
        }
    }
}