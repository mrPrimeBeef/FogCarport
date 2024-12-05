package app.controllers;

import java.util.ArrayList;

import app.exceptions.AccountException;
import app.services.PasswordGenerator;
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
        app.get("opdaterKundeInfo", ctx -> ctx.render("opdaterKundeInfo"));
        app.post("opdaterKundeInfo", ctx -> setNewPassword(ctx, connectionPool));
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
                salesrepShowAllCustomersPage(ctx, connectionPool);
                return;
            }
            //TODO skal henvise til en kunde index side i ctx.render.
            if (account.getRole().equals("Kunde")) {
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

    public static void setNewPassword(Context ctx, ConnectionPool connectionPool) {
        String currentPassword = ctx.formParam("currentPassword");
        String newPassword1 = ctx.formParam("newPassword");
        String newPassword2 = ctx.formParam("newPassword");

        String email = ctx.sessionAttribute("email");

        if (email == null) {
            ctx.attribute("errorMessage", "Du skal være logget ind for at ændre din adgangskode.");
            ctx.render("opdaterKundeInfo.html");
            return;
        }
        try {
            Account account = AccountMapper.getAccountByEmail(email, connectionPool);

            if (!account.getPassword().equals(currentPassword)) {
                ctx.attribute("errorMessage", "Den nuværende adgangskode er forkert.");
                ctx.render("opdaterKundeInfo.html");
                return;
            }
            if (!newPassword1.equals(newPassword2)) {
                ctx.attribute("errorMessage", "De nye adgangskoder matcher ikke.");
                ctx.render("opdaterKundeInfo.html");
                return;
            }
            AccountMapper.updatePassword(email, newPassword1, connectionPool);

            ctx.attribute("successMessage", "Adgangskoden blev opdateret.");
            ctx.render("opdaterKundeInfo.html");

        } catch (AccountException e) {
            ctx.attribute("errorMessage", "Fejl ved opdateringen af adgangskoden: " + "Error in setNewPassword" + e.getMessage());
            ctx.render("opdaterKundeInfo.html");
        }
    }
}