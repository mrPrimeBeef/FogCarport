package app.dto;

import java.util.Date;

public class OrderAccountDto {
    private int orderId;
    private int ccountId;
    private String email;
    private Date datePlaced;
    private Date datePaid;
    private Date dateCompleted;
    private double salesPrice;
    private String statu;

    public OrderAccountDto(int orderId, int ccountId, String email, Date datePlaced, Date datePaid, Date dateCompleted, double salesPrice, String statu) {
        this.orderId = orderId;
        this.ccountId = ccountId;
        this.email = email;
        this.datePlaced = datePlaced;
        this.datePaid = datePaid;
        this.dateCompleted = dateCompleted;
        this.salesPrice = salesPrice;
        this.statu = statu;
    }
}
