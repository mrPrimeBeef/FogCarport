package app.controllers;

import java.util.ArrayList;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.dto.OverviewOrderAccountDto;
import app.entities.EmailReceipt;
import app.entities.Account;
import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;
import app.persistence.OrderMapper;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index"));
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> postCarportCustomerInfo(ctx, connectionPool));
        app.get("saelgeralleordrer", ctx -> salesrepShowAllOrdersPage(ctx, connectionPool));
    }

    private static void postCarportCustomerInfo(Context ctx, ConnectionPool connectionPool) {
        int carportWidth = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLength = Integer.parseInt(ctx.formParam("carport-laengde"));
        String trapeztag = ctx.formParam("trapeztag");
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

            new EmailReceipt(carportWidth, carportLength, trapeztag, shedWidth, shedLength, notes, name, address, zip, city, phone, email);
        } catch (AccountCreationException | OrderCreationException | DatabaseException e) {
            ctx.attribute("ErrorMessage", e.getMessage());
            ctx.render("error.html");
        }
        showThankYouPage(name, email, ctx);
    }

    static void salesrepShowAllOrdersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !activeAccount.getRole().equals("salesrep")) {

            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }

        try {
            ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = OrderMapper.getOverviewOrderAccountDtos(connectionPool);
            ctx.attribute("OverviewOrderAccountDtos", OverviewOrderAccountDtos);
            ctx.render("saelgeralleordrer.html");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
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

    private static void showThankYouPage(String name, String email, Context ctx) {
        ctx.attribute("navn", name);
        ctx.attribute("email", email);
        ctx.render("tak.html");
    }
}