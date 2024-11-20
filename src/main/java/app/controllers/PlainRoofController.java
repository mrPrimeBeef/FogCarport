package app.controllers;

import app.entities.EmailReceipt;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class PlainRoofController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> postCarportInfo(ctx, connectionPool));

    }

    private static void postCarportInfo(Context ctx, ConnectionPool connectionPool) {
        String carportWidth = ctx.formParam("carport-bredde");
        String carportLength = ctx.formParam("carport-laengde");
        String trapeztag = ctx.formParam("trapeztag");
        String shedWidth = ctx.formParam("redskabsrum-bredde");
        String shedLength = ctx.formParam("redskabsrum-laengde");
        String notes = ctx.formParam("bemærkninger");

        String name = ctx.formParam("navn");
        String adress = ctx.formParam("adresse");
        String zip = ctx.formParam("postnummer");
        String city = ctx.formParam("by");
        String mobil = ctx.formParam("telefon");
        String email = ctx.formParam("email");

        EmailReceipt emailReceipt = new EmailReceipt(carportWidth, carportLength, trapeztag, shedWidth, shedLength, notes, name, adress, zip, city, mobil, email);
    }
}