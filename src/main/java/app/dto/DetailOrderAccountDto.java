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
    private double marginPercentage;
    private String status;
    private int carportLengthCm;
    private int carportWidthCm;
    private int carportHeightCm;

    public DetailOrderAccountDto(int orderId, int accountId, String email, String name, String phone, int zip, String city, Date datePlaced, Date datePaid, Date dateCompleted, double marginPercentage, String status, int carportLengthCm, int carportWidthCm, int carportHeightCm) {
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
        this.marginPercentage = marginPercentage;
        this.status = status;
        this.carportLengthCm = carportLengthCm;
        this.carportWidthCm = carportWidthCm;
        this.carportHeightCm = carportHeightCm;
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

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getZip() {
        return zip;
    }

    public String getCity() {
        return city;
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

    public double getMarginPercentage() {
        return marginPercentage;
    }

    public String getStatus() {
        return status;
    }

    public int getCarportLengthCm() {
        return carportLengthCm;
    }

    public int getCarportWidthCm() {
        return carportWidthCm;
    }

    public int getCarportHeightCm() {
        return carportHeightCm;
    }
}