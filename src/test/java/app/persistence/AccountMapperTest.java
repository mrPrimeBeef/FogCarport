package app.persistence;

import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest extends AbstractMapperTest {
    @Test
    void getAllAccountEmails() throws DatabaseException {
        ArrayList<String> emails = AccountMapper.getAllAccountEmails(connectionPool);
        assertEquals(3, emails.size());
        assertTrue(emails.contains("test@test.dk"));
        assertTrue(emails.contains("admin@admin.dk"));
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

        assertEquals("test@test.dk", accounts.get(0).getEmail());
        assertEquals("Test Testersen", accounts.get(0).getName());
        assertEquals("Testvej 1", accounts.get(0).getAddress());
        assertEquals(2100, accounts.get(0).getZip());
        assertEquals("København Ø", accounts.get(0).getCity());
        assertEquals("12345678", accounts.get(0).getPhone());

        assertEquals("kunde@kunde.dk", accounts.get(1).getEmail());
        assertEquals("Kurt Kunde", accounts.get(1).getName());
        assertEquals("Kundevej 123", accounts.get(1).getAddress());
        assertEquals(2300, accounts.get(1).getZip());
        assertEquals("København S", accounts.get(1).getCity());
        assertEquals("11223344", accounts.get(1).getPhone());
    }

    @Test
    void createAccount() throws AccountException, DatabaseException {
        int accountId = AccountMapper.createAccount("String name", "String address", 2100, "String phone", "String email", connectionPool);
        assertEquals(4, accountId);
        Account account = AccountMapper.getAccountByEmail("String email", connectionPool);
        assertEquals(4, account.getAccountId());
        assertEquals("String email", account.getEmail());
        assertEquals("Kunde", account.getRole());
        assertEquals(4, AccountMapper.getAllAccountEmails(connectionPool).size());

        accountId = AccountMapper.createAccount("String name2", "String address2", 2200, "String phone2", "String email2", connectionPool);
        assertEquals(5, accountId);
        account = AccountMapper.getAccountByEmail("String email2", connectionPool);
        assertEquals(5, account.getAccountId());
        assertEquals("String email2", account.getEmail());
        assertEquals("Kunde", account.getRole());
        assertEquals(5, AccountMapper.getAllAccountEmails(connectionPool).size());

        // TODO: There should be some tests with: Too long email, too long password, email that allready exists
    }

    @Test
    void login() throws AccountException {
        // Login as customer
        Account account = AccountMapper.login("test@test.dk", "1234", connectionPool);
        assertEquals(1, account.getAccountId());
        assertEquals("test@test.dk", account.getEmail());
        assertEquals("Test Testersen", account.getName());
        assertEquals("Kunde", account.getRole());
        assertEquals("Testvej 1", account.getAddress());
        assertEquals("København Ø", account.getCity());
        assertEquals("12345678", account.getPhone());
        assertEquals(0, account.getZip());
        assertEquals(null, account.getPassword());

        // Login as salesrep
        account = AccountMapper.login("admin@admin.dk", "admin", connectionPool);
        assertEquals(2, account.getAccountId());
        assertEquals(null, account.getEmail());
        assertEquals(null, account.getName());
        assertEquals("salesrep", account.getRole());
        assertEquals(null, account.getAddress());
        assertEquals(null, account.getCity());
        assertEquals(null, account.getPhone());
        assertEquals(0, account.getZip());
        assertEquals(null, account.getPassword());

        // Login with valid email and invalid password
        assertNull(AccountMapper.login("test@test.dk", "WrongPassword", connectionPool));
        assertNull(AccountMapper.login("test@test.dk", "", connectionPool));
        assertNull(AccountMapper.login("test@test.dk", null, connectionPool));

        // Login with invalid email and valid password
        assertNull(AccountMapper.login("dont@exists.dk", "1234", connectionPool));
        assertNull(AccountMapper.login("", "1234", connectionPool));
        assertNull(AccountMapper.login(null, "1234", connectionPool));
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

        assertNull(AccountMapper.getAccountByEmail("dont@exists.dk", connectionPool));
        assertNull(AccountMapper.getAccountByEmail("", connectionPool));
        assertNull(AccountMapper.getAccountByEmail(null, connectionPool));
    }

    @Test
    void getPasswordAndEmail() throws AccountException {
        Account account = AccountMapper.getPasswordAndEmail(1, connectionPool);
        assertEquals("1234", account.getPassword());
        assertEquals("test@test.dk", account.getEmail());

        account = AccountMapper.getPasswordAndEmail(2, connectionPool);
        assertEquals("admin", account.getPassword());
        assertEquals("admin@admin.dk", account.getEmail());

        assertNull(AccountMapper.getPasswordAndEmail(0, connectionPool));
    }

    @Test
    void getPasswordByEmail() throws AccountException {
        Account account = AccountMapper.getPasswordByEmail("test@test.dk", connectionPool);
        assertEquals("1234", account.getPassword());

        assertNull(AccountMapper.getPasswordByEmail("dont@exists.dk", connectionPool));
        assertNull(AccountMapper.getPasswordByEmail("", connectionPool));
        assertNull(AccountMapper.getPasswordByEmail(null, connectionPool));
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

        // Invalid email and valid password
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("dont@exists.dk", "4321", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("", "4321", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword(null, "4321", connectionPool));
    }
}