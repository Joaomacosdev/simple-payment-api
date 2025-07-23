package com.simplepaymentapi.service;

import com.simplepaymentapi.domain.user.User;
import com.simplepaymentapi.domain.user.UserType;
import com.simplepaymentapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Usuario do tipo lojista não esta autorizado a realizar transação");
        }

        if (sender.getBalance().compareTo(amount) <= 0) {
            throw new Exception("Saldo insuficiente");
        }
    }

    public User findUserById(Long id) throws Exception {
        return this.userRepository.findUserById(id).orElseThrow(() -> new Exception("Usuario não encontrado"));
    }

    public void saveUser(User user){
        this.userRepository.save(user);
    }
}
