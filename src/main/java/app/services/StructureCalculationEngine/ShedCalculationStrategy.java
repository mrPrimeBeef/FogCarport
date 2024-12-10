package app.services.StructureCalculationEngine;

import java.util.List;

import app.services.StructureCalculationEngine.Entities.PlacedMaterial;
import app.services.StructureCalculationEngine.Entities.Structure;

public class ShedCalculationStrategy implements CalculationStrategy {

    @Override
    public List<PlacedMaterial> calculateStructure(Structure structure) {
        return List.of();
    }
}
