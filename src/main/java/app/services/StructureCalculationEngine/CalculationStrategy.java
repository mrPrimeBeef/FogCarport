package app.services.StructureCalculationEngine;

import java.sql.SQLException;
import java.util.List;

import app.services.StructureCalculationEngine.Entities.PlacedMaterial;
import app.services.StructureCalculationEngine.Entities.Structure;

public interface CalculationStrategy {

    public List<PlacedMaterial> calculateStructure(Structure structure) throws SQLException;
}
