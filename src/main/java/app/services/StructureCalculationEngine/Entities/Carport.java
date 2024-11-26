package app.services.StructureCalculationEngine.Entities;

import app.persistence.ConnectionPool;
import app.services.StructureCalculationEngine.CarportCalculationStrategy;
import app.services.StructureCalculationEngine.RoofType;

public class Carport extends Structure{

    private int width;
    private int length;
    private int height;
    private Shed shed;
    boolean hasRaisedRoof;
    RoofType roofType;
    int angle;

    // *****Temporary comment for integrating carport into other classes:*****
    // Only the Carport class needs to be imported.
    // Carport carport = new Carport(width, length, height, shed, hasRaisedRoof, angle);
    // Carport carport = new Carport(760, 510, 230, null or shed object, false, 0);
    // Shed can be NULL if no shed is required (shed not implemented).
    // hasRaisedRoof = false
    // angle = 0;
    //
    // If we need a shed, we need to declare a shed instance before the
    // carport instance, and include this shed in the carport instance at carport creation
    // Shed creation is simple (not yet implemented):
    // Shed shed = new Shed(210, 180, NULL);
    // Sheds should automagically adjust height after the carport.
    // Default/lowest height for carport is 210cm, even if height is set to 0
    public Carport(int width, int length, int height, Shed shed, boolean hasRaisedRoof, int angle, ConnectionPool connectionPool) {
        super(new CarportCalculationStrategy(connectionPool));
        this.width = width;
        this.length = length;
        this.shed = shed;
        this.hasRaisedRoof = hasRaisedRoof;
        this.angle = angle;

        // Sets height to 210 if initial height is lower, also makes height a multiple of 30 if it is not already
        this.height = Math.max(height, 210);
        if(this.height % 30 != 0){
            this.height = (this.height / 30) * 30 + ((this.height % 30 >= 15) ? 30 : 0);
        }

        this.length = Math.max(length, 240);
        if(this.length % 30 != 0){
            this.length = (this.length / 30) * 30 + ((this.length % 30 >= 15) ? 30 : 0);
        }

        this.width = Math.max(width, 240);
        if(this.width % 30 != 0){
            this.width = (this.width / 30) * 30 + ((this.width % 30 >= 15) ? 30 : 0);
        }

        // Sets Roof Type based on hasRaisedRoof
        if(hasRaisedRoof){
            roofType = RoofType.RAISED;
        }else{
            roofType = RoofType.FLAT;
        }
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

    public void setShedHeight(){
        shed.setHeight(height);
    }
}
