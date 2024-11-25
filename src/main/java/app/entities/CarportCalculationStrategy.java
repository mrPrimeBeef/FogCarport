package app.entities;

import app.persistence.ConnectionPool;
import app.persistence.ItemMapper;

import java.sql.SQLException;
import java.util.List;

public class CarportCalculationStrategy implements CalculationStrategy{

    ConnectionPool pool;

    @Override
    public List<PlacedMaterial> calculateStructure(Structure structure) throws SQLException {

        Carport carport = (Carport) structure;

        //Every unit is in cm unless otherwise is specified
        int overhang = 20;
        int maxPillarDistance = 450;

        try {
            Material Pillar = ItemMapper.getItemById(1, pool);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return List.of();
    }
}
