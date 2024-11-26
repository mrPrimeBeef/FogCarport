package app.controllers;

import java.util.ArrayList;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.entities.EmailReceipt;
import app.entities..Account;
import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> postCarportCustomerInfo(ctx, connectionPool));
        app.get("tak", ctx -> ctx.render("tak.html"));
    }

    private static void postCarportCustomerInfo(Context ctx, ConnectionPool connectionPool) {
        int carportWidth = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLength = Integer.parseInt(ctx.formParam("carport-laengde"));
        String trapeztag = ctx.formParam("trapeztag");
        int shedWidth = Integer.parseInt(ctx.formParam("redskabsrum-bredde"));
        int shedLength = Integer.parseInt(ctx.formParam("redskabsrum-laengde"));
        String notes = ctx.formParam("bemaerkninger");

        String name = ctx.formParam("navn");
        String address  = ctx.formParam("adresse");
        int zip = Integer.parseInt(ctx.formParam("postnummer"));
        String city = ctx.formParam("by");
        String phone = ctx.formParam("telefon");
        String email = ctx.formParam("email");

        try {
            int accountId = createOrGetAccountId(email, name, address, zip, phone, ctx, connectionPool);
            OrderMapper.createOrder(accountId, carportWidth, carportLength, shedWidth, shedLength, connectionPool);

            new EmailReceipt(carportWidth, carportLength, trapeztag, shedWidth, shedLength, notes, name, address, zip, city, phone, email);

        } catch (AccountCreationException | OrderCreationException | DatabaseException e) {
            ctx.attribute("ErrorMessage", e.getMessage());
            ctx.render("error.html");
        }
        ctx.render("tak.html");
    }

    private static int createOrGetAccountId(String email, String name, String address, int zip, String phone, Context ctx, ConnectionPool connectionPool) throws DatabaseException, AccountCreationException {
        int accountId;
        boolean allreadyUser = false;
        ArrayList<String> emails;
        emails = AccountMapper.getAllAccountEmails(connectionPool);

        for (String mail : emails) {
            if (mail.equals(email)) {
                allreadyUser = true;
            }
        }

        if (!allreadyUser) {
            accountId = AccountMapper.createAccount(name, address, zip, phone, email, connectionPool);
            return accountId;
        }
        return AccountMapper.getAccountIdFromEmail(email, connectionPool);
    }

    private static void showThankYouPage(Context ctx, ConnectionPool connectionPool) {
        Account currentAccount = ctx.sessionAttribute("currentAccount");

        try {
            ArrayList<Orderline> orderlines = OrderlineMapper.getOrderlinesByOrderNumber(activeOrderNumber, connectionPool);
            if (orderlines.isEmpty()) {
                ctx.attribute("errorMessage", "Læg noget i kurven for at købe.");
                showOrderingPage(ctx, connectionPool);
                return;
            }

            double memberBalance = MemberMapper.getBalance(currentMember.getMemberId(), connectionPool);
            double totalOrderPrice = OrderMapper.getOrderPrice(activeOrderNumber, connectionPool);
            if (totalOrderPrice > memberBalance) {
                ctx.attribute("errorMessage", "Der er ikke nok penge på din konto til at gennemføre ordren.");
                ctx.render("errorAlreadyLogin.html");
                return;
            }


            ctx.attribute("activeOrderNumber", activeOrderNumber);
            ctx.render("tak.html");

        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", "Der opstod en fejl under behandlingen af din ordre.");
        }
    }
}