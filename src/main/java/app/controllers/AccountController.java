package app.controllers;

import java.util.ArrayList;
import java.util.logging.Logger;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.config.LoggerConfig;
import app.entities.Account;
import app.services.PasswordGenerator;
import app.exceptions.DatabaseException;
import app.exceptions.AccountException;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;

public class AccountController {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("login", ctx -> ctx.render("login"));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("glemtKode", ctx -> ctx.render("glemtKode"));
        app.post("glemtKode", ctx -> forgotPassword(ctx, connectionPool));
        app.get("kundeside", ctx -> showKundeside(ctx));
        app.get("logout",ctx->logout(ctx));
        app.get("saelgerallekunder", ctx -> salesrepShowAllCustomersPage(ctx, connectionPool));
    }

    public static void salesrepShowAllCustomersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("activeAccount");
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
        } catch (DatabaseException e) {
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
            ctx.sessionAttribute("activeAccount", account);
            if (account.getRole().equals("salesrep")) {
                salesrepShowAllCustomersPage(ctx, connectionPool);
                return;
            }
           if (account.getRole().equals("Kunde")) {
                ctx.sessionAttribute("account", account);
                ctx.render("/kundeside");
            }

        } catch (AccountException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    private static void showKundeside(Context ctx) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null) {
            ctx.attribute("Du er ikke logget ind");
            ctx.render("/error");
            return;
        }
        if (activeAccount.getRole().equals("Kunde")) {
            ctx.render("/kundeside");
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