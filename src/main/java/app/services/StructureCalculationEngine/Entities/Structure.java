package app.services.StructureCalculationEngine.Entities;

import app.services.StructureCalculationEngine.CalculationStrategy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Structure {
    private CalculationStrategy strategy;
    private Map<Material, Integer> partsList;

    public abstract int getWidth();
    public abstract int getLength();
    public abstract int getHeight();

    public Structure(CalculationStrategy strategy) {
        this.strategy = strategy;
        this.partsList = new HashMap<>();
    }

    public CalculationStrategy getStrategy() {
        return strategy;
    }

    public void setCalculationStrategy(CalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<PlacedMaterial> getPlacedMaterials() throws SQLException {
        return strategy.calculateStructure(this);
    }

    // Adds the parts, and quantity of parts of the Structure, to a Hashmap. If the material is already in the map, the
    // quantity is added with the already existing quantity.
    public void addToPartsList(Material materialName, int quantity) {
        if (partsList.containsKey(materialName)) {
            int existingQuantity = partsList.get(materialName);
            partsList.put(materialName, existingQuantity + quantity);
        } else {
            partsList.put(materialName, quantity);
        }
    }

    public Map<Material, Integer> getPartsList() {
        return partsList;
    }
}
