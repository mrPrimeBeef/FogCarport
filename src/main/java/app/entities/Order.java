package app.entities;

import java.util.Date;

public class Order {
    int orderr_id;
    Date datePlaced;
    Date datePaid;
    Date dateCompleted;
    double saleprice;
    String status;

    public Order(int orderrId, Date datePlaced, Date datePaid, Date dateCompleted, double saleprice, String status) {
    this.orderr_id = orderrId;
    this.datePlaced = datePlaced;
    this.datePaid = datePaid;
    this.dateCompleted = dateCompleted;
    this.saleprice = saleprice;
    this.status = status;
    }

    public int getOrderr_id() {
        return orderr_id;
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

    public double getSaleprice() {
        return saleprice;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderr_id=" + orderr_id +
                ", datePlaced=" + datePlaced +
                ", datePaid=" + datePaid +
                ", dateCompleted=" + dateCompleted +
                ", saleprice=" + saleprice +
                ", status='" + status + '\'' +
                '}';
    }
}