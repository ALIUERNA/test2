package com.example.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String phone;
    private String idCard;
    private String password;
    private String name;
    private String email;
    private Boolean isActive;
    private String token;
}
