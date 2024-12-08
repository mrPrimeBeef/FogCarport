package app.dto;

import java.util.Date;

public class OverviewOrderAccountDto {
    private int orderId;
    private int accountId;
    private String email;
    private Date datePlaced;
    private Date datePaid;
    private Date dateCompleted;
    private double salesPrice;
    private String status;

    public OverviewOrderAccountDto(int orderId, int accountId, String email, Date datePlaced, Date datePaid, Date dateCompleted, double salesPrice, String status) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.email = email;
        this.datePlaced = datePlaced;
        this.datePaid = datePaid;
        this.dateCompleted = dateCompleted;
        this.salesPrice = salesPrice;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
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

    public double getSalesPrice() {
        return salesPrice;
    }

    public String getStatus() {
        return status;
    }
}