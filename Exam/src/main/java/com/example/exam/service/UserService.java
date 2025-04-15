package com.example.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.exam.entity.User;
import com.example.exam.mapper.UserMapper;

import com.example.exam.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    public boolean register(User user) {
        // 检查手机号是否已注册
        User existingUser = userMapper.selectOne(
                new QueryWrapper<User>().eq("phone", user.getPhone()));
        if (existingUser != null) {
            return false;
        }

        return userMapper.insert(user) > 0;
    }

    public User login(String phone, String password) {
        User user = userMapper.selectOne(
                new QueryWrapper<User>().eq("phone", phone).eq("password", password));
        return user;
    }

    public boolean changePassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        user.setPassword(newPassword);
        return userMapper.updateById(user) > 0;
    }
}