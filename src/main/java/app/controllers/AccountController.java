package app.controllers;

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
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            Account account = AccountMapper.login(email, password, connectionPool);
            if (account.getRole().equals("salesrep")) {
                ctx.render("saelgeralleorder");
                return;
            }
            if (account.getRole().equals("customer")) {
                ctx.attribute("customer", account);
                ctx.render("/kundeside");
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