package app.persistence;

import app.entities.Orderline;
import app.exceptions.OrderException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderlineMapperTest extends AbstractMapperTest {

    @Test
    void getOrderlinesForCustomerOrSalesrep() throws OrderException {
        ArrayList<Orderline> orderlineList = OrderlineMapper.getOrderlinesForCustomerOrSalesrep(1,"Kunde",connectionPool);

        assertNotNull(orderlineList);
        assertEquals(2,orderlineList.size());
    }

    @Test
    void addOrderlines() {
    }

    @Test
    void deleteOrderlinesFromOrderId() {
    }
}