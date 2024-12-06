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

    // ***** Temporary comment for integrating carport into other classes: *****
    // Only the Carport class needs to be imported.
    // Carport carport = new Carport(width, length, height, shed, hasRaisedRoof, angle);
    // Carport carport = new Carport(760, 510, 230, null or shed object, false, 0);
    // Shed can be NULL if no shed is required (shed not implemented).
    // hasRaisedRoof = false
    // angle = 0;
    //
    // To go through all materials of a carport (for example to use with SVG), you can include this
    // ***** NOTICE: you might have to change x and y based on your preferences *****
    // List<PlacedMaterial> placedMaterials = carport.getPlacedMaterials();
    //      for (PlacedMaterial material : placedMaterials) {
    //            double x = material.getX();
    //            double y = material.getY();
    //            double width = material.getMaterial().getWidthMm();
    //            double height = material.getMaterial().getLengthCm();
    //
    // If we need a shed, we need to declare a shed instance before the
    // carport instance, and include this shed in the carport instance at carport creation
    // Shed creation is simple (not yet implemented):
    // Shed shed = new Shed(210, 180, NULL);
    // Sheds should automagically adjust height after the carport.
    // Default/lowest height for carport is 210cm, even if height is set to

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