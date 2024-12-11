package app.services.StructureCalculationEngine;

import app.services.StructureCalculationEngine.Entities.PlacedMaterial;
import app.services.StructureCalculationEngine.Entities.Structure;

import java.util.List;

public class ShedCalculationStrategy implements CalculationStrategy {

    @Override
    public List<PlacedMaterial> calculateStructure(Structure structure) {
        return List.of();
    }
}
