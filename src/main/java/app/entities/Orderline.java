package app.entities;

public class Orderline {
    private String name;
    private String description;
    private int lengthCm;
    private int quantity;
    private double costPrice;
    private double salePriceInclVAT;

    public Orderline(String name, String description, int lengthCm, int quantity, double costPrice, double salePriceInclVAT) {
        this.name = name;
        this.description = description;
        this.lengthCm = lengthCm;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.salePriceInclVAT = salePriceInclVAT;
    }

    public Orderline(String name, String description, int lengthCm, int quantity, double salePriceInclVAT) {
        this.name = name;
        this.description = description;
        this.lengthCm = lengthCm;
        this.quantity = quantity;
        this.salePriceInclVAT = salePriceInclVAT;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLengthCm() {
        return lengthCm;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public double getSalePriceInclVAT() {
        return salePriceInclVAT;
    }

}