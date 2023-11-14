package employees.spring.security;

import java.util.List;

import employees.spring.security.dto.Account;

public interface AccountProvider {
	List<Account> getAccounts();
	void setAccounts(List<Account> accounts) ; 
}
