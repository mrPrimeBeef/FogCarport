package app;

import app.util.EmailSender;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import app.controllers.AccountController;
import app.controllers.OrderController;
import app.config.ThymeleafConfig;
import app.persistence.ConnectionPool;

public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "fog";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            config.staticFiles.add("/templates");
        }).start(7071);

        AccountController.addRoutes(app, connectionPool);
        OrderController.addRoutes(app, connectionPool);

        try{
            EmailSender sender = new EmailSender();

            sender.sendEmail("Rolf", "williamjosephsen1993@gmail.com", "1234");
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}