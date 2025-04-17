package com.example.exam.controller;

import com.example.exam.common.Result;
import com.example.exam.entity.User;
import com.example.exam.security.JwtTokenProvider;
import com.example.exam.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        // 从SecurityContext获取已认证的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 这里对应用户的phone

        User user = userService.findUserByPhone(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 调用Service层方法
        return userService.changePassword(user.getId(), oldPassword, newPassword);
    }
    @PostMapping("/admin/register")
    public Result adminRegister(@RequestBody User user) {
        return userService.adminRegister(user);
    }
    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody Map<String, String> loginInfo) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        return userService.adminLogin(username, password);
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
        // 从 UserService 中获取 UserDetails
        org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername(username);
        // 这里假设 UserDetails 中的 username 就是 User 中的 phone 字段
        User user = userService.findUserByPhone(username);
        if (user != null) {
            return user.getId();
        }
        return null;
    }
}