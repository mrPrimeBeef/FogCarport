package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ArrayList;
import java.util.logging.Logger;

import app.entities.Order;
import app.dto.DetailOrderAccountDto;
import app.dto.OverviewOrderAccountDto;
import app.exceptions.DatabaseException;
import app.config.LoggerConfig;
import app.exceptions.OrderException;
import app.services.SalePriceCalculator;

public class OrderMapper {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    public static ArrayList<OverviewOrderAccountDto> getOverviewOrderAccountDtos(ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = new ArrayList<>();

        String sql = "SELECT orderr_id, account_id, email, date_placed, date_paid, date_completed, status, margin_percentage," +
                " (SELECT SUM(cost_price) FROM orderline WHERE orderline.orderr_id=orderr.orderr_id)" +
                " FROM orderr JOIN account USING(account_id) ORDER BY status DESC , date_placed DESC";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("orderr_id");
                int accountId = rs.getInt("account_id");
                String email = rs.getString("email");
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                String status = rs.getString("status");
                double marginPercentage = rs.getDouble("margin_percentage");
                double costPrice = rs.getDouble("sum");
              
                double salePriceInclVAT = SalePriceCalculator.calculateSalePriceInclVAT(costPrice, marginPercentage);
                OverviewOrderAccountDtos.add(new OverviewOrderAccountDto(orderId, accountId, email, datePlaced, datePaid, dateCompleted, salePriceInclVAT, status));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in getOverviewOrderAccountDtos() connection. E message: " + e.getMessage());
            throw new DatabaseException("Fejl til sælger", "Error in getAllOrderAccountDtos()", e.getMessage());
        }
        return OverviewOrderAccountDtos;
    }

    public static boolean createOrder(int accountId, int carportWidth, int carportLength, int carportHeight, ConnectionPool connectionPool) throws OrderException, DatabaseException {
        boolean success = false;

        String sql = "INSERT INTO orderr (account_id, status, carport_length_cm, carport_width_cm, carport_height_cm) " +
                " VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, accountId);
            ps.setString(2, "Henvendelse");
            ps.setInt(3, carportLength);
            ps.setInt(4, carportWidth);
            ps.setInt(5, carportHeight);
//            ps.setInt(6, shedWidth);
//            ps.setInt(7, shedLength);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                success = true;
            } else {
                LOGGER.severe("Error in createOrder() SQL query was not a sucessful execution");
                throw new OrderException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder()");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error in createOrder() connection. E message: " + e.getMessage());
            throw new DatabaseException("Der skete en fejl i at oprette din ordre", "Error in CreateOrder()", e.getMessage());
        }
        return success;
    }

    public static ArrayList<Order> getOrdersFromAccountId(int account_id, ConnectionPool connectionPool) throws OrderException {
        ArrayList<Order> orders = new ArrayList<>();
        String sql = "SELECT orderr_id, date_placed, date_paid, date_completed, status, margin_percentage," +
                     " (SELECT SUM(cost_price) FROM orderline WHERE orderline.orderr_id=orderr.orderr_id)" +
                     " FROM orderr WHERE account_id = ? ORDER BY status DESC , date_placed DESC";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, account_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("orderr_id");
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double costPrice = rs.getDouble("sum");
                double marginPercentage = rs.getDouble("margin_percentage");
                String status = rs.getString("status");

                if(!status.equals("Henvendelse")){
                    double salePriceInclVAT = SalePriceCalculator.calculateSalePriceInclVAT(costPrice, marginPercentage);
                    orders.add(new Order(orderId, datePlaced, datePaid, dateCompleted, salePriceInclVAT, status));
                } else{
                    orders.add(new Order(orderId, datePlaced, datePaid, dateCompleted, status));
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new OrderException("Der skete en fejl i at hente din ordre", "Error happen in showCustomerOrder()", e.getMessage());
        }
    }

    public static Order getOrder(int orderId, ConnectionPool connectionPool) throws OrderException {
        Order order = null;
        String sql = "SELECT orderr_id, date_placed, date_paid, date_completed, status, margin_percentage," +
                " (SELECT SUM(cost_price) FROM orderline WHERE orderline.orderr_id=orderr.orderr_id)" +
                " FROM orderr WHERE account_id = ? ORDER BY status DESC , date_placed DESC";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double costPrice = rs.getDouble("sum");
                double marginPercentage = rs.getDouble("margin_percentage");
                String status = rs.getString("status");

                if(!status.equals("Henvendelse")){
                    double salePriceInclVAT = SalePriceCalculator.calculateSalePriceInclVAT(costPrice, marginPercentage);
                    order = new Order(orderId, datePlaced, datePaid, dateCompleted, salePriceInclVAT, status);
                } else{
                    order = new Order(orderId, datePlaced, datePaid, dateCompleted, status);
                }
            }
            return order;

        } catch (SQLException e) {
            throw new OrderException("Der skete en fejl i at hente din ordre", "Error happen in: showCustomerOrder", e.getMessage());
        }
    }

    public static DetailOrderAccountDto getDetailOrderAccountDtoByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT orderr_id, account_id, email, name, phone, zip_code, city, date_placed, date_paid, date_completed, margin_percentage, status, carport_length_cm, carport_width_cm, carport_height_cm, svg_side_view, svg_top_view FROM orderr JOIN account USING(account_id) JOIN zip_code USING(zip_code) WHERE orderr_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String email = rs.getString("email");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                int zip = rs.getInt("zip_code");
                String city = rs.getString("city");
                Date datePlaced = rs.getDate("date_placed");
                Date datePaid = rs.getDate("date_paid");
                Date dateCompleted = rs.getDate("date_completed");
                double marginPercentage = rs.getDouble("margin_percentage");
                String status = rs.getString("status");
                int carportLengthCm = rs.getInt("carport_length_cm");
                int carportWidthCm = rs.getInt("carport_width_cm");
                int carportHeightCm = rs.getInt("carport_height_cm");
                String svgSideView = rs.getString("svg_side_view");
                String svgTopView = rs.getString("svg_top_view");
                return new DetailOrderAccountDto(orderId, accountId, email, name, phone, zip, city, datePlaced, datePaid, dateCompleted, marginPercentage, status, carportLengthCm, carportWidthCm, carportHeightCm, svgSideView, svgTopView);
            }
            throw new DatabaseException("Fejl ved hentning af ordrenr: " + orderId, "Error in getDetailOrderAccountDtoByOrderId for orderId: " + orderId);
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af ordrenr: " + orderId, "Error in getDetailOrderAccountDtoByOrderId for orderId: " + orderId, e.getMessage());
        }
    }

    public static void updateMarginPercentage(int orderId, double marginPercentage, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "UPDATE orderr SET margin_percentage = ? WHERE orderr_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, marginPercentage);
            ps.setInt(2, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl i at ændre dækningsgrad for ordrenr: " + orderId, "Error in updateMarginPercentage for orderId: " + orderId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i at ændre dækningsgrad for ordrenr: " + orderId, "Error in updateMarginPercentage for orderId: " + orderId, e.getMessage());
        }
    }


    public static void updateCarport(int orderId, int carportWidthCm, int carportLengthCm, int carportHeightCm, String svgSideView, String svgTopView, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "UPDATE orderr SET carport_width_cm=?, carport_length_cm=?, carport_height_cm=?, svg_side_view=?, svg_top_view=? WHERE orderr_id=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, carportWidthCm);
            ps.setInt(2, carportLengthCm);
            ps.setInt(3, carportHeightCm);
            ps.setString(4, svgSideView);
            ps.setString(5, svgTopView);
            ps.setInt(6, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl i at opdatere carport beregning for ordrenr: " + orderId, "Error in updateCarport for orderId: " + orderId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i at opdatere carport beregning for ordrenr: " + orderId, "Error in updateCarport for orderId: " + orderId, e.getMessage());
        }

    }

    public static void updateStatus(int orderId, String status, Boolean isDone, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "UPDATE orderr SET status=?";

        if(isDone){
            sql += ", date_completed=CURRENT_DATE";
        }

        sql += " WHERE orderr_id=?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl i at opdatere status for ordrenr: " + orderId, "Error in updateStatus() in OrderMapper for orderId: " + orderId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl i at opdatere status for ordrenr: " + orderId, "Error in updateStatus() in OrderMapper for orderId: " + orderId, e.getMessage());
        }
    }

    public static void updateIsPaid(int orderId, ConnectionPool connectionPool) throws OrderException {
        String sql = "UPDATE orderr SET paid = true, status = 'Afsluttet' WHERE orderr_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new OrderException("Fejl i køb af ordre");
            }

        } catch (OrderException | SQLException e){
            LOGGER.severe("Error in updateIsPaid() " + e.getMessage());
            throw new OrderException("Fejl i at oprette forbindelse til database");
        }
    }
}