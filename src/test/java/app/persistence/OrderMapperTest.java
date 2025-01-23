package app.persistence;

import java.util.ArrayList;
import java.time.LocalDate;

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
        boolean actual = OrderMapper.createOrder(1, 200, 200, 200, connectionPool);
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
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);

        DetailOrderAccountDto dto = OrderMapper.getDetailOrderAccountDtoByOrderId(2, connectionPool);
        assertEquals(2, dto.getOrderId());
        assertEquals(1, dto.getAccountId());
        assertEquals("test@test.dk", dto.getEmail());
        assertEquals("Test Testersen", dto.getName());
        assertEquals("12345678", dto.getPhone());
        assertEquals(2100, dto.getZip());
        assertEquals("København Ø", dto.getCity());
        assertEquals(today.toString(), dto.getDatePlaced().toString());
        assertEquals(tomorrow.toString(), dto.getDatePaid().toString());
        assertEquals(dayAfterTomorrow.toString(), dto.getDateCompleted().toString());
        assertEquals(30, Math.round(dto.getMarginPercentage()));
        assertEquals(84, Math.round(dto.getMarginAmount()));
        assertEquals(195, Math.round(dto.getCostPrice()));
        assertEquals(279, Math.round(dto.getSalePrice()));
        assertEquals(348, Math.round(dto.getSalePriceInclVAT()));
        assertEquals("betalt", dto.getStatus());
        assertEquals(580, dto.getCarportLengthCm());
        assertEquals(530, dto.getCarportWidthCm());
        assertEquals(230, dto.getCarportHeightCm());
        assertEquals("<svg>SideView</svg>", dto.getSvgSideView());
        assertEquals("<svg>TopView</svg>", dto.getSvgTopView());

        assertThrows(DatabaseException.class, () -> OrderMapper.getDetailOrderAccountDtoByOrderId(0, connectionPool));
    }

    @Test
    void updateMarginPercentage() throws DatabaseException {
        OrderMapper.updateMarginPercentage(2, 35, connectionPool);
    }

    @Test
    void updateCarport() {

    }
}