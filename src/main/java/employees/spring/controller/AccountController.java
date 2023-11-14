package employees.spring.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employees.spring.security.dto.Account;
import employees.spring.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
@CrossOrigin
public class AccountController {
	
	final AccountService accountService;

	@GetMapping("get/{username}")
	public Account getAccount(@PathVariable String username) {
		return accountService.getAccount(username);
	}

	@GetMapping("exist/{username}")
	public boolean isAccountExist(@PathVariable String username) {
		return accountService.isAccountExist(username);
	}

	@PostMapping
	public Account addAccount(@RequestBody @Valid Account account) {
		return accountService.addAccount(account);
	}
}