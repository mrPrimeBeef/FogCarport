package app.controllers;

import app.dto.OverviewOrderAccountDto;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("saelgeralleorder", ctx -> salesrepShowAllOrdersPage(ctx, connectionPool));
    }

    static void salesrepShowAllOrdersPage(Context ctx, ConnectionPool connectionPool) {
//        Account currentUser = ctx.sessionAttribute("currentUser");
//        if (currentUser == null || !currentUser.getRole().equals("admin")) {
//            ctx.attribute("errorMessage", "Kun adgang for admin.");
//            ctx.render("error.html");
//            return;
//        }

        try {
            ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = OrderMapper.getOverviewOrderAccountDtos(connectionPool);
            ctx.attribute("OverviewOrderAccountDtos", OverviewOrderAccountDtos);
            ctx.render("saelgeralleorder.html");
        } catch (DatabaseException e) {
            ctx.attribute("errorMessage", e.getMessage());
            ctx.render("error.html");
        }
    }
}