package com.example.exam.controller;



import com.example.exam.common.Result;
import com.example.exam.service.TestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestRecordController {

    @Autowired
    private TestRecordService testRecordService;

    @PostMapping("/submit")
    public Result submitTest(@RequestBody List<String> answers) {
        return testRecordService.submitTest(answers);
    }
}
