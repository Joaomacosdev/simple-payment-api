package com.simplepaymentapi.repository;

import com.simplepaymentapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByDocument(String document);
    Optional<User> findUserById(Long id);


}
