package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

import app.persistence.ConnectionPool;
import app.services.svgEngine.CarportSvg;
import app.services.StructureCalculationEngine.Entities.Carport;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("saelgerordre", ctx -> salesrepShowOrderPage(ctx, connectionPool));
    }

    private static void salesrepShowOrderPage(Context ctx, ConnectionPool connectionPool) {

        int carportLengthCm = 780;
        int carportWidthCm = 600;
        int carportHeightCm = 0;


        Carport carport = new Carport(carportWidthCm, carportLengthCm, carportHeightCm, null, false, 0, connectionPool);

        System.out.println(carport.getLength());
        System.out.println(carport.getWidth());
        System.out.println(carport.getHeight());



        ctx.attribute("carportSvgTopView", CarportSvg.topView(carportLengthCm, carportWidthCm));
        ctx.render("saelgerordre.html");
    }

}
