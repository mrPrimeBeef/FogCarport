package app.persistence;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;

class AccountMapperTest extends AbstractMapperTest {
    @Test
    void getAllAccountEmails() throws DatabaseException {
        ArrayList<String> emails = AccountMapper.getAllAccountEmails(connectionPool);
        assertEquals(3, emails.size());
        assertEquals("test@test.dk", emails.get(0));
        assertEquals("admin@admin.dk", emails.get(1));
    }

    @Test
    void getAccountIdFromEmail() throws AccountException {
        assertEquals(1, AccountMapper.getAccountIdFromEmail("test@test.dk", connectionPool));
        assertEquals(2, AccountMapper.getAccountIdFromEmail("admin@admin.dk", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.getAccountIdFromEmail("dont@exists.dk", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.getAccountIdFromEmail("", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.getAccountIdFromEmail(null, connectionPool));
    }

    @Test
    void getAllCustomerAccounts() throws AccountException {
        ArrayList<Account> accounts = AccountMapper.getAllCustomerAccounts(connectionPool);
        assertEquals(2, accounts.size());
        assertEquals("Test Testersen", accounts.get(0).getName());
        assertEquals("Kurt Kunde", accounts.get(1).getName());
//        TODO: Also test for all the other attirbutes
    }

    @Test
    void createAccount() throws AccountException {
        int actual = AccountMapper.createAccount("String name", "String address", 2100, "String phone", "String email", connectionPool);
        assertEquals(4, actual);

        actual = AccountMapper.createAccount("String name2", "String address2", 2100, "String phone2", "String email2", connectionPool);
        assertEquals(5, actual);

        // TODO: Use get account to test that the correct things come out (even though I am interesting to test that the correct things come in)
    }

    @Test
    void login() throws AccountException {
        Account account;
        account = AccountMapper.login("test@test.dk", "1234", connectionPool);
        assertEquals(1, account.getAccountId());
        assertEquals("Kunde", account.getRole());
        // TODO: Evt. test for de andre ting der bliver puttet ind i Account objectet for en kunde

        // TODO: Lav nedenstående om så der istedet testes for når der bliver logget ind som sælger
        assertNotEquals(2, account.getAccountId());
        assertNotEquals("admin", account.getRole());
    }

    @Test
    void getAccountByEmail() throws AccountException {
        Account account = AccountMapper.getAccountByEmail("test@test.dk", connectionPool);
        assertEquals(1, account.getAccountId());
        assertEquals("test@test.dk", account.getEmail());
        assertEquals("Kunde", account.getRole());

        account = AccountMapper.getAccountByEmail("admin@admin.dk", connectionPool);
        assertEquals(2, account.getAccountId());
        assertEquals("admin@admin.dk", account.getEmail());
        assertEquals("salesrep", account.getRole());

        account = AccountMapper.getAccountByEmail("dont@exists.dk", connectionPool);
        assertNull(account);

        account = AccountMapper.getAccountByEmail("", connectionPool);
        assertNull(account);

        account = AccountMapper.getAccountByEmail(null, connectionPool);
        assertNull(account);
    }

    @Test
    void getPasswordAndEmail() throws AccountException {
        Account account = AccountMapper.getPasswordAndEmail(1, connectionPool);
        assertEquals("1234", account.getPassword());
        assertEquals("test@test.dk", account.getEmail());

        account = AccountMapper.getPasswordAndEmail(2, connectionPool);
        assertEquals("admin", account.getPassword());
        assertEquals("admin@admin.dk", account.getEmail());

        account = AccountMapper.getPasswordAndEmail(0, connectionPool);
        assertNull(account);
    }

    @Test
    void getPasswordByEmail() throws AccountException {
        Account account = AccountMapper.getPasswordByEmail("test@test.dk", connectionPool);
        assertEquals("1234", account.getPassword());

        account = AccountMapper.getPasswordByEmail("dont@exists.dk", connectionPool);
        assertNull(account);

        account = AccountMapper.getPasswordByEmail("", connectionPool);
        assertNull(account);

        account = AccountMapper.getPasswordByEmail(null, connectionPool);
        assertNull(account);
    }



    @Test
    void updatePassword() throws AccountException {
        // Valid email and valid password
        AccountMapper.updatePassword("test@test.dk", "234", connectionPool);
        Account account = AccountMapper.getPasswordByEmail("test@test.dk", connectionPool);
        assertEquals("234", account.getPassword());

        // Valid email and empty password
        AccountMapper.updatePassword("test@test.dk", "", connectionPool);
        account = AccountMapper.getPasswordByEmail("test@test.dk", connectionPool);
        assertEquals("", account.getPassword());

        // Valid email and null password
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("test@test.dk", null, connectionPool));

        // Valid password and invalid emails
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("dont@exists.dk", "4321", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("", "4321", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword(null, "4321", connectionPool));
    }
}