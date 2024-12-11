package app.entities;

import java.util.Date;

public class Order {
    int orderId;
    Date datePlaced;
    Date datePaid;
    Date dateCompleted;
    double salePriceInclVAT;
    String status;

    public Order(int orderId, Date datePlaced, Date datePaid, Date dateCompleted, double salePriceInclVAT, String status) {
        this.orderId = orderId;
        this.datePlaced = datePlaced;
        this.datePaid = datePaid;
        this.dateCompleted = dateCompleted;
        this.salePriceInclVAT = salePriceInclVAT;
        this.status = status;
    }

    public Order(int orderId, Date datePlaced, Date datePaid, Date dateCompleted, String status) {
        this.orderId = orderId;
        this.datePlaced = datePlaced;
        this.datePaid = datePaid;
        this.dateCompleted = dateCompleted;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public Date getDatePlaced() {
        return datePlaced;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public double getSalePriceInclVAT() {
        return salePriceInclVAT;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", datePlaced=" + datePlaced +
                ", datePaid=" + datePaid +
                ", dateCompleted=" + dateCompleted +
                ", salePriceInclVAT=" + salePriceInclVAT +
                ", status='" + status + '\'' +
                '}';
    }
}