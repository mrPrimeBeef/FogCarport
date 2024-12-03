package app.persistence;

import app.services.StructureCalculationEngine.Entities.Material;
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
            if ("length_cm".equals(key) || "width_mm".equals(key) || "height_mm".equals(key)) {
                sql.append(" AND ").append(key).append(" >= ?");
            } else {
                sql.append(" AND ").append(key).append(" = ?");
            }
            parameters.add(value);
        });

        sql.append(" ORDER BY length_cm ASC, width_mm ASC, height_mm ASC LIMIT 1");

        try (Connection connection = pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToMaterial(rs);
            }else {
                StringBuilder offendingFields = new StringBuilder();
                filters.forEach((key, value) -> offendingFields.append(key).append("=").append(value).append(", "));

                throw new DatabaseException("Databasefejl",  "Error in searchSingleItem() in ItemMapper for query: " + offendingFields);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl: ingen forbindelse",  "Error in DB connection in searchSingleItem() for ItemMapper", e.getMessage());
        }
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
