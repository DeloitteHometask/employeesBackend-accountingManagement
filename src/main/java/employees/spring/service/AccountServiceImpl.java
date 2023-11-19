package employees.spring.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import employees.spring.NotFoundException;
import employees.spring.repository.AccountsRepository;
import employees.spring.security.dto.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
	
	final PasswordEncoder passwordEncoder;
	final AccountsRepository accountsRepository;
	
	@Override
	public List<Account> getAllAccounts() {
		return accountsRepository.findAll();
	}

	@Override
	public Account getAccount(String username) {
		Account res = accountsRepository.findById(username).orElse(null);
		if (res == null) {
			throw new NotFoundException(username + " not found");
		}
		return res;
	}

	@Override
	public boolean isAccountExist(String username) {
		return accountsRepository.findById(username).orElse(null) != null;
	}

	@Override
	public Account addAccount(Account account) {
		String username = account.getUsername();
		if (isAccountExist(username)) {
			throw new IllegalStateException(String.format("user %s already exists", username));
		}
		String plainPassword = account.getPassword();
		String passwordHash = passwordEncoder.encode(plainPassword);
		Account user = new Account(username, passwordHash, account.getRoles());

		accountsRepository.save(user);
		log.debug("user {} was added to database", username);
		return user;

	}
}
