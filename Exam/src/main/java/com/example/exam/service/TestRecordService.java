package com.example.exam.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.exam.entity.Question;
import com.example.exam.entity.TestRecord;
import com.example.exam.entity.User;
import com.example.exam.mapper.TestRecordMapper;
import com.example.exam.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestRecordService extends ServiceImpl<TestRecordMapper, TestRecord> {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;
    @Transactional
    public Result submitTest(List<String> answers) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        User user = userService.findUserByPhone(phone);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证答案数量
        List<Question> questions = questionService.getRandomQuestions(answers.size());
        if (questions.size() != answers.size()) {
            return Result.error("答案数量与题目不匹配");
        }

        // 计算得分
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers.get(i).equalsIgnoreCase(questions.get(i).getCorrectAnswer())) {
                correctCount++;
            }
        }
        int wrongCount = questions.size() - correctCount;
        double score = (correctCount * 100.0) / questions.size();

        // 保存考试记录
        TestRecord record = new TestRecord();
        record.setUserId(user.getId()); // 设置 userId
        record.setTestTime(LocalDateTime.now());
        record.setCorrectCount(correctCount);
        record.setWrongCount(wrongCount);
        record.setScore(score);
        this.save(record);

        // 返回结果
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("correctCount", correctCount);
        resultData.put("wrongCount", wrongCount);
        resultData.put("score", score);

        // 返回结果
        return Result.success(resultData);
    }
}
