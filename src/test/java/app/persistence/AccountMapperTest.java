package app.persistence;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import app.entities.Account;
import app.exceptions.AccountException;
import app.exceptions.DatabaseException;

class AccountMapperTest extends AbstractMapperTest {

    final String tooLongString = "This_string_is_65_chars_long____Too_long_for_emails_and_passwords";

    @Test
    void getAllAccountEmails() throws DatabaseException {
        ArrayList<String> emails = AccountMapper.getAllAccountEmails(connectionPool);
        assertEquals(3, emails.size());
        assertTrue(emails.contains("test@test.dk"));
        assertTrue(emails.contains("admin@admin.dk"));
    }

    @Test
    void getAccountIdFromEmail() throws AccountException {
        // Valid emails
        assertEquals(1, AccountMapper.getAccountIdFromEmail("test@test.dk", connectionPool));
        assertEquals(2, AccountMapper.getAccountIdFromEmail("admin@admin.dk", connectionPool));

        // Invalid emails
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
        assertEquals(null, accounts.get(0).getPassword());

        assertEquals("kunde@kunde.dk", accounts.get(1).getEmail());
        assertEquals("Kurt Kunde", accounts.get(1).getName());
        assertEquals("Kundevej 123", accounts.get(1).getAddress());
        assertEquals(2300, accounts.get(1).getZip());
        assertEquals("København S", accounts.get(1).getCity());
        assertEquals("11223344", accounts.get(1).getPhone());
        assertEquals(null, accounts.get(1).getPassword());
    }

    @Test
    void createAccount() throws AccountException, DatabaseException {
        // Create a valid account
        int accountId = AccountMapper.createAccount("String name", "String address", 2100, "String phone", "String email", connectionPool);
        assertEquals(4, accountId);
        Account account = AccountMapper.getAccountByEmail("String email", connectionPool);
        assertEquals(4, account.getAccountId());
        assertEquals("String email", account.getEmail());
        assertEquals("Kunde", account.getRole());
        assertEquals(4, AccountMapper.getAllAccountEmails(connectionPool).size());

        // Create another valid account
        accountId = AccountMapper.createAccount("String name2", "String address2", 2200, "String phone2", "String email2", connectionPool);
        assertEquals(5, accountId);
        account = AccountMapper.getAccountByEmail("String email2", connectionPool);
        assertEquals(5, account.getAccountId());
        assertEquals("String email2", account.getEmail());
        assertEquals("Kunde", account.getRole());
        assertEquals(5, AccountMapper.getAllAccountEmails(connectionPool).size());

        // Create accounts with invalid emails
        String emailUsedByOtherAccount = "test@test.dk";
        assertThrows(AccountException.class, () -> AccountMapper.createAccount("String name", "String address", 2100, "String phone", emailUsedByOtherAccount, connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.createAccount("String name", "String address", 2100, "String phone", tooLongString, connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.createAccount("String name", "String address", 2100, "String phone", null, connectionPool));
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

        // Login with valid email and invalid passwords
        assertNull(AccountMapper.login("test@test.dk", "WrongPassword", connectionPool));
        assertNull(AccountMapper.login("test@test.dk", "", connectionPool));
        assertNull(AccountMapper.login("test@test.dk", null, connectionPool));

        // Login with invalid emails and valid password
        assertNull(AccountMapper.login("dont@exists.dk", "1234", connectionPool));
        assertNull(AccountMapper.login("", "1234", connectionPool));
        assertNull(AccountMapper.login(null, "1234", connectionPool));
    }

    @Test
    void getAccountByEmail() throws AccountException {
        // Valid email
        Account account = AccountMapper.getAccountByEmail("test@test.dk", connectionPool);
        assertEquals(1, account.getAccountId());
        assertEquals("test@test.dk", account.getEmail());
        assertEquals("Kunde", account.getRole());
        assertEquals(null, account.getPassword());

        // Another valid email
        account = AccountMapper.getAccountByEmail("admin@admin.dk", connectionPool);
        assertEquals(2, account.getAccountId());
        assertEquals("admin@admin.dk", account.getEmail());
        assertEquals("salesrep", account.getRole());
        assertEquals(null, account.getPassword());

        // Invalid emails
        assertNull(AccountMapper.getAccountByEmail("dont@exists.dk", connectionPool));
        assertNull(AccountMapper.getAccountByEmail("", connectionPool));
        assertNull(AccountMapper.getAccountByEmail(null, connectionPool));
    }

    @Test
    void getPasswordAndEmail() throws AccountException {
        // Valid accountId
        Account account = AccountMapper.getPasswordAndEmail(1, connectionPool);
        assertEquals("1234", account.getPassword());
        assertEquals("test@test.dk", account.getEmail());

        // Another valid accountId
        account = AccountMapper.getPasswordAndEmail(2, connectionPool);
        assertEquals("admin", account.getPassword());
        assertEquals("admin@admin.dk", account.getEmail());

        // Invalid accountId
        assertNull(AccountMapper.getPasswordAndEmail(0, connectionPool));
    }

    @Test
    void getPasswordByEmail() throws AccountException {
        // Valid email
        Account account = AccountMapper.getPasswordByEmail("test@test.dk", connectionPool);
        assertEquals("1234", account.getPassword());

        // Another valid email
        account = AccountMapper.getPasswordByEmail("admin@admin.dk", connectionPool);
        assertEquals("admin", account.getPassword());

        // Invalid emails
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

        // Valid email and invalid passwords
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("test@test.dk", tooLongString, connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("test@test.dk", null, connectionPool));

        // Invalid emails and valid password
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("dont@exists.dk", "4321", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword("", "4321", connectionPool));
        assertThrows(AccountException.class, () -> AccountMapper.updatePassword(null, "4321", connectionPool));
    }
}