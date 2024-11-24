package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.persistence.ConnectionPool;
import app.services.svg.CarportSvg;

public class DeleteMe {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("svg", ctx -> showSvg(ctx, connectionPool));
    }

    private static void showSvg(Context ctx, ConnectionPool connectionPool) {

        int carportLengthCm = 780;
        int carportWidthCm = 600;
        int carportHeightCm = 0;

        ctx.attribute("carportTopView", CarportSvg.topView(carportLengthCm, carportWidthCm));
        ctx.render("show_svg.html");
    }

}
