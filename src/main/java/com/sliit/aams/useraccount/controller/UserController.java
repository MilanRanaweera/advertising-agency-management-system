package com.sliit.aams.useraccount.controller;

import com.sliit.aams.useraccount.dto.CreateUserRequest;
import com.sliit.aams.useraccount.dto.UserDto;
import com.sliit.aams.useraccount.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Owner: Jayalath M.P.M.P.A. (IT25102962)
 * REST endpoints for the User Account Management module.
 */
@RestController
@RequestMapping("/api/useraccount")
public class UserController {

    @Autowired
    private UserService userService;

    /** GET /api/useraccount — list all users */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /** GET /api/useraccount/{id} — get user by ID */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /** POST /api/useraccount — create a new user */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDto created = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/useraccount/{id} — update a user */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CreateUserRequest request) {
        try {
            UserDto updated = userService.updateUser(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/useraccount/{id} — delete a user */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/useraccount/search?keyword= — search users */
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }
}
