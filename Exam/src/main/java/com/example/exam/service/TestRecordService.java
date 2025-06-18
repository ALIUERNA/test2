package com.example.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.exam.common.Result;
import com.example.exam.entity.Question;
import com.example.exam.entity.TestRecord;
import com.example.exam.entity.User;
import com.example.exam.mapper.TestRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public Result getTestRecordsForCurrentUser() {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        User user = userService.findUserByPhone(phone);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 查询该用户的考试记录
        QueryWrapper<TestRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId())
                .orderByDesc("test_time");
        List<TestRecord> records = this.list(queryWrapper);

        // 计算考试次数
        int examCount = records.size();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("examCount", examCount);

        return Result.success(result);
    }

    @Transactional(readOnly = true)
    public Result getAllTestRecordsForAdmin() {
        // 查询所有考试记录
        List<TestRecord> allRecords = this.list(new QueryWrapper<TestRecord>().orderByDesc("test_time"));

        // 获取所有用户信息
        Map<Long, User> userMap = userService.listUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 按用户分组统计
        Map<Long, List<TestRecord>> recordsByUser = allRecords.stream()
                .collect(Collectors.groupingBy(TestRecord::getUserId));

        // 构建返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, List<TestRecord>> entry : recordsByUser.entrySet()) {
            Long userId = entry.getKey();
            List<TestRecord> records = entry.getValue();
            User user = userMap.get(userId);

            if (user != null) {
                // 计算该用户的平均分和考试次数
                double averageScore = records.stream()
                        .mapToDouble(TestRecord::getScore)
                        .average()
                        .orElse(0.0);

                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", userId);
                userData.put("userName", user.getName());
                userData.put("userPhone", user.getPhone());
                userData.put("examCount", records.size());
                userData.put("averageScore", Math.round(averageScore * 100) / 100.0);
                userData.put("records", records);

                result.add(userData);
            }
        }

        // 按考试次数排序
        result.sort((a, b) ->
                Integer.compare((int) b.get("examCount"), (int) a.get("examCount"))
        );

        return Result.success(result);
    }
}
