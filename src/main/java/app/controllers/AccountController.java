package app.controllers;

import java.util.ArrayList;
import java.util.logging.Logger;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.config.LoggerConfig;
import app.dto.DetailOrderAccountDto;
import app.services.PasswordGenerator;
import app.entities.Order;
import app.entities.Orderline;
import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.persistence.OrderlineMapper;
import app.persistence.ConnectionPool;
import app.persistence.AccountMapper;
import app.persistence.OrderMapper;

public class AccountController {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("login", ctx -> ctx.render("login.html"));
        app.post("login", ctx -> login(ctx, connectionPool));
        app.get("logout", ctx -> logout(ctx));
        app.get("glemtkode", ctx -> ctx.render("glemtkode.html"));
        app.post("glemtkode", ctx -> forgotPassword(ctx, connectionPool));
        app.get("opdaterkundeinfo", ctx -> ctx.render("opdaterkundeinfo.html"));
        app.post("opdaterkundeinfo", ctx -> setNewPassword(ctx, connectionPool));
        app.get("kundeside", ctx -> showCustomerOverview(ctx, connectionPool));
        app.get("kundesideordre", ctx -> showCustomerOrderPage(ctx, connectionPool));
        app.post("koebordre", ctx -> buyOrder(ctx, connectionPool));
        app.get("saelgerallekunder", ctx -> salesrepShowAllCustomersPage(ctx, connectionPool));
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            Account activeAccount = AccountMapper.login(email, password, connectionPool);
            if (activeAccount == null) {
                ctx.attribute("message", "Forkert email eller adgangskode");
                ctx.render("login.html");
                return;
            }

            ctx.sessionAttribute("account", activeAccount);

            switch (activeAccount.getRole()) {
                case "salesrep":
                    ctx.redirect("saelgeralleordrer");
                    break;
                case "Kunde":
                    ctx.redirect("kundeside");
                    break;
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
            ArrayList<String> emails = AccountMapper.getAllAccountEmails(connectionPool);

            if (!emails.contains(email)) {
                ctx.attribute("errorMessage", "Ingen konto fundet for den indtastede e-mail.");
                ctx.render("glemtkode.html");
                return;
            }

            Account account = AccountMapper.getAccountByEmail(email, connectionPool);
            String role = account.getRole();

            if ("Kunde".equalsIgnoreCase(role)) {
                String newPassword = PasswordGenerator.generatePassword();
                AccountMapper.updatePassword(email, newPassword, connectionPool);

                System.out.println("Den indtastede email: " + email + "\n" + "Adgangskoden for den indtastede mail er: " + newPassword);

                ctx.attribute("message", "Din adgangskode er blevet nulstillet. Log ind med den nye adgangskode.");
                ctx.render("login.html");
            } else {
                ctx.attribute("errorMessage", "Ingen konto fundet for den indtastede email. Prøv igen.");
                ctx.render("glemtkode.html");
            }
        } catch (AccountException | DatabaseException e) {
            ctx.attribute("errorMessage", "Error in forgotPassword() " + e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void setNewPassword(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"Kunde".equalsIgnoreCase(activeAccount.getRole())) {
            ctx.attribute("errorMessage", "Du skal være logget ind for at ændre din adgangskode.");
            ctx.render("login.html");
            return;
        }

        String email = activeAccount.getEmail();
        String currentPassword = ctx.formParam("currentPassword");
        String newPassword1 = ctx.formParam("newPassword1");
        String newPassword2 = ctx.formParam("newPassword2");

        try {
            Account account = AccountMapper.getPasswordByEmail(email, connectionPool);

            if (account == null) {
                ctx.attribute("errorMessage", "Ingen konto fundet. Prøv igen.");
                ctx.render("login.html");
                return;
            }

            if (account.getPassword() == null) {
                ctx.attribute("errorMessage", "Ingen adgangskode fundet til denne konto.");
                ctx.render("opdaterkundeinfo.html");
                return;
            }

            if (!account.getPassword().equals(currentPassword)) {
                ctx.attribute("errorMessage", "Den nuværende adgangskode er forkert.");
                ctx.render("opdaterkundeinfo.html");
                return;
            }

            if (!newPassword1.equals(newPassword2)) {
                ctx.attribute("errorMessage", "De nye adgangskoder matcher ikke.");
                ctx.render("opdaterkundeinfo.html");
                return;
            }

            AccountMapper.updatePassword(email, newPassword1, connectionPool);

            System.out.println("Den indtastede email: " + email + "\n" + "Din nye adgangskode er: " + newPassword1);

            ctx.attribute("successMessage", "Din adgangskode er blevet opdateret.");
            ctx.render("opdaterkundeinfo.html");

        } catch (AccountException e) {
            ctx.attribute("errorMessage", "Error in setNewPassword() " + e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void showCustomerOverview(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"Kunde".equalsIgnoreCase(activeAccount.getRole())) {
            ctx.attribute("errorMessage", "Du er ikke logget ind");
            ctx.render("error.html");
            return;
        }
        try {
            ArrayList<Order> orders = OrderMapper.getOrdersFromAccountId(activeAccount.getAccountId(), connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("kundeside.html");
        } catch (OrderException e) {
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void showCustomerOrderPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"Kunde".equalsIgnoreCase(activeAccount.getRole())) {
            ctx.attribute("errorMessage", "Du er ikke logget ind");
            ctx.render("error.html");
            return;
        }

        try {
            int orderId = Integer.parseInt(ctx.queryParam("ordrenr"));

            DetailOrderAccountDto detailOrderAccountDto = OrderMapper.getDetailOrderAccountDtoByOrderId(orderId, connectionPool);
            if (activeAccount.getAccountId() != detailOrderAccountDto.getAccountId()) {
                ctx.attribute("errorMessage", "Du har ikke adgang til at se denne ordre");
                ctx.render("error.html");
                return;
            }

            ArrayList<Orderline> orderlines = OrderlineMapper.getOrderlinesForCustomerOrSalesrep(orderId, activeAccount.getRole(), connectionPool);

            ctx.attribute("detailOrderAccountDto", detailOrderAccountDto);
            ctx.attribute("orderlines", orderlines);
            ctx.render("kundesideordre.html");

        } catch (OrderException | DatabaseException e) {
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }

    }

    private static void buyOrder(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"Kunde".equalsIgnoreCase(activeAccount.getRole())) {
            ctx.attribute("errorMessage", "Kun adgang for kunder");
            ctx.render("error.html");
            return;
        }
        try {
            int orderId = Integer.parseInt(ctx.formParam("ordrenr"));
            OrderMapper.updateIsPaid(orderId, connectionPool);

            ctx.redirect("kundesideordre?ordrenr=" + orderId);
        } catch (OrderException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static void salesrepShowAllCustomersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"salesrep".equalsIgnoreCase(activeAccount.getRole())) {
            LOGGER.warning("Uautoriseret adgangsforsøg til kundeliste. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }

        try {
            LOGGER.info("Sælger henter kundeliste. sælger: " + activeAccount.getAccountId());

            ArrayList<Account> accounts = AccountMapper.getAllCustomerAccounts(connectionPool);
            ctx.attribute("accounts", accounts);
            ctx.render("saelgerallekunder.html");
        } catch (AccountException e) {
            LOGGER.severe("Fejl ved hentning af kundeliste: " + e.getMessage());
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }
}