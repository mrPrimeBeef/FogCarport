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
import app.services.SalePriceCalculator;
import app.services.StructureCalculationEngine.Entities.Material;

public class OrderlineMapper {

    public static ArrayList<Orderline> getOrderlinesForCustomerOrSalesrep(int orderId, String role, ConnectionPool connectionPool) throws OrderException {
        ArrayList<Orderline> orderlineList = new ArrayList<>();
        String sql = "SELECT orderline.quantity, orderline.cost_price, item.name, item.description, item.length_cm, orderr.paid, orderr.margin_percentage FROM orderline JOIN item USING(item_id) JOIN orderr USING(orderr_id) WHERE orderr_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                double costPrice = rs.getDouble("cost_price");
                String name = rs.getString("name");
                int lengthCm = rs.getInt("length_cm");
                String description = rs.getString("description") != null ? rs.getString("description") : "";
                boolean paid = rs.getBoolean("paid");

                double marginPercentage = rs.getDouble("margin_percentage");

                // TODO: skal beregnes ordenligt
                double salePriceInclVAT = SalePriceCalculator.calculateSalePriceInclVAT(costPrice, marginPercentage);

                if (role.equals("Kunde") && paid) {
                    orderlineList.add(new Orderline(name, lengthCm, description, quantity, salePriceInclVAT));
                } else if (role.equals("salesrep")) {
                    orderlineList.add(new Orderline(name, lengthCm, quantity, costPrice));
                }
            }
        } catch (SQLException e) {
            throw new OrderException("Der skete en fejl i at hente din stykliste", "Error in getOrderlinesForCustomerOrSalesrep()", e.getMessage());
        }
        return orderlineList;
    }

    public static void addOrderlines(int orderId, Map<Material, Integer> orderParts, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO orderline (item_id, quantity, orderr_id, cost_price)" + "VALUES(?, ?, ?, ?) ";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            for (Map.Entry<Material, Integer> parts : orderParts.entrySet()) {
                Material material = parts.getKey();
                int quantity = parts.getValue();

                ps.setInt(1, material.getMaterialId());
                ps.setInt(2, quantity);
                ps.setInt(3, orderId);
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

    public static void deleteOrderlinesFromOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM orderline WHERE orderr_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Fejl", "Error in deleteOrderlinesFromOrderId()", e.getMessage());
        }
    }


    public static double getTotalCostPriceFromOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT SUM(cost_price) FROM orderline WHERE orderr_id=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double totalCostPrice = rs.getDouble("sum");
                return totalCostPrice;
            }
            throw new DatabaseException("Fejl ved hentning af total kostpris for ordrenr: " + orderId, "Error in getTotalCostPriceFromOrderId() for orderId: " + orderId);

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af total kostpris for ordrenr: " + orderId, "Error in getTotalCostPriceFromOrderId() for orderId: " + orderId, e.getMessage());
        }
    }

}