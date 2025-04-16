package com.example.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.exam.entity.Question;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

}