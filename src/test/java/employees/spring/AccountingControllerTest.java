package employees.spring;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import employees.spring.controller.AccountController;
import employees.spring.security.dto.Account;
import employees.spring.service.AccountService;

@SpringBootApplication

class AccountServiceMock implements AccountService {

	 static final String NOT_EXISTED_USERNAME = "user_not_exist";

	@Override
	public Account getAccount(String username) {
		return new Account(username, username, new String[] {"ADMIN"});
	}

	@Override
	public Account addAccount(Account account) {
		return null;
	}
	
	@Override
	public boolean isAccountExist(String username) {
		return true;
	}

	@Override
	public List<Account> getAllAccounts() {
		return null;
	}
}

@WebMvcTest({AccountController.class, AccountServiceMock.class, SecurityConfiguration.class})
@WithMockUser(password = "ddd", username = "admin", roles = {"ADMIN"})
public class AccountingControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	AccountController controller;
	Account account = new Account("user123", "userPass1", new String[] {"ADMIN", "USER"});
	Account accountWrongUsername = new Account("user", "userPass1", new String[] {"ADMIN", "USER"});
	Account accountWrongRoles = new Account("user123", "userPass1", new String[] {});
	Account accountWrongPassword = new Account("user123", "userPass", new String[] {"ADMIN", "USER"});
	@Autowired
	ObjectMapper mapper;
	String baseUrl = "http://localhost:8080/accounts";
    
	@Test
     void loadContext() {
    	 assertNotNull(mockMvc);
    	 assertNotNull(controller);
     }
     @Test
     void getAccountTest() throws Exception {
    	 mockMvc.perform(get(baseUrl + "/get/user")).andDo(print())
    			 .andExpect(status().isOk());
     }
     @Test
     void addAccountNormalFlowTest() throws Exception {
    	 String accountJson = mapper.writeValueAsString(account);
    	 var actions = getRequestBase(accountJson);
    	 actions.andExpect(status().isOk());
    	 
     }
     @Test
     void addAccountUsernameWrongFlowTest() throws Exception {
    	 String accountJson = mapper.writeValueAsString(accountWrongUsername);
    	 var actions = getRequestBase(accountJson);
    	 actions.andExpect(status().isBadRequest());
    	 
     }
     @Test
     void addAccountRolesWrongFlowTest() throws Exception {
    	 String accountJson = mapper.writeValueAsString(accountWrongRoles);
    	 var actions = getRequestBase(accountJson);
    	 actions.andExpect(status().isBadRequest());
     }
    
     private ResultActions getRequestBase(String json) throws Exception {
 		return mockMvc.perform(post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(json))
 		.andDo(print());
 	}
}