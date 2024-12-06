package app.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.config.LoggerConfig;
import app.dto.DetailOrderAccountDto;
import app.dto.OverviewOrderAccountDto;
import app.entities.EmailReceipt;
import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.persistence.OrderMapper;
import app.persistence.AccountMapper;
import app.persistence.ConnectionPool;


import app.services.svgEngine.CarportSvg;
import app.services.StructureCalculationEngine.Entities.Carport;

public class OrderController {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index"));
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> postCarportCustomerInfo(ctx, connectionPool));
        app.get("saelgeralleordrer", ctx -> salesrepShowAllOrdersPage(ctx, connectionPool));
        app.get("saelgerordre", ctx -> salesrepShowOrderPage(ctx, connectionPool));
        app.post("daekningsgrad", ctx -> salesrepPostMarginPercentage(ctx, connectionPool));
        app.post("carportberegning", ctx -> salesrepPostCarportCalculation(ctx, connectionPool));
    }

    private static void postCarportCustomerInfo(Context ctx, ConnectionPool connectionPool) {
        int carportWidth = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLength = Integer.parseInt(ctx.formParam("carport-laengde"));
        int shedWidth = Integer.parseInt(ctx.formParam("redskabsrum-bredde"));
        int shedLength = Integer.parseInt(ctx.formParam("redskabsrum-laengde"));
        String notes = ctx.formParam("bemaerkninger");

        String name = ctx.formParam("navn");
        String address = ctx.formParam("adresse");
        int zip = Integer.parseInt(ctx.formParam("postnummer"));
        String city = ctx.formParam("by");
        String phone = ctx.formParam("telefon");
        String email = ctx.formParam("email");
        try {
            int accountId = createOrGetAccountId(email, name, address, zip, phone, ctx, connectionPool);
            OrderMapper.createOrder(accountId, carportWidth, carportLength, shedWidth, shedLength, connectionPool);

            new EmailReceipt(carportWidth, carportLength, shedWidth, shedLength, notes, name, address, zip, city, phone, email);
        } catch (AccountException | OrderException | DatabaseException e) {

            LOGGER.severe("Fejl ved posting af carport info: " + e.getMessage());

            ctx.attribute("ErrorMessage", e.getMessage());
            ctx.render("error.html");
        }
        showThankYouPage(name, email, ctx);
    }

    static void salesrepShowAllOrdersPage(Context ctx, ConnectionPool connectionPool) {
        Account activeAccount = ctx.sessionAttribute("activeAccount");
        if (activeAccount == null || !activeAccount.getRole().equals("salesrep")) {

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

    private static int createOrGetAccountId(String email, String name, String address, int zip, String phone, Context ctx, ConnectionPool connectionPool) throws DatabaseException, AccountException {
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

    private static void showThankYouPage(String name, String email, Context ctx) {
        ctx.attribute("navn", name);
        ctx.attribute("email", email);
        ctx.render("tak.html");
    }

    // TODO: Fix the exception handling to show error page
    private static void salesrepShowOrderPage(Context ctx, ConnectionPool connectionPool) {

        // TODO: Tilføj guard condition her

        int orderId = Integer.parseInt(ctx.queryParam("ordrenr"));

        try {
            DetailOrderAccountDto detailOrderAccountDto = OrderMapper.getDetailOrderAccountDtoByOrderId(orderId, connectionPool);

            // TODO: Disse beregninger skal evt. ligges ud i en service klasse. Så bliver de også muligt at unit teste
            double costPrice = 10000;  // TODO: Skal beregnes ved at summere cost price fra orderlines, f.eks: OrderlineMapper.getSumCostPriceByOrderId(orderId, connectionPool)
            double marginPercentage = detailOrderAccountDto.getMarginPercentage();
            double salePrice = 100 * costPrice / (100 - marginPercentage);
            double marginAmount = salePrice - costPrice;
            double salePriceInclVAT = 1.25 * salePrice;

            ctx.attribute("detailOrderAccountDto", detailOrderAccountDto);
            ctx.attribute("costPrice", costPrice);
            ctx.attribute("marginAmount", marginAmount);
            ctx.attribute("salePrice", salePrice);
            ctx.attribute("salePriceInclVAT", salePriceInclVAT);
            ctx.render("saelgerordre.html");

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        ctx.render("saelgerordre.html");
    }

    // TODO: Fix the exception handling to show error page
    private static void salesrepPostMarginPercentage(Context ctx, ConnectionPool connectionPool) {

        // TODO: Tilføj guard condition her

        int orderId = Integer.parseInt(ctx.formParam("ordrenr"));
        Double marginPercentage = Double.parseDouble(ctx.formParam("daekningsgrad"));

        try {
            OrderMapper.updateMarginPercentage(orderId, marginPercentage, connectionPool);
            ctx.redirect("saelgerordre?ordrenr=" + orderId);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

    }

    // TODO: Fix the exception handling to show error page
    private static void salesrepPostCarportCalculation(Context ctx, ConnectionPool connectionPool) {

        // TODO: Tilføj guard condition her

        int orderId = Integer.parseInt(ctx.formParam("ordrenr"));
        int carportWidthCm = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLengthCm = Integer.parseInt(ctx.formParam("carport-laengde"));
        int carportHeightCm = Integer.parseInt(ctx.formParam("carport-hoejde"));

        try {

            Carport carport = new Carport(carportWidthCm, carportLengthCm, carportHeightCm, null, false, 0, connectionPool);
            carportWidthCm = carport.getWidth();
            carportLengthCm = carport.getLength();
            carportHeightCm = carport.getHeight();
            String svgSideView = CarportSvg.sideView(carport);
            String svgTopView = CarportSvg.topView(carport);

            OrderMapper.updateCarport(orderId, carportWidthCm, carportLengthCm, carportHeightCm, svgSideView, svgTopView, connectionPool);

            ctx.redirect("saelgerordre?ordrenr=" + orderId);

        } catch (DatabaseException | SQLException e) {
            e.printStackTrace();
        }

    }

}