package com.simplepaymentapi.service;

import com.simplepaymentapi.domain.transaction.Transaction;
import com.simplepaymentapi.domain.user.User;
import com.simplepaymentapi.dtos.TransactionDTO;
import com.simplepaymentapi.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserService userService, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    public void createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorize = this.authorizeTransaction(sender, transaction.value());

        if (this.authorizeTransaction(sender, transaction.value())){
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().subtract(transaction.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
        if (authorizationResponse.getStatusCode() == HttpStatus.OK && authorizationResponse.getBody().get("message") == "Autorizado"){
            String message = (String) authorizationResponse.getBody().get("message");
            return "Autorizado".equalsIgnoreCase(message);
        } else return false;
    }
}
