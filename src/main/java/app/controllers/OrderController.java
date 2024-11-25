package app.controllers;

import app.entities.EmailReceipt;
import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> postCarportCustomerInfo(ctx, connectionPool));
    }

    private static void postCarportCustomerInfo(Context ctx, ConnectionPool connectionPool) {
        int carportWidth = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLength = Integer.parseInt(ctx.formParam("carport-laengde"));
        String trapeztag = ctx.formParam("trapeztag");
        int shedWidth = Integer.parseInt(ctx.formParam("redskabsrum-bredde"));
        int shedLength = Integer.parseInt(ctx.formParam("redskabsrum-laengde"));
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

            OrderMapper.createOrder(carportWidth, carportLength, shedWidth, shedLength, ctx, connectionPool);

        } catch (AccountCreationException e) {
            ctx.attribute("ErrorMessage", new AccountCreationException(e.getMessage()));
            ctx.render("error.html");
        } catch (OrderCreationException e) {
            ctx.attribute("ErrorMessage", new OrderCreationException(e.getMessage()));
            ctx.render("error.html");
        } catch (DatabaseException e) {
            ctx.attribute("ErrorMessage", new DatabaseException(e.getMessage()));
            ctx.render("error.html");
        }
        ctx.render("tak.html");
    }
}