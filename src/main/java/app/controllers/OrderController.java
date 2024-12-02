package app.controllers;

import java.sql.SQLException;

import app.entities.EmailReceipt;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import app.persistence.ConnectionPool;
import app.services.svgEngine.CarportSvg;
import app.services.StructureCalculationEngine.Entities.Carport;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index"));
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> postCarportCustomerInfo(ctx, connectionPool));
        app.get("saelgerordre", ctx -> salesrepShowOrderPage(ctx, connectionPool));

    }
private static void postCarportCustomerInfo(Context ctx, ConnectionPool connectionPool) {
    int carportWidth = Integer.parseInt(ctx.formParam("carport-bredde"));
    int carportLength = Integer.parseInt(ctx.formParam("carport-laengde"));
    int shedWidth = Integer.parseInt(ctx.formParam("redskabsrum-bredde"));
    int shedLength = Integer.parseInt(ctx.formParam("redskabsrum-laengde"));
    String notes = ctx.formParam("bemaerkninger");

    String name = ctx.formParam("navn");
    String address = ctx.formParam("adresse");
    int zip = Integer.parseInt(ctx.formParam("postnummer"));
    String city = ctx.formParam("by");
    String phone = ctx.formParam("telefon");
    String email = ctx.formParam("email");
    try {
        int accountId = createOrGetAccountId(email, name, address, zip, phone, ctx, connectionPool);
        OrderMapper.createOrder(accountId, carportWidth, carportLength, shedWidth, shedLength, connectionPool);

        new EmailReceipt(carportWidth, carportLength, shedWidth, shedLength, notes, name, address, zip, city, phone, email);
    } catch (AccountException | OrderException | DatabaseException e) {
        ctx.attribute("ErrorMessage", e.getMessage());
        ctx.render("error.html");
    }
    showThankYouPage(name, email, ctx);
}


    private static void salesrepShowOrderPage(Context ctx, ConnectionPool connectionPool) {

        int carportLengthCm = 752;
        int carportWidthCm = 600;
        int carportHeightCm = 210;

        Carport carport = new Carport(carportWidthCm, carportLengthCm, carportHeightCm, null, false, 0, connectionPool);

        System.out.println(carport.getLength());
        System.out.println(carport.getWidth());
        System.out.println(carport.getHeight());

        try {
            ctx.attribute("carportSvgSideView", CarportSvg.sideView(carport));
            ctx.attribute("carportSvgTopView", CarportSvg.topView(carport));
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        ctx.render("saelgerordre.html");
    }

}