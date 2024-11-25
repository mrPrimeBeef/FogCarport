package app.entities;

public class Carport extends Structure{

    int width;
    int length;
    int height;
    private Shed shed;

    public Carport(int width, int length, int height) {
        super(new CarportCalculationStrategy());
        this.width = width;
        this.length = length;
        this.height = Math.max(height, 200);
    }

    public void addShed(Shed shed){
        this.shed = shed;
        shed.attachToCarport(this);
    }

    public Shed getShed(){
        return shed;
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
}
