package app.controllers;

import app.entities.EmailReceipt;
import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> getCarportInfo(ctx, connectionPool));
    }

    private static void getCarportInfo(Context ctx, ConnectionPool connectionPool) {
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

        boolean allreadyUser = false;
        ArrayList<String> emails = null;

        try {
            emails = AccountMapper.getAllEmailsFromAccount(ctx, connectionPool);

            for (String mail : emails) {
                if (mail.equals(email)) {
                    allreadyUser = true;
                }
            }

            if (!allreadyUser) {
                AccountMapper.createAccount(name, adress, zip, mobil, email, ctx, connectionPool);
            }

            new EmailReceipt(carportWidth, carportLength, trapeztag, shedWidth, shedLength, notes, name, adress, zip, city, mobil, email);

            CarportMapper.createCarportInquiry(carportWidth, carportLength, trapeztag, shedWidth, shedLength, ctx, connectionPool);

        } catch (DatabaseException | AccountCreationException) {

        }

        ctx.render("tak.html");
    }
}