package app.persistence;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;

class AccountMapperTest extends AbstractMapperTest {
    @Test
    void getAllEmailsFromAccount() throws DatabaseException {
        ArrayList<String> emails = new ArrayList<>();

        emails = AccountMapper.getAllAccountEmails(connectionPool);
        assertEquals(3, emails.size());

        String mail = emails.get(0);
        assertEquals("test@test.dk", mail);

        mail = emails.get(1);
        assertNotEquals("test@test.dk", mail);
    }

    @Test
    void getIdFromAccountEmail() throws AccountException {
        int actual = AccountMapper.getAccountIdFromEmail("test@test.dk", connectionPool);
        assertEquals(1, actual);

        actual = AccountMapper.getAccountIdFromEmail("test@test.dk", connectionPool);
        assertNotEquals(0, actual);
    }

    @Test
    void getAllAccounts() throws DatabaseException {
        ArrayList<Account> accounts = AccountMapper.getAllAccounts(connectionPool);
        String actual = accounts.get(2).getName();

        assertEquals("Test Testersen", actual);
        assertEquals(3, accounts.size());

        assertNotEquals("Test Testersen", accounts.get(1).getName());
    }

    @Test
    void createAccount() throws AccountException {
        int actual = AccountMapper.createAccount("String name", "String adress", 2100, "String phone", "String email", connectionPool);
        assertEquals(4, actual);

        actual = AccountMapper.createAccount("String name2", "String adress2", 2100, "String phone2", "String email2", connectionPool);
        assertNotEquals(4, actual);
    }

    @Test
    void login() throws AccountException {
        Account account;
        account = AccountMapper.login("test@test.dk", "1234", connectionPool);
        assertEquals(1, account.getAccountId());
        assertEquals("customer", account.getRole());

        assertNotEquals(2, account.getAccountId());
        assertNotEquals("admin", account.getRole());
    }
}