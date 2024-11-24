package app.controllers;

import java.util.Locale;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.persistence.ConnectionPool;
import app.services.Svg;

public class DeleteMe {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("svg", ctx -> showSvg(ctx, connectionPool));
    }

    private static void showSvg(Context ctx, ConnectionPool connectionPool) {

        Locale.setDefault(new Locale("US"));

        Svg carportSvg = new Svg(0, 0, "0 0 855 690", "100%", "auto");


        for (int y = 10; y <= 600; y += 50) {
            carportSvg.addRectangle(10, y, 600, 10, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }

        carportSvg.addLine(100,100,200,400, "");

        ctx.attribute("drawing", carportSvg.toString());
        ctx.render("show_svg.html");
    }

}
