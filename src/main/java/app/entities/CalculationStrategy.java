package app.entities;

import java.sql.SQLException;
import java.util.List;

public interface CalculationStrategy {

    public List<PlacedMaterial> calculateStructure(Structure structure) throws SQLException;
}
