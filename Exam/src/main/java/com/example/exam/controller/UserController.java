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

import static com.baomidou.mybatisplus.extension.toolkit.Db.updateById;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findUserByPhone(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return userService.changePassword(user.getId(), oldPassword, newPassword);
    }

    @PostMapping("/admin/register")
    public Result adminRegister(@RequestBody User user) {
        return userService.adminRegister(user);
    }

    @PostMapping("/admin/login")
    public Result adminLogin(@RequestBody Map<String, String> loginInfo) {
        String phone = loginInfo.get("phone"); // 改为接收手机号
        String password = loginInfo.get("password");
        return userService.adminLogin(phone, password); // 传入手机号
    }
    @GetMapping("/activate")
    public Result activateAccount(@RequestParam String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String phone = jwtTokenProvider.getUsernameFromToken(token);
            User user = userService.findUserByPhone(phone);
            if (user != null) {
                // 激活用户
                user.setIsActive(true);
                updateById(user);
                return Result.success("账户激活成功");
            }
        }
        return Result.error("激活链接无效或已过期");
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
        org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername(username);
        User user = userService.findUserByPhone(username);
        if (user != null) {
            return user.getId();
        }
        return null;
    }
}