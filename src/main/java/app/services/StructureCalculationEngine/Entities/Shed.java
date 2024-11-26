package app.services.StructureCalculationEngine.Entities;

import app.services.StructureCalculationEngine.ShedCalculationStrategy;

public class Shed extends Structure{

    int width;
    int length;
    int height;
    private Carport attachedCarport;

    public Shed(int width, int length, int height) {
        super(new ShedCalculationStrategy());
        this.width = width;
        this.length = length;
        this.height = height;
    }

    public void attachToCarport(Carport carport){
        this.attachedCarport = carport;
    }

    public Carport getAttachedCarport(){
        return attachedCarport;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    void setHeight(int height){
        this.height = height;
    }
}
