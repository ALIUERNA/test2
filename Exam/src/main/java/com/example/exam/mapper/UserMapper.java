package com.example.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.exam.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
