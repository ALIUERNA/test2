package com.example.exam.controller;

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
}