package app.entities;

import java.sql.SQLException;
import java.util.List;

public abstract class Structure {
    private CalculationStrategy strategy;

    public Structure(CalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public CalculationStrategy getStrategy() {
        return strategy;
    }

    public List<PlacedMaterial> getPlacedMaterials() throws SQLException {
        return strategy.calculateStructure(this);
    }


    public abstract int getWidth();
    public abstract int getLength();
    public abstract int getHeight();
}
