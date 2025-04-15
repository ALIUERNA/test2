package com.example.exam.controller;

import com.example.exam.common.Result;

import com.example.exam.entity.User;
import com.example.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        boolean success = userService.register(user);
        return success ? Result.success() : Result.error("注册失败");
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getPhone(), user.getPassword());
        if (loggedInUser != null) {
            return Result.success(loggedInUser);
        } else {
            return Result.error("登录失败");
        }
    }

    @PostMapping("/change-password")
    public Result changePassword(@RequestBody User user) {
        boolean success = userService.changePassword(user.getId(), user.getPassword());
        return success ? Result.success() : Result.error("修改密码失败");
    }
}