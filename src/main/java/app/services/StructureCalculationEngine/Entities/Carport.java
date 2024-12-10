package app.services.StructureCalculationEngine.Entities;

import app.persistence.ConnectionPool;
import app.services.StructureCalculationEngine.CarportCalculationStrategy;
import app.services.StructureCalculationEngine.RoofType;
import app.services.StructureCalculationEngine.ShedCalculationStrategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Carport extends Structure{

    private int width;
    private int length;
    private int height;
    private Shed shed;
    boolean hasRaisedRoof;
    RoofType roofType;
    int angle;

    // Cache for placed materials
    private List<PlacedMaterial> cachedMaterials;

    public Carport(int width, int length, int height, Shed shed, boolean hasRaisedRoof, int angle, ConnectionPool connectionPool) {
        super(new CarportCalculationStrategy(connectionPool));
        this.shed = shed;
        this.hasRaisedRoof = hasRaisedRoof;
        this.angle = angle;
        this.length = length;
        this.width = width;
        this.height = height;

        // Sets Roof Type based on hasRaisedRoof
        this.roofType = hasRaisedRoof ? RoofType.RAISED : RoofType.FLAT;
    }

    @Override
    public List<PlacedMaterial> getPlacedMaterials() throws SQLException {
        // Calculate materials only if they haven't been calculated yet.
        // This ensures that if we call getPlacedMaterials, we don't get duplicate materials
        if (cachedMaterials == null) {
            cachedMaterials = new ArrayList<>(super.getPlacedMaterials());

            if (shed != null) {
                cachedMaterials.addAll(shed.getPlacedMaterials());
            }
        }
        return cachedMaterials;
    }

    public void attachShed(Shed shed) {
        this.shed = shed;
    }


    public Shed getShed(){
        return shed;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}