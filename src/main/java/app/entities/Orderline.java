package app.entities;

public class Orderline {
    private String name;
    private String description;
    private int quantity;
    private double price;

    public Orderline(String name, String description, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
    public Orderline(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
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