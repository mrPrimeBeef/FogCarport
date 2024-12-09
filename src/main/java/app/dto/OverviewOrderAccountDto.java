package app.dto;

import java.util.Date;

public class OverviewOrderAccountDto {
    private int orderId;
    private int accountId;
    private String email;
    private Date datePlaced;
    private Date datePaid;
    private Date dateCompleted;
    private double salePriceInclVAT;
    private String status;

    public OverviewOrderAccountDto(int orderId, int accountId, String email, Date datePlaced, Date datePaid, Date dateCompleted, double salePriceInclVAT, String status) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.email = email;
        this.datePlaced = datePlaced;
        this.datePaid = datePaid;
        this.dateCompleted = dateCompleted;
        this.salePriceInclVAT = salePriceInclVAT;
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

    public double getSalePriceInclVAT() {
        return salePriceInclVAT;
    }

    // TODO: Slet denne metode. Er her kun for at thymeleaf fungerer
    public double getSalesPrice() {
        return salePriceInclVAT;
    }

    public String getStatus() {
        return status;
    }
}