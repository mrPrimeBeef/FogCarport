package app.controllers;

import java.util.ArrayList;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.dto.OverviewOrderAccountDto;
import app.entities.EmailReceipt;
import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import app.exceptions.OrderCreationException;
import app.persistence.OrderMapper;
import app.persistence.ConnectionPool;



public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("fladttag", ctx -> ctx.render("fladttag"));
        app.post("fladttag", ctx -> postCarportCustomerInfo(ctx, connectionPool));
        app.get("saelgeralleorder", ctx -> salesrepShowAllOrdersPage(ctx, connectionPool));
    }

    private static void postCarportCustomerInfo(Context ctx, ConnectionPool connectionPool) {
        int carportWidth = Integer.parseInt(ctx.formParam("carport-bredde"));
        int carportLength = Integer.parseInt(ctx.formParam("carport-laengde"));
        String trapeztag = ctx.formParam("trapeztag");
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

            new EmailReceipt(carportWidth, carportLength, trapeztag, shedWidth, shedLength, notes, name, address, zip, city, phone, email);
        } catch (AccountCreationException | OrderCreationException | DatabaseException e) {
            ctx.attribute("ErrorMessage", e.getMessage());
            ctx.render("error.html");
        }




                ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = OrderMapper.getOverviewOrderAccountDtos(connectionPool);
                ctx.attribute("OverviewOrderAccountDtos", OverviewOrderAccountDtos);
                ctx.render("saelgeralleorder.html");
            } catch (DatabaseException e) {
                ctx.attribute("errorMessage", e.getMessage());
                ctx.render("error.html");
            }
            ctx.render("tak.html");
        }
    static void salesrepShowAllOrdersPage (Context ctx, ConnectionPool connectionPool) {
//        Account currentUser = ctx.sessionAttribute("currentUser");
//        if (currentUser == null || !currentUser.getRole().equals("admin")) {
//            ctx.attribute("errorMessage", "Kun adgang for admin.");
//            ctx.render("error.html");
//            return;
//        }
    }


}