package employees.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.TestPropertySource;

import employees.spring.repository.AccountsRepository;
import employees.spring.security.dto.Account;
import employees.spring.service.AccountServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = { "app.security.admin.password=ppp",
		"app.security.accounts.file.name=test.data", "logging.level.telran=debug" })
public class AccountingServiceTest {

    @Mock
    private ConcurrentHashMap<String, Account> accounts;
    @Mock
    private AccountsRepository accountsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDetailsManager manager;

    @InjectMocks
    private AccountServiceImpl service;
    
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
    	MockitoAnnotations.openMocks(this);
    }

	@AfterAll
	static void deleteFileAfter() throws IOException {
		Files.deleteIfExists(Path.of("test.data"));
	}

	@BeforeAll
	static void deleteFileBefore() throws IOException {
		Files.deleteIfExists(Path.of("test.data"));
	}
    
    @Test
    void testGetAccountSuccess() {
        Account expectedAccount = new Account("testUser", "password", new String[]{"USER"});
        when(accounts.get("testUser")).thenReturn(expectedAccount);
        Account result = service.getAccount("testUser");
        assertEquals(expectedAccount, result);
    }

    @Test
    void testGetAccountNotFound() {
        when(accounts.get("unknownUser")).thenReturn(null);
        assertThrows(NotFoundException.class, () -> service.getAccount("unknownUser"));
    }

    @Test
    void testAddAccountUserExists() {
        Account existingAccount = new Account("existingUser", "password", new String[]{"USER"});
        when(manager.userExists("existingUser")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.addAccount(existingAccount));
    }

    @Test
    void testAddAccountSynchronizationError() {
        Account newAccount = new Account("newUser", "password", new String[]{"USER"});
        when(manager.userExists("newUser")).thenReturn(false);
        when(accounts.containsKey("newUser")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> service.addAccount(newAccount));
    }




  
}