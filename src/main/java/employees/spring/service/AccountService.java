package employees.spring.service;

import employees.spring.security.dto.Account;

public interface AccountService {
	Account getAccount(String username);

	Account addAccount(Account account);

	public boolean isAccountExist(String username);

}
