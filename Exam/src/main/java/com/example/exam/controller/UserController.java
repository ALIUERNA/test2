package com.example.exam.controller;

import com.example.exam.common.Result;
import com.example.exam.entity.User;
import com.example.exam.security.JwtTokenProvider;
import com.example.exam.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        return userService.login(user.getPhone(), user.getPassword());
    }

    @PostMapping("/change-password")
    public Result changePassword(
            @RequestHeader("Authorization") String token,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        Long userId = parseUserIdFromToken(token);
        return userService.changePassword(userId, oldPassword, newPassword);
    }

    @PostMapping("/admin/set-privilege")
    @PreAuthorize("hasRole('ADMIN')")
    public Result setAdminPrivilege(
            @RequestParam Long userId,
            @RequestParam Boolean isAdmin
    ) {
        return userService.setAdminPrivilege(userId, isAdmin);
    }

    private Long parseUserIdFromToken(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        return 1L; // 示例值
    }
}