package com.example.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.exam.common.Result;

import com.example.exam.entity.Question;
import com.example.exam.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/import")
    public Result importQuestions(@RequestParam("file") MultipartFile file) {
        boolean success = questionService.importQuestions(file);
        return success ? Result.success() : Result.error("导入失败");
    }

    @GetMapping("/random")
    public Result getRandomQuestions(@RequestParam int count) {
        List<Question> questions = questionService.getRandomQuestions(count);
        return Result.success(questions);
    }
    @GetMapping("/list")
    public Result getQuestionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<Question> questionPage = questionService.getQuestionList(page, pageSize);
        return Result.success(questionPage);
    }
    // 新增题目
    @PostMapping("/add")
    public Result addQuestion(@RequestBody Question question) {
        boolean success = questionService.save(question);
        return success ? Result.success() : Result.error("添加失败");
    }

    // 修改题目
    @PutMapping("/update")
    public Result updateQuestion(@RequestBody Question question) {
        boolean success = questionService.updateById(question);
        return success ? Result.success() : Result.error("更新失败");
    }

    // 删除题目
    @DeleteMapping("/delete/{id}")
    public Result deleteQuestion(@PathVariable Long id) {
        boolean success = questionService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

}