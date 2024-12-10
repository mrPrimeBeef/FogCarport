package app.entities;

import java.util.Date;

public class Order {
    int orderId;
    Date datePlaced;
    Date datePaid;
    Date dateCompleted;
    double marginPercentage;
    String status;

    public Order(int orderrId, Date datePlaced, Date datePaid, Date dateCompleted, double marginPercentage, String status) {
    this.orderId = orderrId;
    this.datePlaced = datePlaced;
    this.datePaid = datePaid;
    this.dateCompleted = dateCompleted;
    this.marginPercentage = marginPercentage;
    this.status = status;
    }
    public Order(int orderrId, Date datePlaced, Date datePaid, Date dateCompleted, String status) {
        this.orderId = orderrId;
        this.datePlaced = datePlaced;
        this.datePaid = datePaid;
        this.dateCompleted = dateCompleted;
        this.marginPercentage = marginPercentage;
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

    public double getmarginPercentage() {
        return marginPercentage;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderr_id=" + orderId +
                ", datePlaced=" + datePlaced +
                ", datePaid=" + datePaid +
                ", dateCompleted=" + dateCompleted +
                ", saleprice=" + marginPercentage +
                ", status='" + status + '\'' +
                '}';
    }
}