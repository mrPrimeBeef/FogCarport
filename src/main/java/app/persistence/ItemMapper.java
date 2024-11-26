package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMapper {

    // Dynamically searches for an item with the help of ItemSearchBuilder
    public static Material searchSingleItem(Map<String, Object> filters, ConnectionPool pool) throws DatabaseException {

        StringBuilder sql = new StringBuilder("SELECT * FROM item WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        filters.forEach((key, value) -> {
            sql.append(" AND ").append(key).append(" = ?");
            parameters.add(value);
        });

        // Limit the query to a single result
        sql.append(" LIMIT 1");

        try (Connection connection = pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToMaterial(rs); // Return the first match
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        // Return null if no item is found
        return null;
    }

    // Dynamically searches for several items with the help of ItemSearchBuilder
    public static List<Material> searchSeveralItems(ConnectionPool pool, Map<String, Object> filters) throws DatabaseException {

        StringBuilder sql = new StringBuilder("SELECT * FROM item WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        filters.forEach((key, value) -> {
            sql.append(" AND ").append(key).append(" = ?");
            parameters.add(value);
        });

        List<Material> materials = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                materials.add(mapRowToMaterial(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
        return materials;
    }

    public static Material getItemById(int id, ConnectionPool pool) throws DatabaseException {

        String sql = "SELECT * FROM item WHERE id = ?";
        try (Connection connection = pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToMaterial(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
        return null;
    }

    public static List<Material> getAllItems(ConnectionPool pool) throws DatabaseException {

        String sql = "SELECT * FROM item";
        List<Material> materials = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                materials.add(mapRowToMaterial(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
        return materials;
    }

    // Convert a result from DB to an instance of a material, and return it.
    private static Material mapRowToMaterial(ResultSet rs) throws SQLException {
        return new Material(
                rs.getInt("item_id"),
                rs.getString("name") != null ? rs.getString("name") : "",
                rs.getString("description") != null ? rs.getString("description") : "",
                rs.getString("item_type") != null ? rs.getString("item_type") : "",
                rs.getString("material_type") != null ? rs.getString("material_type") : "",
                rs.getFloat("length_cm"),
                rs.getFloat("width_mm"),
                rs.getFloat("height_mm"),
                rs.getInt("package_amount"),
                rs.getString("package_type") != null ? rs.getString("package_type") : "",
                rs.getFloat("cost_price")
        );
    }
}
