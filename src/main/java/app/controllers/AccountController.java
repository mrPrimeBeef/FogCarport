package app.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import app.config.LoggerConfig;
import app.services.PasswordGenerator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import app.entities.Order;
import app.entities.Orderline;
import app.services.StructureCalculationEngine.Entities.Carport;
import app.services.svgEngine.CarportSvg;
import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.persistence.OrderlineMapper1;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;
import app.persistence.OrderMapper;

public class AccountController {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("login", ctx -> ctx.render("login"));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("kundeside", ctx -> showCustomerOverview(ctx, connectionPool));
        app.get("kundesideordre", ctx -> showCustomerOrderPage(ctx, connectionPool));
        app.get("glemtKode", ctx -> ctx.render("glemtKode"));
        app.post("glemtKode", ctx -> forgotPassword(ctx, connectionPool));
        app.get("logout",ctx->logout(ctx));
        app.get("saelgerallekunder", ctx -> salesrepShowAllCustomersPage(ctx, connectionPool));
    }

    public static void salesrepShowAllCustomersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
      
        if (activeAccount == null || !activeAccount.getRole().equals("salesrep")) {

            LOGGER.warning("Uautoriseret adgangsforsøg til kundeliste. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));

            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }
        try {
            LOGGER.info("Sælger henter kundeliste. sælger: " + activeAccount.getAccountId());

            ArrayList<Account> accounts = AccountMapper.getAllAccounts(connectionPool);
            ctx.attribute("accounts", accounts);
            ctx.render("saelgerallekunder.html");
        } catch (AccountException e) {
            LOGGER.severe("Fejl ved hentning af kundeliste: " + e.getMessage());
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            Account account = AccountMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("account", account);
            if (account.getRole().equals("salesrep")) {
                OrderController.salesrepShowAllOrdersPage(ctx, connectionPool);
                return;
            }
            if (account.getRole().equals("Kunde")) {
                showCustomerOverview(ctx, connectionPool);
            }

        } catch (AccountException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    public static void showCustomerOverview(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");

        if (activeAccount == null || !activeAccount.getRole().equals("Kunde")) {
            ctx.attribute("Du er ikke logget ind");
            ctx.render("/error");
            return;
        } else{
            try {
                ArrayList<Order> orders = OrderMapper.getOrdersFromAccountId(activeAccount.getAccountId(), connectionPool);
                ctx.attribute("showOrders", orders);

            } catch (OrderException e) {
                ctx.attribute(e.getMessage());
                ctx.render("/error");
            }
            ctx.render("/kundeside");
        }
    }

    public static void showCustomerOrderPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");

        if (activeAccount == null || !activeAccount.getRole().equals("Kunde")) {
            ctx.attribute("Du er ikke logget ind");
            ctx.render("/error");
            return;
        } else {
            // TODO fix med rigtig måde at vise?
            int carportLengthCm = 752;
            int carportWidthCm = 600;
            int carportHeightCm = 210;
            Carport carport = new Carport(carportWidthCm, carportLengthCm, carportHeightCm, null, false, 0, connectionPool);

            // TODO

            try {
                int orderrId = Integer.parseInt(ctx.queryParam("orderId"));

                carport.getPlacedMaterials();
                OrderlineMapper1.deleteOrderlinesFromOrderId(orderrId, connectionPool);
                OrderlineMapper1.addOrderlines(carport.getPartsList(), orderrId, connectionPool);

                Order orders = OrderMapper.getCustomerOrder(orderrId, connectionPool);
                ctx.attribute("showOrder", orders);


                ArrayList<Orderline> orderlines = OrderlineMapper1.getMaterialListForCustomerOrSalesrep(activeAccount.getAccountId(), activeAccount.getRole(), connectionPool);
                ctx.attribute("showOrderlines", orderlines);

                ctx.attribute("carportSvgSideView", CarportSvg.sideView(carport));
                ctx.attribute("carportSvgTopView", CarportSvg.topView(carport));

            } catch (OrderException | DatabaseException | SQLException e) {
                ctx.attribute(e.getMessage());
                ctx.render("/error");
            }
            ctx.render("/kundesideordre");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    public static void forgotPassword(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");

        try {
            Account account = AccountMapper.getAccountByEmail(email, connectionPool);

            if (account == null) {
                ctx.attribute("errorMessage", "Ingen konto fundet for den indtastede e-mail.");
                ctx.render("glemtKode.html");
                return;
            }
            String role = account.getRole();

            if ("salesrep".equals(role)) {
                ctx.attribute("message", "Sælgere kan ikke få adgang til deres adgangskode. Kontakt admin for at ændre adgangskoden.");
                ctx.render("glemtKode.html");
                return;
            }

            if ("Kunde".equals(role)) {
                String newPassword = PasswordGenerator.generatePassword();
                AccountMapper.updatePassword(email, newPassword, connectionPool);

                System.out.println("Den indtastede e-mail: " + email + "\n" + "Adgangskoden for den indtastede mail er: " + newPassword);

                ctx.attribute("message", "Din adgangskode er blevet nulstillet. Log ind med den nye adgangskode.");
                ctx.render("login.html");
            } else {
                ctx.attribute("errorMessage", "Ingen konto fundet for den indtastede e-mail. Prøv igen.");
                ctx.render("glemtKode.html");
            }
        } catch (AccountException e) {
            ctx.attribute("message", "Error in forgotPassword " + e.getMessage());
            ctx.render("error.html");
        }
    }
}