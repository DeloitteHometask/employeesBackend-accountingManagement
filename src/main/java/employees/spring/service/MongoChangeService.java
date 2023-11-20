package employees.spring.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import employees.spring.security.dto.Account;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MongoChangeService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private AccountServiceImpl accountService;

    private final ExecutorService changeStreamExecutor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        changeStreamExecutor.submit(this::subscribeToAccountChanges);
    }
    
    @PreDestroy
    public void preDectroy() {
       shutdown();
    }
    

    private void subscribeToAccountChanges() {
        MongoDatabase database = mongoClient.getDatabase("employees_hometask");
        MongoCollection<Document> collection = database.getCollection("accounts");
        log.debug("subscribed to db: {}, collection length: {}", database.getName(), collection.countDocuments());
        collection.watch().forEach(this::processChange);
    }
    
    private void processChange(ChangeStreamDocument<Document> change) {
        Document document = change.getFullDocument();
        Account account = convertDocumentToAccount(document);
        log.debug("received new account with name{}" , account);
        accountService.updateAccount(account);
    }
    
    private Account convertDocumentToAccount(Document document) {
        String username = document.getString("_id");
        String password = document.getString("password");
        List<String> rolesList = document.getList("roles", String.class);
        String[] roles = rolesList.toArray(new String[0]);

        return new Account(username, password, roles);
    }

    public void shutdown() {
        changeStreamExecutor.shutdown();
    }
}
