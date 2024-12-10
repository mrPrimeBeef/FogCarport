package app.persistence;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import app.entities.Orderline;
import app.exceptions.DatabaseException;
import app.exceptions.OrderException;
import app.services.StructureCalculationEngine.Entities.Carport;

class OrderlineMapperTest extends AbstractMapperTest {

    @Test
    void getOrderlinesForCustomerOrSalesrep() throws OrderException {
        ArrayList<Orderline> orderlineList = OrderlineMapper.getOrderlinesForCustomerOrSalesrep(2, "Kunde", connectionPool);

        assertNotNull(orderlineList);
        assertEquals(2, orderlineList.size());
        assertNotEquals(1, orderlineList.size());
    }

//    @Test
//    void addOrderlines() throws SQLException, DatabaseException {
//        int carportLengthCm = 752;
//        int carportWidthCm = 600;
//        int carportHeightCm = 210;
//        Carport carport = new Carport(carportWidthCm, carportLengthCm, carportHeightCm, null, false, 0, connectionPool);
//
//        carport.getPlacedMaterials();
//        OrderlineMapper.addOrderlines(carport.getPartsList(), 2, connectionPool);
//    }

//    @Test
//    void deleteOrderlinesFromOrderId() {
//
//    }
}