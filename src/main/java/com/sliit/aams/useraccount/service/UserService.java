package com.sliit.aams.useraccount.service;

import com.sliit.aams.useraccount.model.User;
import com.sliit.aams.useraccount.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Jayalath M.P.M.P.A. (IT25102962)
 * TODO: Implement business logic for the useraccount module use cases.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
