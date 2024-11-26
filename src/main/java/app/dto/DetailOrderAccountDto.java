package app.dto;

import java.util.Date;

public class DetailOrderAccountDto {
    private int orderId;
    private int accountId;
    private String email;
    private String name;
    private String phone;
    private int zip;
    private String city;
    private Date datePlaced;
    private Date datePaid;
    private Date dateCompleted;
    private double salesPrice;
    private String status;

    public DetailOrderAccountDto(int orderId, int accountId, String email, String name, String phone, int zip, String city, Date datePlaced, Date datePaid, Date dateCompleted, double salesPrice, String status) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.zip = zip;
        this.city = city;
        this.datePlaced = datePlaced;
        this.datePaid = datePaid;
        this.dateCompleted = dateCompleted;
        this.salesPrice = salesPrice;
        this.status = status;
    }

//    public int getOrderId() {
//        return orderId;
//    }
//
//    public int getAccountId() {
//        return accountId;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public Date getDatePlaced() {
//        return datePlaced;
//    }
//
//    public Date getDatePaid() {
//        return datePaid;
//    }
//
//    public Date getDateCompleted() {
//        return dateCompleted;
//    }
//
//    public double getSalesPrice() {
//        return salesPrice;
//    }
//
//    public String getStatus() {
//        return status;
//    }
}