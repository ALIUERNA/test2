package com.example.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.exam.entity.Question;
import com.example.exam.mapper.QuestionMapper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService extends ServiceImpl<QuestionMapper, Question> {

    @Autowired
    private QuestionMapper questionMapper;

    public boolean importQuestions(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Question> questions = new ArrayList<>();

            for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Question question = new Question();
                question.setContent(row.getCell(0).getStringCellValue());
                question.setOptionA(row.getCell(1).getStringCellValue());
                question.setOptionB(row.getCell(2).getStringCellValue());
                question.setOptionC(row.getCell(3).getStringCellValue());
                question.setOptionD(row.getCell(4).getStringCellValue());
                question.setCorrectAnswer(row.getCell(5).getStringCellValue());

                questions.add(question);
            }

            return questionMapper.insertBatch(questions) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Question> getRandomQuestions(int count) {
        return questionMapper.selectPage(new Page<>(1, count), null).getRecords();
    }
}