package app.controllers;

import java.util.ArrayList;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.entities.Account;
import app.exceptions.DatabaseException;
import app.exceptions.AccountException;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;

public class AccountController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("login", ctx -> ctx.render("login"));
        app.post("login", ctx -> login(ctx, connectionPool));
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
            if (account.getRole().equals("salesrep")) {
                ctx.render("saelgeralleordrer");
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
}