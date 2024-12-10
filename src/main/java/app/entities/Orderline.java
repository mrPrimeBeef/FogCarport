package app.entities;

public class Orderline {
    private String name;
    private String description;
    private int lengthCm;
    private int quantity;
    private double price;
    private double salePriceInclVAT;

    public Orderline(String name, int lengthCm, String description, int quantity, double salePriceInclVAT) {
        this.name = name;
        this.lengthCm = lengthCm;
        this.quantity = quantity;
        this.description = description;
        this.salePriceInclVAT = salePriceInclVAT;
    }

    public Orderline(String name, int lengthCm, int quantity, double price) {
        this.name = name;
        this.lengthCm = lengthCm;
        this.quantity = quantity;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getSalePriceInclVAT() {
        return salePriceInclVAT;
    }

    public int getLengthCm() {
        return lengthCm;
    }

    @Override
    public String toString() {
        return "Orderline{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}