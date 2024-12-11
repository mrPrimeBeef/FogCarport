package app.persistence;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import app.dto.DetailOrderAccountDto;
import app.entities.Order;
import app.dto.OverviewOrderAccountDto;
import app.exceptions.OrderException;
import app.exceptions.DatabaseException;

class OrderMapperTest extends AbstractMapperTest {
    @Test
    void createOrder() throws OrderException, DatabaseException {
        boolean actual = OrderMapper.createOrder(1, 200, 200, 200, 200, connectionPool);
        assertTrue(actual);
    }

    @Test
    void getOverviewOrderAccountDtos() throws DatabaseException {
        ArrayList<OverviewOrderAccountDto> OverviewOrderAccountDtos = OrderMapper.getOverviewOrderAccountDtos(connectionPool);

        assertEquals(2, OverviewOrderAccountDtos.size());
        assertEquals(1, OverviewOrderAccountDtos.get(1).getAccountId());
        assertNotEquals(1, OverviewOrderAccountDtos.size());
    }

    @Test
    void getOrdersFromAccountId() throws OrderException {
        ArrayList<Order> orders = OrderMapper.getOrdersFromAccountId(1, connectionPool);

        assertEquals(2, orders.size());
        assertNotEquals(3, orders.size());
    }

    @Test
    void getOrder() throws OrderException {
        Order order = OrderMapper.getOrder(1, connectionPool);
        String actual = order.getStatus();

        assertNotNull(order);
        assertEquals("betalt", actual);
        assertNotEquals("In progress", order.getStatus());
    }

    @Test
    void getDetailOrderAccountDtoByOrderId() throws DatabaseException {
        DetailOrderAccountDto dto = OrderMapper.getDetailOrderAccountDtoByOrderId(1, connectionPool);
        String actual = dto.getStatus();
        assertNotNull(dto);
        assertEquals("henvendelse", actual);
        assertNotEquals("In progress", dto.getStatus());
    }

    @Test
    void updateMarginPercentage() throws DatabaseException {
        OrderMapper.updateMarginPercentage(2, 35, connectionPool);
    }

    @Test
    void updateCarport() {

    }
}