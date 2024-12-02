package app.controllers;

import java.util.ArrayList;

import app.exceptions.AccountException;
import io.javalin.Javalin;
import io.javalin.http.Context;

import app.entities.Account;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;

public class AccountController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("login", ctx -> ctx.render("login"));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("glemtKode", ctx -> ctx.render("glemtKode"));
        app.post("glemtKode", ctx -> forgotPassword(ctx, connectionPool));
        app.get("saelgerallekunder", ctx -> salesrepShowAllCustomersPage(ctx, connectionPool));
    }
  
  public static void salesrepShowAllCustomersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("activeAccount");
        if (activeAccount == null || !activeAccount.getRole().equals("salesrep")) {
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }
        try {
            ArrayList<Account> accounts = AccountMapper.getAllAccounts(connectionPool);
            ctx.attribute("accounts", accounts);
            ctx.render("saelgerallekunder.html");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            Account account = AccountMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("activeAccount", account);
            if (account.getRole().equals("salesrep")) {
                salesrepShowAllCustomersPage(ctx,connectionPool);
                return;
            }
            //TODO skal henvise til en kunde index side i ctx.render.
            if (account.getRole().equals("customer")) {
                ctx.render("/index");
                return;
            }

        } catch (AccountException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
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
                ctx.attribute("errorMessage", "Ingen konto fundet for den angivne e-mail.");
                ctx.render("error.html");
                return;
            }

            String role = account.getRole();
            if ("salesrep".equals(role)) {
                ctx.attribute("message", "Sælgere kan ikke få adgang til deres adgangskode. Kontakt en administrator.");
                ctx.render("glemtKode.html");
            } else if ("customer".equals(role)) {
                String password = AccountMapper.getPasswordByEmail(email, connectionPool);
                ctx.attribute("message", "Adgangskoden for " + email + " er: " + password);
                ctx.render("glemtKode.html");
            } else {
                ctx.attribute("errorMessage", "Ugyldig brugertype. Kontakt support.");
                ctx.render("error.html");
            }

        } catch (AccountException e) {
            ctx.attribute("errorMessage", "Noget gik galt: " + e.getMessage());
            ctx.render("error.html");
        }
    }
}