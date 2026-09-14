package com.sliit.aams.useraccount.controller;

import com.sliit.aams.useraccount.model.User;
import com.sliit.aams.useraccount.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Owner: Jayalath M.P.M.P.A. (IT25102962)
 * REST endpoints for the useraccount module.
 */
@RestController
@RequestMapping("/api/useraccount")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }
}
