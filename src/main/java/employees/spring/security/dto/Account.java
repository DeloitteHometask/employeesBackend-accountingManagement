package employees.spring.security.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@Document(collection="accounts")
@TypeAlias("account")
@AllArgsConstructor
public class Account implements Serializable {

	private static final long serialVersionUID = -5361662414272788142L;

	@Id
	@Size(min=5, message="username must be not less than 5 letters")
	final String username;

	@Size(min=8, message="password must be not less than 8 letters")
	final String password;

	@NotEmpty
	final String[] roles;
	
}


