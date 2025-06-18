package com.example.exam.controller;



import com.example.exam.common.Result;
import com.example.exam.service.TestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    // TestRecordController.java
    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result getTestRecords() {
        return testRecordService.getTestRecordsForCurrentUser();
    }

    @GetMapping("/admin/all-records")
    @PreAuthorize("hasRole('ADMIN')")
    public Result getAllTestRecords() {
        return testRecordService.getAllTestRecordsForAdmin();
    }
}
