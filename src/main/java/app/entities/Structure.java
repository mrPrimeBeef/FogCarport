package app.entities;

public abstract  class Structure {
    private CalculationStrategy calculationStrategy;

    public Structure(CalculationStrategy calculationStrategy){
        this.calculationStrategy = calculationStrategy;
    }

    public abstract int getWidth();
    public abstract int getLength();
    public abstract int getHeight();
}
