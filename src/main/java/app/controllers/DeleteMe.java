package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class DeleteMe {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("svg", ctx -> showSvg(ctx, connectionPool));
    }

    private static void showSvg(Context ctx, ConnectionPool connectionPool) {

        ctx.render("show_svg.html");
    }

}
