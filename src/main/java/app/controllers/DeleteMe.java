package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class DeleteMe {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("svg", ctx -> showSvg(ctx, connectionPool));
    }

    private static void showSvg(Context ctx, ConnectionPool connectionPool) {

//        System.out.println(String.format("%f tester %s tester", 100.0, "nej"));

        String svgText="<svg width=\"100%\" height=\"auto\" viewBox=\"0 0 7800 6000\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "    <rect width=\"100%\" height=\"100%\" fill=\"white\" />\n" +
                "    <rect x=\"0\" y=\"0\" width=\"45\" height=\"6000\" stroke=\"black\" fill=\"white\"/>\n" +
                "    <rect x=\"550\" y=\"0\" width=\"45\" height=\"6000\" stroke=\"black\" fill=\"white\"/>\n" +
                "    <rect x=\"1100\" y=\"0\" width=\"45\" height=\"6000\" stroke=\"black\" fill=\"white\"/>\n" +
                "    <rect x=\"1650\" y=\"0\" width=\"45\" height=\"6000\" stroke=\"black\" fill=\"white\"/>\n" +
                "    <rect x=\"2200\" y=\"0\" width=\"45\" height=\"6000\" stroke=\"black\" fill=\"white\"/>\n" +
                "    <rect x=\"2750\" y=\"0\" width=\"45\" height=\"6000\" stroke=\"black\" fill=\"white\"/>\n" +
                "    <rect x=\"0\" y=\"100\" width=\"6000\" height=\"45\" stroke=\"black\" fill=\"white\"/>\n" +
                "</svg>";
        ctx.attribute("drawing",svgText);
        ctx.render("show_svg.html");
    }

}
