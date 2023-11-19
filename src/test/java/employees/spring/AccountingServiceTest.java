package employees.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import employees.spring.repository.AccountsRepository;
import employees.spring.security.dto.Account;
import employees.spring.service.AccountServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AccountingServiceTest {

    @Mock
    private AccountsRepository accountsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl service;

    @BeforeEach
    void setUp() {
    }
    
    @Test
    void testGetAccountSuccess() {
        Account expectedAccount = new Account("testUser", "password", new String[]{"USER"});
        when(accountsRepository.findById("testUser")).thenReturn(Optional.of(expectedAccount));
        Account result = service.getAccount("testUser");
        assertEquals(expectedAccount, result);
    }

    @Test
    void testGetAccountNotFound() {
        when(accountsRepository.findById("unknownUser")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getAccount("unknownUser"));
    }

    @Test
    void testAddAccountUserExists() {
        Account existingAccount = new Account("existingUser", "password", new String[]{"USER"});
        when(accountsRepository.findById("existingUser")).thenReturn(Optional.of(existingAccount));
        assertThrows(IllegalStateException.class, () -> service.addAccount(existingAccount));
    }

    @Test
    void testAddAccountSuccess() {
        Account newAccount = new Account("newUser", "password", new String[]{"USER"});
        when(accountsRepository.findById("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newAccount.getPassword())).thenReturn("encodedPassword");
        
        Account expectedAccount = new Account("newUser", "encodedPassword", new String[]{"USER"});
        when(accountsRepository.save(any())).thenReturn(expectedAccount);

        Account result = service.addAccount(newAccount);
        assertEquals(expectedAccount.getUsername(), result.getUsername());
        assertEquals(expectedAccount.getPassword(), result.getPassword());
        assertArrayEquals(expectedAccount.getRoles(), result.getRoles());
    }
}
