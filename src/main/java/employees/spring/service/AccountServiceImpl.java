package employees.spring.service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import employees.spring.NotFoundException;
import employees.spring.repository.AccountsRepository;
import employees.spring.security.AccountProvider;
import employees.spring.security.dto.Account;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
	
	final PasswordEncoder passwordEncoder;
	final AccountsRepository accountsRepository;
    final AccountProvider provider;
    
	ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
	
	@Autowired
	UserDetailsManager manager;

	@Override
	public Account getAccount(String username) {
		Account res = accounts.get(username);
		if (res == null) {
			throw new NotFoundException(username + " not found");
		}
		return res;
	}

	@Override
	public boolean isAccountExist(String username) {
		return accounts.get(username) != null;
	}

	@Override
	public Account addAccount(Account account) {

		String username = account.getUsername();
		if (manager.userExists(username)) {
			throw new IllegalStateException(String.format("user %s already exists", username));
		}
		if (accounts.containsKey(username)) {
			throw new RuntimeException("error of synchronization between accounts and accounts manager");
		}
		String plainPassword = account.getPassword();
		String passwordHash = passwordEncoder.encode(plainPassword);
		Account user = new Account(username, passwordHash, account.getRoles());

		createUser(user);
		log.debug("created user {}", username);
		accountsRepository.save(user);
		log.debug("user {} was added to database", username);
		return user;

	}

	private void createUser(Account user) {
		accounts.putIfAbsent(user.getUsername(), user);
		manager.createUser(createUserDetails(user));
	}

	private UserDetails createUserDetails(Account account) {
		return User.withUsername(account.getUsername()).password(account.getPassword()).roles(account.getRoles())
				.build();
	}
	
	@PostConstruct
	void restoreAccounts() {
		List<Account> listAccounts = provider.getAccounts();
		listAccounts.forEach(a -> createUser(a));
	}

	@PreDestroy
	void saveAccounts() {
		provider.setAccounts(new LinkedList<>(accounts.values()));
	}
}
