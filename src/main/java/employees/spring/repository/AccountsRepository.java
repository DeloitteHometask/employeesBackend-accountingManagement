package employees.spring.repository;

import employees.spring.security.dto.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountsRepository extends MongoRepository<Account, String> {

}