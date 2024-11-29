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
        

        // Sets height to 210 if initial height is lower, also makes height a multiple of 30 if it is not already
        this.length = alignToNearest30(length);
        this.width = alignToNearest30(width);
        this.height = alignToNearest30(height);

        // Sets Roof Type based on hasRaisedRoof
        if(hasRaisedRoof){
            roofType = RoofType.RAISED;
        }else{
            roofType = RoofType.FLAT;
        }
    }

    @Override
    public List<PlacedMaterial> getPlacedMaterials() throws SQLException {
        // Get carport's own materials
        List<PlacedMaterial> allMaterials = new ArrayList<>(super.getPlacedMaterials());

        // If a shed is attached, add its materials
        if (shed != null) {
            allMaterials.addAll(shed.getPlacedMaterials());
        }

        return allMaterials;
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

    private int alignToNearest30(int value) {
        value = Math.max(value, 210);
        return (value / 30) * 30 + ((value % 30 >= 15) ? 30 : 0);
    }
}
