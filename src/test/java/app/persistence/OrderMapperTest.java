package app.persistence;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(3, OverviewOrderAccountDtos.size());
        assertEquals(1, OverviewOrderAccountDtos.get(1).getAccountId());
        assertNotEquals(1, OverviewOrderAccountDtos.size());
    }
}