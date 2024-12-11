package app.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.persistence.*;
import app.services.SalePriceCalculator;
import app.config.LoggerConfig;
import app.dto.DetailOrderAccountDto;
import app.dto.OverviewOrderAccountDto;
import app.entities.Account;
import app.entities.EmailReceipt;
import app.entities.Orderline;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.persistence.AccountMapper;
import app.services.svgEngine.CarportSvg;
import app.services.StructureCalculationEngine.Entities.Carport;
import app.services.StructureCalculationEngine.Entities.Material;

public class OrderController {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("fladttag", ctx -> ctx.render("fladttag.html"));
        app.post("fladttag", ctx -> postCarportCustomerInfo(ctx, connectionPool));
        app.get("saelgerordre", ctx -> salesrepShowOrderPage(ctx, connectionPool));
        app.post("sendbrugerinfo", ctx -> sendCustomerInfo(ctx, connectionPool));
        app.post("opdaterstatus", ctx -> salesrepPostStatus(ctx, connectionPool));
        app.post("daekningsgrad", ctx -> salesrepPostMarginPercentage(ctx, connectionPool));
        app.post("carportberegning", ctx -> salesrepPostCarportCalculation(ctx, connectionPool));
        app.get("saelgeralleordrer", ctx -> salesrepShowAllOrdersPage(ctx, connectionPool));
    }

    private static void postCarportCustomerInfo(Context ctx, ConnectionPool connectionPool) {
        int carportWidth = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLength = Integer.parseInt(ctx.formParam("carport-laengde"));
        int carportHeight = Integer.parseInt(ctx.formParam("carport-hoejde"));

        // Feature not ready yet
//        int shedWidth = Integer.parseInt(ctx.formParam("redskabsrum-bredde"));
//        int shedLength = Integer.parseInt(ctx.formParam("redskabsrum-laengde"));
//        String notes = ctx.formParam("bemaerkninger");

        String name = ctx.formParam("navn");
        String address = ctx.formParam("adresse");
        int zip = Integer.parseInt(ctx.formParam("postnummer"));
        String phone = ctx.formParam("telefon");
        String email = ctx.formParam("email");

        ArrayList<Integer> validPostnumre = AccountMapper.getAllZips(connectionPool);

        if (!validPostnumre.contains(zip)) {
            ctx.attribute("ErrorMessage", "Postnummeret skal være et gyldigt.");
            ctx.render("fladttag.html");
            return;
        }

        try {
            int accountId = createOrGetAccountId(email, name, address, zip, phone, ctx, connectionPool);
            OrderMapper.createOrder(accountId, carportWidth, carportLength, carportHeight, connectionPool);

            new EmailReceipt(carportWidth, carportLength, carportHeight, name, address, zip, phone, email);
        } catch (AccountException | OrderException | DatabaseException e) {

            LOGGER.severe("Fejl ved posting af carport info: " + e.getMessage());

            ctx.attribute("ErrorMessage", e.getMessage());
            ctx.render("error.html");
        }
        showThankYouPage(name, email, ctx);
    }

    private static void salesrepShowOrderPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"salesrep".equalsIgnoreCase(activeAccount.getRole())) {
            LOGGER.warning("Uautoriseret adgangsforsøg til ordresiden for sælgere. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }

        int orderId = Integer.parseInt(ctx.queryParam("ordrenr"));

        try {
            DetailOrderAccountDto detailOrderAccountDto = OrderMapper.getDetailOrderAccountDtoByOrderId(orderId, connectionPool);
            int accountId = detailOrderAccountDto.getAccountId();
            String role = activeAccount.getRole();
            ArrayList<Orderline> orderlines = OrderlineMapper.getOrderlinesForCustomerOrSalesrep(accountId, role, connectionPool);

            double costPrice = OrderlineMapper.getTotalCostPriceFromOrderId(orderId, connectionPool);
            double marginPercentage = detailOrderAccountDto.getMarginPercentage();
            double marginAmount = SalePriceCalculator.calculateMarginAmount(costPrice, marginPercentage);
            double salePrice = SalePriceCalculator.calculateSalePrice(costPrice, marginPercentage);
            double salePriceInclVAT = SalePriceCalculator.calculateSalePriceInclVAT(costPrice, marginPercentage);

            ctx.attribute("detailOrderAccountDto", detailOrderAccountDto);
            ctx.attribute("orderlines", orderlines);
            ctx.attribute("costPrice", costPrice);
            ctx.attribute("marginAmount", marginAmount);
            ctx.attribute("salePrice", salePrice);
            ctx.attribute("salePriceInclVAT", salePriceInclVAT);
            ctx.render("saelgerordre.html");

        } catch (DatabaseException | OrderException e) {
            LOGGER.severe(e.getMessage());
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }

    }

    public static void sendCustomerInfo(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"salesrep".equalsIgnoreCase(activeAccount.getRole())) {
            LOGGER.warning("Uautoriseret adgangsforsøg til at prøve at sende kundeinfo. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }

        try {
            int accountId = Integer.parseInt(ctx.formParam("accountId"));
            Account account = AccountMapper.getPasswordAndEmail(accountId, connectionPool);

            // Sending mock email via System.out.println
            System.out.println("Her er din fog konto:");
            System.out.println("Dit brugernavn: " + account.getEmail());
            System.out.println("Dit kodeord: " + account.getPassword());

            int orderId = Integer.parseInt(ctx.formParam("ordrenr"));
            ctx.redirect("saelgerordre?ordrenr=" + orderId);

        } catch (AccountException e) {
            LOGGER.severe(e.getMessage());
            ctx.attribute("message", "Error in sendCustomerInfo " + e.getMessage());
            ctx.render("error.html");
        }
    }

    private static void salesrepPostStatus(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"salesrep".equalsIgnoreCase(activeAccount.getRole())) {
            LOGGER.warning("Uautoriseret adgangsforsøg til at prøve at ændre status på en ordre. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }

        int orderId = Integer.parseInt(ctx.formParam("ordrenr"));
        String status = ctx.formParam("status");
        boolean isDone = false;

        if (status.equalsIgnoreCase("afsluttet") || status.equalsIgnoreCase("annulleret")) {
            isDone = true;
        }

        try {
            OrderMapper.updateStatus(orderId, status, isDone, connectionPool);
            ctx.redirect("saelgerordre?ordrenr=" + orderId);
        } catch (DatabaseException e) {
            LOGGER.severe(e.getMessage());
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }

    private static void salesrepPostMarginPercentage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"salesrep".equalsIgnoreCase(activeAccount.getRole())) {
            LOGGER.warning("Uautoriseret adgangsforsøg til at ændre dækningsgrad. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }

        int orderId = Integer.parseInt(ctx.formParam("ordrenr"));
        Double marginPercentage = Double.parseDouble(ctx.formParam("daekningsgrad"));

        try {
            OrderMapper.updateMarginPercentage(orderId, marginPercentage, connectionPool);
            ctx.redirect("saelgerordre?ordrenr=" + orderId);
        } catch (DatabaseException e) {
            LOGGER.severe(e.getMessage());
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }

    }

    private static void salesrepPostCarportCalculation(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"salesrep".equalsIgnoreCase(activeAccount.getRole())) {
            LOGGER.warning("Uautoriseret adgangsforsøg til at køre carport beregning. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }

        int orderId = Integer.parseInt(ctx.formParam("ordrenr"));
        int carportWidthCm = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLengthCm = Integer.parseInt(ctx.formParam("carport-laengde"));
        int carportHeightCm = Integer.parseInt(ctx.formParam("carport-hoejde"));

        try {

            Carport carport = new Carport(carportWidthCm, carportLengthCm, carportHeightCm, null, false, 0, connectionPool);
            Map<Material, Integer> partsList = carport.getPartsList();
            carportWidthCm = carport.getWidth();
            carportLengthCm = carport.getLength();
            carportHeightCm = carport.getHeight();
            String svgSideView = CarportSvg.sideView(carport);
            String svgTopView = CarportSvg.topView(carport);

            OrderMapper.updateCarport(orderId, carportWidthCm, carportLengthCm, carportHeightCm, svgSideView, svgTopView, connectionPool);
            OrderlineMapper.deleteOrderlinesFromOrderId(orderId, connectionPool);
            OrderlineMapper.addOrderlines(orderId, partsList, connectionPool);

            ctx.redirect("saelgerordre?ordrenr=" + orderId);

        } catch (DatabaseException | SQLException e) {
            LOGGER.severe(e.getMessage());
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }

    static void salesrepShowAllOrdersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"salesrep".equalsIgnoreCase(activeAccount.getRole())) {
            LOGGER.warning("Uautoriseret adgangsforsøg til kundeliste. Rolle: " +
                    (activeAccount != null ? activeAccount.getRole() : "Ingen konto"));
            ctx.attribute("errorMessage", "Kun adgang for sælgere.");
            ctx.render("error.html");
            return;
        }
        try {
            ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = OrderMapper.getOverviewOrderAccountDtos(connectionPool);
            ctx.attribute("OverviewOrderAccountDtos", OverviewOrderAccountDtos);
            ctx.render("saelgeralleordrer.html");
        } catch (DatabaseException e) {
            LOGGER.severe("Fejl ved hentning af alle kunders ordre: " + e.getMessage());

            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }

    private static void showThankYouPage(String name, String email, Context ctx) {
        Account activeAccount = ctx.sessionAttribute("account");
        if (activeAccount == null || !"Kunde".equalsIgnoreCase(activeAccount.getRole())) {
            ctx.attribute("errorMessage", "Kun adgang for kunder");
            ctx.render("error.html");
            return;
        }
        ctx.attribute("navn", name);
        ctx.attribute("email", email);
        ctx.render("tak.html");
    }

    private static int createOrGetAccountId(String email, String name, String address, int zip, String
            phone, Context ctx, ConnectionPool connectionPool) throws DatabaseException, AccountException {
        int accountId;
        boolean allreadyUser = false;
        ArrayList<String> emails = AccountMapper.getAllAccountEmails(connectionPool);

        for (String mail : emails) {
            if (mail.equals(email)) {
                allreadyUser = true;
            }
        }

        if (!allreadyUser) {
            return accountId = AccountMapper.createAccount(name, address, zip, phone, email, connectionPool);
        }
        return AccountMapper.getAccountIdFromEmail(email, connectionPool);
    }

}