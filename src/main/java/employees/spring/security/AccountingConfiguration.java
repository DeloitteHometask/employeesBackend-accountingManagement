package employees.spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AccountingConfiguration {
	@Value("${app.accounts.admin.password}")
	String adminPassword;
	@Value("${app.accounts.admin.username:admin}")
	String adminUsername;

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsManager getUserDetailsService() {
		UserDetailsManager manager = new InMemoryUserDetailsManager();
		log.debug("admin username {}", adminUsername);
		manager.createUser(User.withUsername(adminUsername).password(getPasswordEncoder().encode(adminPassword))
				.roles("ADMIN").build());
		return manager;
	}
}
