package employees.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class SecurityConfiguration {

	@Bean
	SecurityFilterChain configure(HttpSecurity httpSec) throws Exception {

		return httpSec.csrf(custom -> custom.disable()).cors(custom -> custom.disable())
				.authorizeHttpRequests(
						custom -> custom.requestMatchers(HttpMethod.GET).authenticated().anyRequest().hasRole("ADMIN"))
				.sessionManagement(custom -> custom.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
				.httpBasic(Customizer.withDefaults()).build();
	}
}