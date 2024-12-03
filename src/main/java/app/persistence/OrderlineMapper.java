package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import app.entities.Orderline;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.services.StructureCalculationEngine.Entities.Material;

public class OrderlineMapper {

    public static ArrayList<Orderline> getMaterialListForCustomerOrSalesrep(int orderr_id, String role, ConnectionPool connectionPool) throws OrderException {
        ArrayList<Orderline> orderlineList = new ArrayList<>();
        String sql = "SELECT orderline.quantity, orderline.cost_price, item.name, item.description FROM orderline JOIN item USING(item_id) JOIN orderr USING(orderr_id) WHERE orderr_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, orderr_id);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                int quantity = rs.getInt("quantity");
                double costPrice = rs.getDouble("cost_price");
                String name = rs.getString("name");
                String description = rs.getString("description") != null ? rs.getString("description") : "";
                if(role.equals("Kunde")){
                    orderlineList.add(new Orderline(name,description, quantity));
                } else if(role.equals("salesrep")){
                    orderlineList.add(new Orderline(name, quantity, costPrice));
                }
            }
        } catch (SQLException e) {
            throw new OrderException("Der skete en fejl i at hente din stykliste", "Error in getMaterialListForCustomerOrSalesrep()", e.getMessage());
        }
        return orderlineList;
    }

    public static void addOrderline(Map<Material, Integer> orderParts, int orderr_id, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO orderline (item_id, quantity, orderr_id, cost_price)" + "VALUES(?, ?, ?, ?) ";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

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
            throw new DatabaseException("Databasefejl: Der skete en fejl i at oprette din ordre", "Error in addOrderline() in OrderLineMapper", e.getMessage());
        }
    }
}