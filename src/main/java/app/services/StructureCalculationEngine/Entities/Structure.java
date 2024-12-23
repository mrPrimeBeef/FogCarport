package app.services.StructureCalculationEngine.Entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.services.StructureCalculationEngine.CalculationStrategy;

public abstract class Structure {
    private CalculationStrategy strategy;
    List<PlacedMaterial> placedMaterials;
    private Map<Material, Integer> partsList;
    private List<List<Float>> fixatingStrapListXY = new ArrayList<>();

    public abstract int getWidth();

    public abstract int getLength();

    public abstract int getHeight();

    public Structure(CalculationStrategy strategy) {
        this.strategy = strategy;
        this.placedMaterials = new ArrayList<>();
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
    public void addToPartsList(Material material, int quantity) {
        if (partsList.containsKey(material)) {
            int existingQuantity = partsList.get(material);
            partsList.put(material, existingQuantity + quantity);
        } else {
            partsList.put(material, quantity);
        }
    }

    public Map<Material, Integer> getPartsList() {
        return partsList;
    }

    public void addFixatingStrapListXY(List<Float> oneStrap) {
        fixatingStrapListXY.add(oneStrap);
    }

    public List<List<Float>> getFixatingStrapListXY() {
        return fixatingStrapListXY;
    }
}
