package app.controllers;

import java.sql.SQLException;
import java.util.ArrayList;

import app.entities.Order;
import app.entities.Orderline;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.persistence.OrderlineMapper;
import app.services.StructureCalculationEngine.Entities.Carport;
import app.services.svgEngine.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import app.entities.Account;
import app.exceptions.AccountException;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;
import app.persistence.OrderMapper;

public class AccountController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("login", ctx -> ctx.render("login"));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("kundeside", ctx -> showCustomerOverview(ctx, connectionPool));
        app.get("kundesideordre", ctx -> showCustomerOrderPage(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
        app.get("saelgerallekunder", ctx -> salesrepShowAllCustomersPage(ctx, connectionPool));
    }

    public static void salesrepShowAllCustomersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !activeAccount.getRole().equals("salesrep")) {
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }
        try {
            ArrayList<Account> accounts = AccountMapper.getAllAccounts(connectionPool);
            ctx.attribute("accounts", accounts);
            ctx.render("saelgerallekunder.html");
        } catch (AccountException e) {
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
                salesrepShowAllCustomersPage(ctx, connectionPool);
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

        if (activeAccount == null) {
            ctx.attribute("Du er ikke logget ind");
            ctx.render("/error");
            return;
        }
        if (activeAccount.getRole().equals("Kunde")) {
            try {
                ArrayList<Order> orders = OrderMapper.showCustomerOrders(activeAccount.getAccountId(), connectionPool);
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

        if (activeAccount == null) {
            ctx.attribute("Du er ikke logget ind");
            ctx.render("/error");
            return;
        }

        if (activeAccount.getRole().equals("Kunde")) {
            // TODO fix med rigtig måde at vise?
            int carportLengthCm = 752;
            int carportWidthCm = 600;
            int carportHeightCm = 210;
            Carport carport = new Carport(carportWidthCm, carportLengthCm, carportHeightCm, null, false, 0, connectionPool);

            // TODO

            try {
                int orderrId = Integer.parseInt(ctx.queryParam("orderId"));

                carport.getPlacedMaterials();
                OrderlineMapper.deleteOrderlinesFromOrderId(orderrId, connectionPool);
                OrderlineMapper.addOrderlines(carport.getPartsList(), orderrId, connectionPool);

                Order orders = OrderMapper.showCustomerOrder(orderrId, connectionPool);
                ctx.attribute("showOrder", orders);


                ArrayList<Orderline> orderlines = OrderlineMapper.getMaterialListForCustomerOrSalesrep(activeAccount.getAccountId(), activeAccount.getRole(), connectionPool);
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
}