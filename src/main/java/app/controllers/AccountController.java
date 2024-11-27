package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;
import app.entities.Account;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;

public class AccountController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("login", ctx -> ctx.render("login.html"));
        app.post("login", ctx -> login(ctx, connectionPool));
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            Account account = AccountMapper.login(email, password, connectionPool);
            //TODO skal henvise til en admin index side.
            if (account.getRole().equals("admin")) {
                System.out.println("logget på som admin");
                return;
            }
            //TODO skal henvise til en kunde index side.
            if (account.getRole().equals("customer")) {
                System.out.println("logget på som kunde");
                return;
            }

        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}