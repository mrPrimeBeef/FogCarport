package app.services.StructureCalculationEngine;

import app.services.StructureCalculationEngine.Entities.PlacedMaterial;
import app.services.StructureCalculationEngine.Entities.Structure;

import java.sql.SQLException;
import java.util.List;

public interface CalculationStrategy {

    public List<PlacedMaterial> calculateStructure(Structure structure) throws SQLException;
}
