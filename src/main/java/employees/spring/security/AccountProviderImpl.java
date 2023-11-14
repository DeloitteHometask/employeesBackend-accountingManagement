package employees.spring.security;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import employees.spring.security.dto.Account;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class AccountProviderImpl implements AccountProvider {
	
 @Value("${app.accounts.file.name:employees-hometask-accounts.data}")
	private String fileName;
 
	@SuppressWarnings("unchecked")
	@Override
	public List<Account> getAccounts() {
		List<Account> res = Collections.emptyList();
		if (Files.exists(Path.of(fileName))) {
			try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(fileName))) {
				res = (List<Account>) stream.readObject();
				log.info("accounts have been restored from the file {}", fileName);
			} catch (Exception e) {
				throw new RuntimeException(String.format
						("error %s during restoring from file %s", e.toString(), fileName));
			} 
		}
		return res;
	}

	@Override
	public void setAccounts(List<Account> accounts) {
		try(ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(fileName))){
			stream.writeObject(accounts);
			log.info(" {} accounts have been saved to the file {}", accounts.size(),fileName);
		}catch (Exception e) {
			throw new RuntimeException(String.format
					("error %s during saving to file %s", e.toString(), fileName));
		}
	}
}


