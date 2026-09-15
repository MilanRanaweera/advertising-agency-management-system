package com.sliit.aams.useraccount.controller;



import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.useraccount.dto.UpdateProfileRequest;
import com.sliit.aams.useraccount.model.User;
import com.sliit.aams.useraccount.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserAccountService svc;

    @GetMapping("/me")
    public ApiResponse<User> me(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok(svc.getById(userId));
    }

    @PutMapping("/me")
    public ApiResponse<User> update(@RequestHeader("X-User-Id") Long userId,
                                       @RequestBody UpdateProfileRequest req) {
        return ApiResponse.ok("Profile updated", svc.updateProfile(userId, req));
    }
}